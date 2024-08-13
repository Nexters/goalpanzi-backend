package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.ForbiddenException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class MissionMembers {
    private final List<MissionMember> missionMembers;

    public Integer getProgressCount() {
        return missionMembers.stream()
                .filter(missionMember ->
                        missionMember.getUpdatedAt().toLocalDate().equals(LocalDate.now()))
                .toList().size();
    }

    public List<MissionMember> getMissionMembersByBoardNumber(final Integer boardNumber) {
        return missionMembers.stream()
                .filter(missionMember -> missionMember.getVerificationCount().equals(boardNumber))
                .toList();
    }

    public void verifyMissionMember(final Member member) {
        missionMembers.stream()
                .filter(missionMember -> missionMember.getMember().equals(member))
                .findAny()
                .orElseThrow(() -> new ForbiddenException(ErrorCode.NOT_JOINED_MISSION_MEMBER));
    }
}
