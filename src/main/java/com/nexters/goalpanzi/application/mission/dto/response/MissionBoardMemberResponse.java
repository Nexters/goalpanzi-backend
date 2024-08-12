package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.member.CharacterType;
import com.nexters.goalpanzi.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record MissionBoardMemberResponse(
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        String nickname,
        @Schema(description = "캐릭터 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        CharacterType characterType
) {

    public static MissionBoardMemberResponse from(final Member member) {
        return new MissionBoardMemberResponse(member.getNickname(), member.getCharacterType());
    }
}
