package com.nexters.goalpanzi.config.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Jwt {
    private final String accessToken;
    private final String refreshToken;
    private final long refreshExpiresIn;

    @Builder
    public Jwt(String accessToken, String refreshToken, long refreshExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshExpiresIn = refreshExpiresIn;
    }
}
