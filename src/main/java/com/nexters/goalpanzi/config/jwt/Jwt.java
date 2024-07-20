package com.nexters.goalpanzi.config.jwt;

import lombok.Builder;

@Builder
public record Jwt(String accessToken, String refreshToken, Long refreshExpiresIn) {
}
