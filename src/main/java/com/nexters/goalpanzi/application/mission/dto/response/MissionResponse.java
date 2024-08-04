package com.nexters.goalpanzi.application.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MissionResponse(
        @Schema(description = "미션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long missionId,
        @Schema(description = "목표 행동", requiredMode = Schema.RequiredMode.REQUIRED)
        String description
) {
}