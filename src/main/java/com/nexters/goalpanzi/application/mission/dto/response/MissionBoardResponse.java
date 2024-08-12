package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Reward;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record MissionBoardResponse(
        @Schema(description = "보드칸 번호", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer number,
        @Schema(description = "보드칸 보상", requiredMode = Schema.RequiredMode.REQUIRED)
        Reward reward,
        @Schema(description = "내 장기말 존재 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isMyPosition,
        @Schema(description = "해당 보드칸에 존재하는 장기말", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MissionBoardMemberResponse> missionBoardMembers
) {

    public static MissionBoardResponse of(final Long memberId, final Integer number, final List<Member> members) {
        return new MissionBoardResponse(
                number,
                Reward.of(number),
                isMyPosition(memberId, members),
                members.stream()
                        .map(MissionBoardMemberResponse::from)
                        .collect(Collectors.toList())
        );
    }

    private static boolean isMyPosition(final Long memberId, final List<Member> members) {
        return !members.stream().filter(member -> Objects.equals(member.getId(), memberId)).toList().isEmpty();
    }
}
