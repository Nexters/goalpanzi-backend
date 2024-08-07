package com.nexters.goalpanzi.application.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record AppleLoginCommand(
        @Schema(description = "애플 ID 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String identityToken
) {
}
