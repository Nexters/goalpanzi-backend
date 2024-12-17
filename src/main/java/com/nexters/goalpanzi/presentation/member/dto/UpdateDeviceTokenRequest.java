package com.nexters.goalpanzi.presentation.member.dto;

import com.nexters.goalpanzi.application.member.dto.request.UpdateDeviceTokenCommand;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateDeviceTokenRequest(
        @Schema(description = "디바이스 식별자", requiredMode = Schema.RequiredMode.REQUIRED)
        String deviceIdentifier,
        @Schema(description = "device token", requiredMode = Schema.RequiredMode.REQUIRED)
        String deviceToken
) {

    public UpdateDeviceTokenCommand toServiceDto(final Long memberId) {
        return new UpdateDeviceTokenCommand(memberId, this.deviceIdentifier, this.deviceToken);
    }
}
