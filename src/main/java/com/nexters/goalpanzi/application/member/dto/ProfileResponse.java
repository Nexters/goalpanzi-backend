package com.nexters.goalpanzi.application.member.dto;

import com.nexters.goalpanzi.domain.member.CharacterType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProfileResponse(
        @Schema(description = "닉네임")
        String nickname,
        @Schema(description = "캐릭터 타입")
        CharacterType characterType
) {
}
