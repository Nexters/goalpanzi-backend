package com.nexters.goalpanzi.application.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record MissionVerificationResponse(
        @Schema(description = "image url", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String imageUrl
) {
}
