package com.nexters.goalpanzi.application.mission.event.handler;

import com.nexters.goalpanzi.application.member.event.DeleteMemberEvent;
import com.nexters.goalpanzi.application.mission.MissionMemberService;
import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MissionMemberEventHandler {
    private final MissionMemberService missionMemberService;
    private final MissionVerificationService missionVerificationService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handleJoinMissionEvent(final JoinMissionEvent event) {
        missionMemberService.joinMission(event.memberId(), new InvitationCode(event.invitationCode()));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handelDeleteMemberEvent(final DeleteMemberEvent event) {
        missionMemberService.deleteAllByMemberId(event.memberId());
        missionVerificationService.deleteAllByMemberId(event.memberId());
    }
}
