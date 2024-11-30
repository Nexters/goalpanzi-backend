package com.nexters.goalpanzi.application.mission.event.handler;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.member.event.DeleteMemberEvent;
import com.nexters.goalpanzi.application.member.event.UpdateDeviceTokenEvent;
import com.nexters.goalpanzi.application.member.event.UpdatePushActivationStatusEvent;
import com.nexters.goalpanzi.application.mission.MissionMemberService;
import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.application.mission.event.CreateMissionEvent;
import com.nexters.goalpanzi.application.mission.event.DeleteMissionEvent;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_DELETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionMemberEventHandler {

    private final MissionMemberService missionMemberService;
    private final MissionVerificationService missionVerificationService;

    private final PushNotificationSender pushNotificationSender;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handleCreateMissionEvent(final CreateMissionEvent event) {
        missionMemberService.joinMission(event.memberId(), new InvitationCode(event.invitationCode()));
        log.info("Handled CreateMissionEvent for memberId: {}", event.memberId());
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
        String topic = TopicGenerator.getTopic(event.missionId());
        pushNotificationSender.sendGroupNotification(
                MISSION_DELETED.getTitle(),
                MISSION_DELETED.getBody(),
                topic
        );
        log.info("Handled DeleteMissionEvent for missionId: {}", event.missionId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUpdateDeviceTokenEvent(final UpdateDeviceTokenEvent event) {
        if (event.deprecatedDeviceToken() != null) {
            missionMemberService.unsubscribeFromMyMissions(event.memberId(), event.deprecatedDeviceToken());
        }
        missionMemberService.subscribeToMyMissions(event.memberId(), event.deviceToken());
        log.info("Handled UpdateDeviceTokenEvent for memberId: {}", event.memberId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUpdatePushActivationStatusEvent(final UpdatePushActivationStatusEvent event) {
        if (event.isPushActivated()) {
            missionMemberService.subscribeToMyMissions(event.memberId(), event.deviceToken());
        } else {
            missionMemberService.unsubscribeFromMyMissions(event.memberId(), event.deviceToken());
        }
        log.info("Handled UpdatePushActivationStatusEvent for memberId: {}", event.memberId());
    }
}
