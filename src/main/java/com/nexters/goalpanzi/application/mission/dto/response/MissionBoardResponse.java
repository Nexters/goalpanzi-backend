package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.application.member.dto.response.ProfileResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.EventItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

public record MissionBoardResponse(
        @Schema(description = "보드칸 번호", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer number,
        @Schema(description = "보드칸 이벤트", requiredMode = Schema.RequiredMode.REQUIRED)
        String eventItem,
        List<ProfileResponse> missionMembers
) {

    public static MissionBoardResponse from(final Integer number, final List<Member> members) {
        return new MissionBoardResponse(
                number,
                EventItem.of(number),
                members.stream().
                        map(m -> new ProfileResponse(m.getNickname(), m.getCharacterType()))
                        .collect(Collectors.toList())
        );
    }
}
