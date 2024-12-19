package com.nexters.goalpanzi.presentation.device.dto;

import com.nexters.goalpanzi.application.device.dto.request.UpdatePushActivationStatusCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdatePushActivationStatusRequest(
        @Schema(description = "디바이스 식별자", requiredMode = Schema.RequiredMode.REQUIRED)
        String deviceIdentifier,
        @Schema(description = "푸시 알림 활성화 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Boolean pushActivationStatus
) {

    public UpdatePushActivationStatusCommand toServiceDto(final Long memberId) {
        return new UpdatePushActivationStatusCommand(memberId, deviceIdentifier, pushActivationStatus);
    }
}
