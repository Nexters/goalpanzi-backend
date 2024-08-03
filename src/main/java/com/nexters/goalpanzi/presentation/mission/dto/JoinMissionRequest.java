package com.nexters.goalpanzi.presentation.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record JoinMissionRequest(
        @Schema(description = "초대코드 4자리", requiredMode = Schema.RequiredMode.REQUIRED)
        String invitationCode
) {
}
