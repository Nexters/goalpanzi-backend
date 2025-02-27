package com.nexters.goalpanzi.application.mission.event.handler;

import com.nexters.goalpanzi.application.device.DeviceSubscriptionService;
import com.nexters.goalpanzi.application.firebase.Topic;
import com.nexters.goalpanzi.application.member.event.DeleteMemberEvent;
import com.nexters.goalpanzi.application.mission.MissionMemberService;
import com.nexters.goalpanzi.application.mission.MissionRetryPushMessageService;
import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.application.mission.event.*;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_DELETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionMemberEventHandler {

    private final MissionMemberService missionMemberService;
    private final MissionVerificationService missionVerificationService;
    private final MissionRetryPushMessageService missionRetryPushMessageService;
    private final DeviceSubscriptionService deviceSubscriptionService;

    private final PushMessageProxy pushMessageProxy;

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

        String topic = Topic.generate(event.missionId());
        Map<String, String> data = new HashMap<>();
        data.put("missionId", event.missionId().toString());

        deviceSubscriptionService.unsubscribeFromDeletedMissionForHost(event.memberId(), event.missionId());
        pushMessageProxy.sendGroupNotificationWithData(
                MISSION_DELETED.getTitle(),
                MISSION_DELETED.getBody(),
                data,
                topic
        );
        log.info("Handled DeleteMissionEvent for missionId: {}", event.missionId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleReserveMissionRetryPushMessageEvent(final ReserveMissionRetryPushMessageEvent event) {
        missionRetryPushMessageService.reserveRetryPushMessage(event.memberId(), event.deviceToken());
        log.info("Handled ReserveMissionRetryPushMessageEvent for memberId: {}", event.memberId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCancelMissionRetryPushMessageEvent(final CancelMissionRetryPushMessageEvent event) {
        missionRetryPushMessageService.cancelRetryPushMessage(event.memberId());
        log.info("Handled CancelMissionRetryPushMessageEvent for memberId: {}", event.memberId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleUpdateMissionRetryPushMessageEvent(final UpdateMissionRetryPushMessageEvent event) {
        missionRetryPushMessageService.updateRetryPushMessage(event.memberId(), event.deviceToken());
        log.info("Handled UpdateMissionRetryPushMessageEvent for memberId: {}", event.memberId());
    }
}
