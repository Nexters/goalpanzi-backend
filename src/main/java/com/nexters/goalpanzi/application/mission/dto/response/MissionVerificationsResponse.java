package com.nexters.goalpanzi.application.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MissionVerificationsResponse(
        @Schema(description = "미션 인증 정보 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MissionVerificationResponse> missionVerifications
) {
}
