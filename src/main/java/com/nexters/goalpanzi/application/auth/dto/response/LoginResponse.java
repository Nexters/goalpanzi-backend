package com.nexters.goalpanzi.application.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "access token", requiredMode = Schema.RequiredMode.REQUIRED)
        String accessToken,
        @Schema(description = "refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken,
        @Schema(description = "프로필 설정 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isProfileSet
) {
}
