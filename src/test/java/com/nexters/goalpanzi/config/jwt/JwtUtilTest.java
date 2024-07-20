package com.nexters.goalpanzi.config.jwt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtUtilTest {
    private static final String secret = "test-secret";
    private static final long accessExpiresIn = 86400000;
    private static final long refreshExpiresIn = 1209600000;

    private final JwtUtil jwtUtil = new JwtUtil(secret, accessExpiresIn, refreshExpiresIn);

    @Test
    void generateTokens() {
        String subject = "test-subject";

        Jwt jwt = jwtUtil.generateTokens(subject);

        assertThat(jwt).isNotNull();
    }

    @Test
    void validateToken() {
        String subject = "test-subject";

        Jwt jwt = jwtUtil.generateTokens(subject);
        Boolean result1 = jwtUtil.validateToken(jwt.getAccessToken());
        Boolean result2 = jwtUtil.validateToken(jwt.getRefreshToken());

        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(true);
    }

    @Test
    void getSubject() {
        String subject = "test-subject";

        Jwt jwt = jwtUtil.generateTokens(subject);
        String result1 = jwtUtil.getSubject(jwt.getAccessToken());
        String result2 = jwtUtil.getSubject(jwt.getRefreshToken());

        assertThat(result1).isEqualTo(subject);
        assertThat(result2).isEqualTo(subject);
    }

    @Test
    void isExpired() {
        String subject = "test-subject";

        Jwt jwt = jwtUtil.generateTokens(subject);
        Boolean result1 = jwtUtil.isExpired(jwt.getAccessToken());
        Boolean result2 = jwtUtil.isExpired(jwt.getRefreshToken());

        assertThat(result1).isEqualTo(false);
        assertThat(result2).isEqualTo(false);
    }
}
