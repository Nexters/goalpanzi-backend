package com.nexters.goalpanzi.application.mission.handler;

import com.nexters.goalpanzi.application.member.event.DeleteMemberEvent;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MissionMemberEventHandler {

    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationRepository missionVerificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    void handelDeleteMemberEvent(final DeleteMemberEvent event) {
        missionMemberRepository.findAllByMemberId(event.memberId())
                .forEach(BaseEntity::delete);
        missionVerificationRepository.findByMemberId(event.memberId())
                .forEach(BaseEntity::delete);
    }
}
