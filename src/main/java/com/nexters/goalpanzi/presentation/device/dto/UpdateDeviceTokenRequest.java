package com.nexters.goalpanzi.presentation.device.dto;

import com.nexters.goalpanzi.application.device.dto.request.UpdateDeviceTokenCommand;
import com.nexters.goalpanzi.domain.device.OsType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateDeviceTokenRequest(
        @Schema(description = "디바이스 식별자", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String deviceIdentifier,
        @Schema(description = "device token", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String deviceToken,
        @Schema(description = "디바이스 운영체제", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull OsType osType
) {

    public UpdateDeviceTokenCommand toServiceDto(final Long memberId) {
        return new UpdateDeviceTokenCommand(memberId, deviceIdentifier, deviceToken, osType);
    }
}
