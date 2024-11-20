package com.nexters.goalpanzi.presentation.member.dto;

import com.nexters.goalpanzi.application.member.dto.request.UpdatePushActivationStatusCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdatePushActivationStatusRequest(
        @Schema(description = "푸시 알림 활성화 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Boolean pushActivationStatus
) {

    public UpdatePushActivationStatusCommand toServiceDto(final Long memberId) {
        return new UpdatePushActivationStatusCommand(memberId, pushActivationStatus);
    }
}
