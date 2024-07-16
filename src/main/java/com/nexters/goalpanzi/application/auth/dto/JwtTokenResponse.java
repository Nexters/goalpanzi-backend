package com.nexters.goalpanzi.application.auth.dto;

public record JwtTokenResponse(
        String accessToken,
        String refreshToken
) {
}
