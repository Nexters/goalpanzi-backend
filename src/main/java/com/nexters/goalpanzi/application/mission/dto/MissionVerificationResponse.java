package com.nexters.goalpanzi.application.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record MissionVerificationResponse(
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String nickname,

        // TODO 캐릭터 이미지

        @Schema(description = "인증 이미지 URL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String imageUrl
) {
}
