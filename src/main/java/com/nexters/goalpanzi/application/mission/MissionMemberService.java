package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionMemberService {

    private final MissionMemberRepository missionMemberRepository;
    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;

    public MissionsResponse findAllByMemberId(final Long memberId) {
        List<MissionMember> missionMembers = missionMemberRepository.findAllByMemberId(memberId);
        Member member = getMember(memberId);
        return MissionsResponse.of(member, missionMembers);
    }

    @Transactional
    public void joinMission(final Long memberId, final String invitationCode) {
        Member member = getMember(memberId);
        Mission mission = getMission(invitationCode);

        missionMemberRepository.save(MissionMember.join(member, mission));
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private Mission getMission(final String invitationCode) {
        return missionRepository.findByInvitationCode(new InvitationCode(invitationCode))
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
