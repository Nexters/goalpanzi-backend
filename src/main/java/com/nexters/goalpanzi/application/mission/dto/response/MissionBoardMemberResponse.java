package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.member.CharacterType;
import com.nexters.goalpanzi.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record MissionBoardMemberResponse(
        @Schema(description = "멤버 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        Long memberId,
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        String nickname,
        @Schema(description = "캐릭터 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        CharacterType characterType
) {

    public static MissionBoardMemberResponse from(final Member member) {
        return new MissionBoardMemberResponse(member.getId(), member.getNickname(), member.getCharacterType());
    }
}
