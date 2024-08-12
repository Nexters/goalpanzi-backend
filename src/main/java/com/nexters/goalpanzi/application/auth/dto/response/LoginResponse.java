package com.nexters.goalpanzi.application.auth.dto.response;

import com.nexters.goalpanzi.common.auth.jwt.Jwt;
import com.nexters.goalpanzi.domain.member.CharacterType;
import com.nexters.goalpanzi.domain.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginResponse(
        @Schema(description = "access token", requiredMode = Schema.RequiredMode.REQUIRED)
        String accessToken,
        @Schema(description = "refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken,
        @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        String nickname,
        @Schema(description = "장기말 타입", requiredMode = Schema.RequiredMode.REQUIRED)
        CharacterType characterType,
        @Schema(description = "프로필 설정 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isProfileSet,
        @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long memberId
) {

    public static LoginResponse of(Member member, Jwt jwt) {
        return LoginResponse.builder()
                .accessToken(jwt.accessToken())
                .refreshToken(jwt.refreshToken())
                .nickname(member.getNickname())
                .characterType(member.getCharacterType())
                .isProfileSet(member.isProfileSet())
                .memberId(member.getId())
                .build();
    }
}
