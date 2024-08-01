package com.nexters.goalpanzi.application.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MissionResponse(
        @Schema(description = "미션 ID")
        Long missionId,
        @Schema(description = "목표 행동")
        String description
) {
}
