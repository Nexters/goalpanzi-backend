package com.nexters.goalpanzi.application.firebase;

import com.nexters.goalpanzi.domain.firebase.DeviceSubscription;
import com.nexters.goalpanzi.domain.firebase.Devices;
import com.nexters.goalpanzi.domain.firebase.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.firebase.repository.DeviceSubscriptionRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DeviceSubscriptionService {

    private final DeviceRepository deviceRepository;
    private final DeviceSubscriptionRepository deviceSubscriptionRepository;

    private final TopicSubscriber topicSubscriber;

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
}
