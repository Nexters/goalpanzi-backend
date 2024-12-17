package com.nexters.goalpanzi.application.device;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.mission.event.CancelMissionRetryPushMessageEvent;
import com.nexters.goalpanzi.application.mission.event.ReserveMissionRetryPushMessageEvent;
import com.nexters.goalpanzi.application.mission.event.UnsubscribeFromMissionEvent;
import com.nexters.goalpanzi.application.mission.event.UpdateMissionRetryPushMessageEvent;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.DeviceSubscription;
import com.nexters.goalpanzi.domain.device.Devices;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.device.repository.DeviceSubscriptionRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionStatus;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.nexters.goalpanzi.domain.mission.MissionStatus.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DeviceSubscriptionService {

    private final DeviceRepository deviceRepository;
    private final DeviceSubscriptionRepository deviceSubscriptionRepository;
    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final TopicSubscriber topicSubscriber;

    private static List<MissionStatus> SUBSCRIBABLE_MISSION_STATUS
            = List.of(CREATED, IN_PROGRESS, PENDING_COMPLETION);

    @Transactional
    public void subscribeToMission(final Long memberId, final Mission mission) {
        Devices devices = new Devices(
                deviceRepository.findAllByMemberId(memberId)
        );

        devices.getActivatedDevices()
                .forEach(device ->
                        deviceSubscriptionRepository.save(new DeviceSubscription(device, mission))
                );
        topicSubscriber.subscribeToTopic(
                devices.getActivatedDeviceTokens(), TopicGenerator.getTopic(mission.getId())
        );
    }

    @Transactional
    public void unsubscribeFromMission(final Long missionId) {
        List<String> deviceTokens = findTopicSubscribers(missionId);

        deviceSubscriptionRepository.deleteAllByMissionId(missionId);
        topicSubscriber.unsubscribeFromTopic(deviceTokens, TopicGenerator.getTopic(missionId));
    }

    private List<String> findTopicSubscribers(final Long missionId) {
        List<DeviceSubscription> subscriptions = deviceSubscriptionRepository.findAllWithDeviceAndMissionByMissionId(missionId);

        return subscriptions.stream()
                .map(it -> it.getDevice().getDeviceToken())
                .toList();
    }

    @Transactional
    public void subscribeToMyMissions(final Long memberId, final Long deviceId) {
        Device device = deviceRepository.getDevice(deviceId);
        List<String> topics = findMySubscribedTopics(deviceId);
        List<Mission> missions = missionRepository.findAllById(
                findMySubscribableMission(memberId, topics)
        );

        topics.forEach(topic ->
                topicSubscriber.subscribeToTopic(List.of(device.getDeviceToken()), topic)
        );
        missions.forEach(mission -> {
            deviceSubscriptionRepository.save(new DeviceSubscription(device, mission));

            String topic = TopicGenerator.getTopic(mission.getId());
            topicSubscriber.subscribeToTopic(List.of(device.getDeviceToken()), topic);
        });
        eventPublisher.publishEvent(
                new UpdateMissionRetryPushMessageEvent(memberId, device.getDeviceToken())
        );
    }

    @Transactional
    public void unsubscribeFromMyMissions(final Long memberId, final Long deviceId, final String deprecatedDeviceToken) {
        List<String> topics = findMySubscribedTopics(deviceId);

        topics.forEach(topic ->
                topicSubscriber.unsubscribeFromTopic(List.of(deprecatedDeviceToken), topic)
        );
        eventPublisher.publishEvent(
                new CancelMissionRetryPushMessageEvent(memberId)
        );
    }

    private List<String> findMySubscribedTopics(final Long deviceId) {
        List<DeviceSubscription> subscriptions = deviceSubscriptionRepository.findAllWithMissionAndDeviceByDeviceId(deviceId);

        return subscriptions.stream()
                .map(it -> TopicGenerator.getTopic(it.getMission().getId()))
                .toList();
    }

    private List<Long> findMySubscribableMission(final Long memberId, List<String> topicFilter) {
        List<MissionMember> missionMembers = missionMemberRepository.findAllWithMissionByMemberId(memberId);
        List<MissionMember> filteredMissionMembers = missionMembers.stream()
                .filter(this::isSubscribableMission)
                .filter(it -> isAlreadySubscribedMission(topicFilter, it.getMission().getId()))
                .toList();

        return filteredMissionMembers.stream()
                .map(it -> it.getMission().getId())
                .collect(Collectors.toList());
    }

    private boolean isSubscribableMission(final MissionMember missionMember) {
        return SUBSCRIBABLE_MISSION_STATUS.contains(missionMember.getMissionStatus());
    }

    private boolean isAlreadySubscribedMission(List<String> filter, final Long missionId) {
        return filter.contains(TopicGenerator.getTopic(missionId));
    }

    @Transactional
    public void unsubscribeFromUselessMissions() {
        List<Mission> missions = missionRepository.getInProgressMissions();

        missions.forEach(mission -> {
            List<MissionMember> missionMembers = missionMemberRepository.findAllWithMemberByMissionId(mission.getId());

            if (isCancelledMission(missionMembers) || isCompletedMission(missionMembers)) {
                eventPublisher.publishEvent(
                        new UnsubscribeFromMissionEvent(mission.getId())
                );
                if (isCompletedMission(missionMembers)) {
                    missionMembers.forEach(missionMember ->
                            reserveRetryPushMessageForMember(missionMember.getMember())
                    );
                }
            }
        });
    }

    private boolean isCancelledMission(final List<MissionMember> missionMembers) {
        return missionMembers.stream()
                .anyMatch(it -> it.getMissionStatus().equals(CANCELED));
    }

    private boolean isCompletedMission(final List<MissionMember> missionMembers) {
        return missionMembers.stream()
                .anyMatch(it -> it.getMissionStatus().equals(COMPLETED));
    }

    private void reserveRetryPushMessageForMember(final Member member) {
        Devices devices = new Devices(
                deviceRepository.findAllByMemberId(member.getId())
        );

        devices.getActivatedDeviceTokens()
                .forEach(deviceToken ->
                        eventPublisher.publishEvent(
                                new ReserveMissionRetryPushMessageEvent(member.getId(), deviceToken)
                        )
                );
    }
}
