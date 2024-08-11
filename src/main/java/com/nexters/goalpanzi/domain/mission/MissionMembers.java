package com.nexters.goalpanzi.domain.mission;

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
                .filter(it -> it.getVerificationCount().equals(boardNumber))
                .toList();
    }
}
