package com.nexters.goalpanzi.presentation.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CompleteMissionRequest(
        @NotNull
        @Schema(description = "미션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long missionId
) {

}
