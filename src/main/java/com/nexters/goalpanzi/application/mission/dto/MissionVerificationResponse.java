package com.nexters.goalpanzi.application.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDateTime;

public record MissionVerificationResponse(
        @Schema(description = "미션 인증 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Boolean isVerified,

        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String nickname,

        // TODO 캐릭터 이미지

        @Schema(description = "인증 이미지 URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String imageUrl,

        @Schema(description = "인증 시간", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        LocalDateTime verifiedAt
) {
}
