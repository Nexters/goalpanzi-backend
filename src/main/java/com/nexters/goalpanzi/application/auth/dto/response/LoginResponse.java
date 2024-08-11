package com.nexters.goalpanzi.application.auth.dto.response;

import com.nexters.goalpanzi.common.auth.jwt.Jwt;
import com.nexters.goalpanzi.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "access token", requiredMode = Schema.RequiredMode.REQUIRED)
        String accessToken,
        @Schema(description = "refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken,
        @Schema(description = "프로필 설정 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isProfileSet,
        @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long memberId
) {
    public static LoginResponse of(Member member, Jwt jwt) {
        return new LoginResponse(jwt.accessToken(), jwt.refreshToken(), member.isProfileSet(), member.getId());
    }
}
