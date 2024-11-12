package com.nexters.goalpanzi.application.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record GoogleLoginCommand(
        @Schema(description = "이메일", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String email,
        @Schema(description = "deviceToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String deviceToken
) {
}