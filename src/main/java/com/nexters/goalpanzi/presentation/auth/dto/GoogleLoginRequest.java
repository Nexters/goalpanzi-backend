package com.nexters.goalpanzi.presentation.auth.dto;

import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record GoogleLoginRequest(
        @Schema(description = "구글 이메일", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String email,
        @Schema(description = "디바이스 식별자", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String deviceIdentifier
) {

    public GoogleLoginCommand toServiceDto() {
        return new GoogleLoginCommand(email, deviceIdentifier);
    }
}
