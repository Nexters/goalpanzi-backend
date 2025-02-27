package com.nexters.goalpanzi.application.firebase.event.handler;

import com.nexters.goalpanzi.application.firebase.Topic;
import com.nexters.goalpanzi.application.mission.event.CompleteMissionEvent;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_COMPLETED;
import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_JOINED;

@Slf4j
@RequiredArgsConstructor
@Component
public class PushMessageEventHandler {

    private final PushMessageProxy pushMessageProxy;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleJoinMissionEvent(final JoinMissionEvent event) {
        Map<String, String> data = new HashMap<>();
        data.put("missionId", event.missionId().toString());

        pushMessageProxy.sendIndividualNotificationWithData(
                MISSION_JOINED.getTitle(),
                MISSION_JOINED.getBody(event.nickname()),
                data,
                event.deviceToken()
        );
        log.info("Handled JoinMissionEvent for missionId: {}", event.missionId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCompleteMissionEvent(final CompleteMissionEvent event) {
        String topic = Topic.generate(event.missionId());
        Map<String, String> data = new HashMap<>();
        data.put("missionId", event.missionId().toString());

        pushMessageProxy.sendGroupNotificationWithData(
                MISSION_COMPLETED.getTitle(),
                MISSION_COMPLETED.getBody(),
                data,
                topic
        );
        log.info("Handled CompleteMissionEvent for missionId: {}", event.missionId());
    }
}
