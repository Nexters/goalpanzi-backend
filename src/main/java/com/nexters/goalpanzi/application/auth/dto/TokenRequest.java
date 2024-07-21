package com.nexters.goalpanzi.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record TokenRequest(
        @Schema(description = "refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String refreshToken
) {
}
