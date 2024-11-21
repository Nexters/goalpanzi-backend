package com.nexters.goalpanzi.presentation.auth.dto;

import com.nexters.goalpanzi.application.auth.dto.request.AppleLoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record AppleLoginRequest(
        @Schema(description = "애플 ID 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String identityToken
) {

    public AppleLoginCommand toServiceDto() {
        return new AppleLoginCommand(identityToken);
    }
}
