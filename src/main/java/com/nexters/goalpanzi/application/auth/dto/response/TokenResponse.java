package com.nexters.goalpanzi.application.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record TokenResponse(
        @Schema(description = "access token", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String accessToken,
        @Schema(description = "refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String refreshToken
) {
}
