package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDateTime;

public record MissionVerificationResponse(
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String nickname,

        // TODO 캐릭터 이미지

        @Schema(description = "인증 이미지 URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String imageUrl,

        @Schema(description = "인증 시간", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        LocalDateTime verifiedAt
) {

    public static MissionVerificationResponse verified(Member member, MissionVerification verification) {
        return new MissionVerificationResponse(member.getNickname(), verification.getImageUrl(), verification.getCreatedAt());
    }

    public static MissionVerificationResponse notVerified(Member member) {
        return new MissionVerificationResponse(member.getNickname(), "", null);
    }
}
