package com.nexters.goalpanzi.application.device.event.handler;

import com.nexters.goalpanzi.application.auth.event.LoginEvent;
import com.nexters.goalpanzi.application.device.DeviceSubscriptionService;
import com.nexters.goalpanzi.application.device.event.UpdateDeviceTokenEvent;
import com.nexters.goalpanzi.application.device.event.UpdatePushActivationStatusEvent;
import com.nexters.goalpanzi.application.mission.event.SubscribeToMissionEvent;
import com.nexters.goalpanzi.application.mission.event.UnsubscribeFromMissionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceSubscriptionEventHandler {

    private final DeviceSubscriptionService deviceSubscriptionService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUpdateDeviceTokenEvent(final UpdateDeviceTokenEvent event) {
        if (event.isTokenDeprecated()) {
            deviceSubscriptionService.unsubscribeFromMyMissions(event.memberId(), event.deviceId(), event.deprecatedDeviceToken());
        }
        deviceSubscriptionService.subscribeToMyMissions(event.memberId(), event.deviceId());
        log.info("Handled UpdateDeviceTokenEvent for memberId: {}", event.memberId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUpdatePushActivationStatusEvent(final UpdatePushActivationStatusEvent event) {
        if (event.isPushActivated()) {
            deviceSubscriptionService.subscribeToMyMissions(event.memberId(), event.deviceId());
        } else {
            deviceSubscriptionService.unsubscribeFromMyMissions(event.memberId(), event.deviceId(), event.deviceToken());
        }
        log.info("Handled UpdatePushActivationStatusEvent for memberId: {}", event.memberId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLoginEvent(final LoginEvent event) {
        deviceSubscriptionService.unsubscribeFromMyMissions(event.memberId(), event.deviceIdentifier());
        deviceSubscriptionService.subscribeToMyMissions(event.memberId(), event.deviceIdentifier());
        log.info("Handled LoginEvent for memberId: {}", event.memberId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleSubscribeMissionEvent(final SubscribeToMissionEvent event) {
        deviceSubscriptionService.subscribeToMission(event.memberId(), event.mission());
        log.info("Handled SubscribeMissionEvent for memberId: {} and missionId: {}", event.memberId(), event.mission().getId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUnsubscribeFromMissionEvent(final UnsubscribeFromMissionEvent event) {
        deviceSubscriptionService.unsubscribeFromMission(event.missionId());
        log.info("Handled UnsubscribeMissionEvent from missionId: {}", event.missionId());
    }
}
