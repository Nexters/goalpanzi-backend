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

    private static final List<MissionStatus> SUBSCRIBABLE_MISSION_STATUS
            = List.of(CREATED, IN_PROGRESS, PENDING_COMPLETION);

    /**
     * <b>새로운 미션 참여 시 미션 구독 시작</b><br>
     * 멤버의 디바이스 중 푸시가 활성화된 디바이스를 대상으로 미션 구독
     *
     * @param memberId 멤버 아이디
     * @param mission  새롭게 참여한 미션
     */
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

    /**
     * <b>미션 취소/종료 시 미션 구독 취소</b><br>
     * 해당 미션을 구독한 디바이스를 대상으로 구독 취소
     *
     * @param missionId 취소/종료된 미션 아이디
     */
    @Transactional
    public void unsubscribeFromMission(final Long missionId) {
        List<String> deviceTokens = findTopicSubscribers(missionId);

        deviceSubscriptionRepository.deleteAllByMissionId(missionId);
        topicSubscriber.unsubscribeFromTopic(deviceTokens, TopicGenerator.getTopic(missionId));
    }

    /**
     * <b>미션 삭제 시 호스트의 미션 구독 취소</b><br>
     * 삭제 푸시 알림을 보내기 전, 호스트는 구독을 취소하여 푸시 알림이 가지 않도록 처리
     *
     * @param memberId  호스트 멤버 아이디
     * @param missionId 호스트가 삭제한 미션 아이디
     */
    @Transactional
    public void unsubscribeFromDeletedMissionForHost(final Long memberId, final Long missionId) {
        String topic = TopicGenerator.getTopic(missionId);
        Devices devices = new Devices(
                deviceRepository.findAllByMemberId(memberId)
        );

        deviceSubscriptionRepository.findAllWithDeviceByMissionIdAndDeviceIds(missionId, devices.getActivatedDeviceIds())
                .forEach(it -> {
                    topicSubscriber.unsubscribeFromTopic(List.of(it.getDevice().getDeviceToken()), topic);
                    deviceSubscriptionRepository.deleteById(it.getId());
                });
    }

    private List<String> findTopicSubscribers(final Long missionId) {
        List<DeviceSubscription> subscriptions = deviceSubscriptionRepository.findAllWithDeviceAndMissionByMissionId(missionId);

        return subscriptions.stream()
                .map(it -> it.getDevice().getDeviceToken())
                .toList();
    }

    /**
     * <b>로그인 시 내 미션 구독 시작</b>
     *
     * @param memberId         멤버 아이디
     * @param deviceIdentifier 디바이스 식별자
     */
    @Transactional
    public void subscribeToMyMissions(final Long memberId, final String deviceIdentifier) {
        Device device = deviceRepository.findByMemberIdAndDeviceIdentifier(memberId, deviceIdentifier)
                .orElse(null);
        if (device == null) {
            return;
        }

        List<String> topics = findMySubscribedTopics(device.getId());
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
    }

    /**
     * <b>디바이스 토큰을 갱신하거나 푸시 알림 활성화 시 새로운 디바이스 토큰으로 내 미션 구독 시작</b><br>
     * + UpdateMissionRetryPushMessageEvent를 통해 예약된 메시지의 디바이스 토큰 갱신
     *
     * @param memberId 멤버 아이디
     * @param deviceId 디바이스 아이디
     */
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

    /**
     * <b>로그인 시 기존 디바이스가 구독한 미션 구독 취소</b>
     * + CancelMissionRetryPushMessageEvent를 통해 예약된 메시지 취소
     *
     * @param memberId         (현재 로그인한) 멤버 아이디
     * @param deviceIdentifier 디바이스 식별자
     */
    @Transactional
    public void unsubscribeFromMyMissions(final Long memberId, final String deviceIdentifier) {
        Devices devices = new Devices(
                deviceRepository.findAllWithMemberByDeviceIdentifier(deviceIdentifier)
        );
        List<String> topics = devices.getActivatedDevices().stream()
                .flatMap(device -> findMySubscribedTopics(device.getId()).stream())
                .toList();

        topics.forEach(topic ->
                topicSubscriber.unsubscribeFromTopic(devices.getActivatedDeviceTokens(), topic)
        );

        devices.getFilteredMemberIds(memberId)
                .forEach(it ->
                        eventPublisher.publishEvent(
                                new CancelMissionRetryPushMessageEvent(it)
                        )
                );
    }

    /**
     * <b>디바이스 토큰을 갱신하거나 푸시 비활성화 시 기존 디바이스 토큰으로 구독한 미션 구독 취소</b><br>
     * + CancelMissionRetryPushMessageEvent를 통해 예약된 메시지 취소
     *
     * @param memberId    멤버 아이디
     * @param deviceId    디바이스 아이디
     * @param deviceToken 기존 디바이스 토큰
     */
    @Transactional
    public void unsubscribeFromMyMissions(final Long memberId, final Long deviceId, final String deviceToken) {
        List<String> topics = findMySubscribedTopics(deviceId);

        topics.forEach(topic ->
                topicSubscriber.unsubscribeFromTopic(List.of(deviceToken), topic)
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
                .filter(it -> !isAlreadySubscribedMission(topicFilter, it.getMission().getId()))
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

    /**
     * <b>취소/완료 상태의 미션을 찾아 이벤트 게시</b><br>
     * - 취소/완료 상태 : UnsubscribeFromMissionEvent를 게시하여 미션 구독 취소<br>
     * - 완료 상태 : ReserveMissionRetryPushMessageEvent를 게시하여 메시지 예약
     */
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
