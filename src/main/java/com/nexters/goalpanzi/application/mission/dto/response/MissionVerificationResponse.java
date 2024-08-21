package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.member.CharacterType;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.MissionVerificationView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.Optional;

public record MissionVerificationResponse(
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty String nickname,
        @Schema(description = "장기말 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty CharacterType characterType,
        @Schema(description = "미션 인증 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        Long missionVerificationId,
        @Schema(description = "인증 이미지 URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String imageUrl,
        @Schema(description = "인증 시간", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        LocalDateTime verifiedAt,
        @Schema(description = "조회 시간", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        LocalDateTime viewedAt
) {

    public static MissionVerificationResponse of(final Member member, final Optional<MissionVerification> missionVerification, final Optional<MissionVerificationView> missionVerificationView) {
        LocalDateTime viewedAt = getViewedAt(missionVerificationView);
        return missionVerification
                .map(v -> verified(member, v, viewedAt))
                .orElseGet(() -> notVerified(member, viewedAt));
    }

    public static MissionVerificationResponse verified(final Member member, final MissionVerification missionVerification, final LocalDateTime viewedAt) {
        return new MissionVerificationResponse(member.getNickname(), member.getCharacterType(), missionVerification.getId(), missionVerification.getImageUrl(), missionVerification.getCreatedAt(), viewedAt);
    }

    public static MissionVerificationResponse notVerified(final Member member, final LocalDateTime viewedAt) {
        return new MissionVerificationResponse(member.getNickname(), member.getCharacterType(), null, "", null, viewedAt);
    }

    private static LocalDateTime getViewedAt(final Optional<MissionVerificationView> missionVerificationView) {
        return missionVerificationView.map(MissionVerificationView::getCreatedAt).orElse(null);
    }

}
