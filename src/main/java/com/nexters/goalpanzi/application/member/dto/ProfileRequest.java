package com.nexters.goalpanzi.application.member.dto;

import com.nexters.goalpanzi.domain.member.CharacterType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProfileRequest(
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull String nickname,
        @Schema(description = "캐릭터 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull CharacterType characterType
) {
}
