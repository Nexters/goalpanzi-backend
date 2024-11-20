package com.nexters.goalpanzi.presentation.member.dto;

import com.nexters.goalpanzi.application.member.dto.request.UpdateDeviceTokenCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateDeviceTokenRequest(
        @Schema(description = "device token", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull String deviceToken
) {

    public UpdateDeviceTokenCommand toServiceDto(final Long memberId) {
        return new UpdateDeviceTokenCommand(memberId, this.deviceToken);
    }
}
