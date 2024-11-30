package com.nexters.goalpanzi.application.firebase.event.handler;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.mission.event.CompleteMissionEvent;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_COMPLETED;
import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_JOINED;

@Slf4j
@RequiredArgsConstructor
@Component
public class PushNotificationEventHandler {

    private final PushNotificationSender pushNotificationSender;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleJoinMissionEvent(final JoinMissionEvent event) {
        pushNotificationSender.sendIndividualNotification(
                MISSION_JOINED.getTitle(),
                MISSION_JOINED.getBody(event.nickname()),
                event.deviceToken()
        );
        log.info("Handled JoinMissionEvent for missionId: {}", event.missionId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCompleteMissionEvent(final CompleteMissionEvent event) {
        String topic = TopicGenerator.getTopic(event.missionId());
        pushNotificationSender.sendGroupNotification(
                MISSION_COMPLETED.getTitle(),
                MISSION_COMPLETED.getBody(),
                topic
        );
        log.info("Handled CompleteMissionEvent for missionId: {}", event.missionId());
    }
}
