package com.nexters.goalpanzi.presentation.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ViewMissionVerificationRequest(
        @NotNull
        @Schema(description = "미션 인증 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
        Long missionVerificationId
) {
}
