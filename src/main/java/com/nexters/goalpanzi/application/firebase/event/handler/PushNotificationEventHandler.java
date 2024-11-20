package com.nexters.goalpanzi.application.firebase.event.handler;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.mission.event.CompleteMissionEvent;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_COMPLETED;
import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_JOINED;

@RequiredArgsConstructor
@Component
public class PushNotificationEventHandler {

    private final PushNotificationSender pushNotificationSender;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleJoinMissionEvent(final JoinMissionEvent event) {
        pushNotificationSender.sendIndividualMessage(
                MISSION_JOINED.getTitle(),
                MISSION_JOINED.getBody(event.nickname()),
                event.deviceToken()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCompleteMissionEvent(final CompleteMissionEvent event) {
        String topic = TopicGenerator.getTopic(event.missionId());
        pushNotificationSender.sendGroupMessage(
                MISSION_COMPLETED.getTitle(),
                MISSION_COMPLETED.getBody(),
                topic
        );
    }
}
