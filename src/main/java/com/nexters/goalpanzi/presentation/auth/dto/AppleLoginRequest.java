package com.nexters.goalpanzi.presentation.auth.dto;

import com.nexters.goalpanzi.application.auth.dto.request.AppleLoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record AppleLoginRequest(
        @Schema(description = "애플 ID 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String identityToken,
        // TODO: 추후 NotEmpty 변경
        @Schema(description = "디바이스 식별자", requiredMode = Schema.RequiredMode.REQUIRED)
        String deviceIdentifier
) {

    public AppleLoginCommand toServiceDto() {
        // TODO: 추후 삭제
        if (deviceIdentifier == null) {
            return new AppleLoginCommand(identityToken, "");
        }
        return new AppleLoginCommand(identityToken, deviceIdentifier);
    }
}
