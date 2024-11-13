package com.nexters.goalpanzi.presentation.auth.dto;

import com.nexters.goalpanzi.application.auth.dto.request.ReissueTokenCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record ReissueTokenRequest(
        @Schema(description = "refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String refreshToken
) {

    public ReissueTokenCommand toServiceDto(final Long memberId) {
        return new ReissueTokenCommand(memberId, this.refreshToken);
    }
}
