package com.nexters.goalpanzi.application.mission.event.handler;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.member.event.DeleteMemberEvent;
import com.nexters.goalpanzi.application.mission.MissionMemberService;
import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.application.mission.event.CompleteMissionEvent;
import com.nexters.goalpanzi.application.mission.event.CreateMissionEvent;
import com.nexters.goalpanzi.application.mission.event.DeleteMissionEvent;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import static com.nexters.goalpanzi.application.firebase.PushNotificationMessage.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionMemberEventHandler {
    private final MissionMemberService missionMemberService;
    private final MissionVerificationService missionVerificationService;

    private final PushNotificationSender pushNotificationSender;
    private final TopicSubscriber topicSubscriber;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handleCreateMissionEvent(final CreateMissionEvent event) {
        missionMemberService.joinMission(event.memberId(), new InvitationCode(event.invitationCode()));
        log.info("Handled JoinMissionEvent for memberId: {}", event.memberId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleDeleteMemberEvent(final DeleteMemberEvent event) {
        missionMemberService.deleteAllByMemberId(event.memberId());
        missionVerificationService.deleteAllByMemberId(event.memberId());
        log.info("Handled DeleteMemberEvent for memberId: {}", event.memberId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleDeleteMissionEvent(final DeleteMissionEvent event) {
        missionMemberService.deleteAllByMissionId(event.missionId());
        missionVerificationService.deleteAllByMissionId(event.missionId());
        
        pushNotificationSender.sendGroupMessage(
                MISSION_DELETED.getTitle(),
                MISSION_DELETED.getBody(),
                TopicGenerator.getTopic(event.missionId())
        );

        log.info("Handled DeleteMissionEvent for missionId: {}", event.missionId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleJoinMissionEvent(final JoinMissionEvent event) {
        String topic = TopicGenerator.getTopic(event.missionId());
        pushNotificationSender.sendGroupMessage(
                MISSION_JOINED.getTitle(),
                MISSION_JOINED.getBody(event.nickname()),
                topic
        );
        topicSubscriber.subscribeToTopic(List.of(event.deviceToken()), topic);

        log.info("Handled JoinMissionEvent for missionId: {}", event.missionId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCompleteMissionEvent(final CompleteMissionEvent event) {
        pushNotificationSender.sendGroupMessage(
                MISSION_COMPLETED.getTitle(),
                MISSION_COMPLETED.getBody(),
                TopicGenerator.getTopic(event.missionId())
        );

        log.info("Handled CompleteMissionEvent for missionId: {}", event.missionId());
    }
}
