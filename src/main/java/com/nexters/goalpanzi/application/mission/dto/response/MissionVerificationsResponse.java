package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.mission.MissionVerification;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

public record MissionVerificationsResponse(
        @Schema(description = "미션 인증 정보 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
        List<MissionVerificationResponse> missionVerifications
) {

    public static MissionVerificationsResponse from(final List<MissionVerification> verifications) {
        return new MissionVerificationsResponse(
                verifications.stream()
                        .map(verification -> MissionVerificationResponse.of(verification.getMember(), Optional.of(verification)))
                        .toList()
        );
    }
}
