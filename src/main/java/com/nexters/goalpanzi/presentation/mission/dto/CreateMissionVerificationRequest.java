package com.nexters.goalpanzi.presentation.mission.dto;

import com.nexters.goalpanzi.application.mission.dto.CreateMissionVerificationCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record CreateMissionVerificationRequest(
        @Schema(description = "image url", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String imageUrl
) {

    public CreateMissionVerificationCommand toServiceDto(final Long memberId, final Long missionId) {
        return new CreateMissionVerificationCommand(memberId, missionId, imageUrl);
    }
}
