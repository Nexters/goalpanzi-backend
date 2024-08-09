package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Reward;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

public record MissionBoardResponse(
        @Schema(description = "보드칸 번호", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer number,
        @Schema(description = "보드칸 보상", requiredMode = Schema.RequiredMode.REQUIRED)
        Reward reward,
        List<MissionBoardMemberResponse> missionBoardMembers
) {

    public static MissionBoardResponse of(final Integer number, final List<Member> members) {
        return new MissionBoardResponse(
                number,
                Reward.of(number),
                members.stream().
                        map(m -> new MissionBoardMemberResponse(m.getNickname(), m.getCharacterType()))
                        .collect(Collectors.toList())
        );
    }
}
