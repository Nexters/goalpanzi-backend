package com.nexters.goalpanzi.presentation.auth.dto;

import com.nexters.goalpanzi.application.auth.dto.request.AppleLoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record AppleLoginRequest(
        @Schema(description = "애플 ID 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String identityToken,
        // 이전 버전 지원을 위해 @NotEmpty 붙이지 않음
        @Schema(description = "디바이스 식별자", requiredMode = Schema.RequiredMode.REQUIRED)
        String deviceIdentifier
) {

    public AppleLoginCommand toServiceDto() {
        if (deviceIdentifier == null) {
            return new AppleLoginCommand(identityToken, "");
        }
        return new AppleLoginCommand(identityToken, deviceIdentifier);
    }
}
