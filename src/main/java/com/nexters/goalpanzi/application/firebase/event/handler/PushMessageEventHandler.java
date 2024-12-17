package com.nexters.goalpanzi.application.firebase.event.handler;

import com.nexters.goalpanzi.application.firebase.DeviceSubscriptionService;
import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.mission.event.CompleteMissionEvent;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.application.mission.event.SubscribeToMissionEvent;
import com.nexters.goalpanzi.application.mission.event.UnsubscribeFromMissionEvent;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_COMPLETED;
import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_JOINED;

@Slf4j
@RequiredArgsConstructor
@Component
public class PushMessageEventHandler {

    private final DeviceSubscriptionService deviceSubscriptionService;

    private final PushMessageSender pushMessageSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleJoinMissionEvent(final JoinMissionEvent event) {
        pushMessageSender.sendIndividualData(
                MISSION_JOINED.getTitle(),
                MISSION_JOINED.getBody(event.nickname()),
                event.deviceToken(),
                event.missionId()
        );
        log.info("Handled JoinMissionEvent for missionId: {}", event.missionId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCompleteMissionEvent(final CompleteMissionEvent event) {
        String topic = TopicGenerator.getTopic(event.missionId());
        pushMessageSender.sendGroupData(
                MISSION_COMPLETED.getTitle(),
                MISSION_COMPLETED.getBody(),
                topic,
                event.missionId()
        );
        log.info("Handled CompleteMissionEvent for missionId: {}", event.missionId());
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
