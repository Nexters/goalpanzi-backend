package com.nexters.goalpanzi.config.jwt;

import com.nexters.goalpanzi.common.jwt.Jwt;
import com.nexters.goalpanzi.common.jwt.JwtProvider;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtProviderTest {

    @Test
    void JWT_토큰을_생성한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        Jwt jwt = jwtProvider.generateTokens("subject");

        assertThat(jwt).isNotNull();
    }

    @Test
    void 유효한_JWT_토큰을_검증한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        Jwt jwt = jwtProvider.generateTokens("subject");
        Boolean isValidAccessToken = jwtProvider.validateToken(jwt.accessToken());
        Boolean isValidRefreshToken = jwtProvider.validateToken(jwt.refreshToken());

        assertThat(isValidAccessToken).isTrue();
        assertThat(isValidRefreshToken).isTrue();
    }

    @Test
    void 유효한_JWT_토큰에서_subject를_추출한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        Jwt jwt = jwtProvider.generateTokens("subject");
        String accessTokenSubject = jwtProvider.getSubject(jwt.accessToken());
        String refreshTokenSubject = jwtProvider.getSubject(jwt.refreshToken());

        assertThat(accessTokenSubject).isEqualTo("subject");
        assertThat(refreshTokenSubject).isEqualTo("subject");
    }

    @Test
    void 만료된_JWT_토큰을_검증한다() throws InterruptedException {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret("secret")
                .accessExpiresIn(1000)
                .refreshExpiresIn(1000)
                .build();

        Jwt jwt = jwtProvider.generateTokens("subject");
        Thread.sleep(2000);
        Boolean isExpiredAccessToken = jwtProvider.validateToken(jwt.accessToken());
        Boolean isExpiredRefreshToken = jwtProvider.validateToken(jwt.refreshToken());

        assertThat(isExpiredAccessToken).isFalse();
        assertThat(isExpiredRefreshToken).isFalse();
    }

    @Test
    void 만료된_JWT_토큰에서_subject를_추출한다() throws InterruptedException {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret("secret")
                .accessExpiresIn(1000)
                .refreshExpiresIn(1000)
                .build();

        Jwt jwt = jwtProvider.generateTokens("subject");
        Thread.sleep(2000);
        String accessTokenSubject = jwtProvider.getSubject(jwt.accessToken());
        String refreshTokenSubject = jwtProvider.getSubject(jwt.refreshToken());

        assertThat(accessTokenSubject).isEqualTo("subject");
        assertThat(refreshTokenSubject).isEqualTo("subject");
    }

    @Test
    void 서명이_잘못된_JWT_토큰을_검증한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        long currMillis = System.currentTimeMillis();
        Date iat = new Date(currMillis);
        Date exp = new Date(currMillis + 60000);

        String invalidSignatureToken = Jwts.builder()
                .setSubject("subject")
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, "wrongSecret")
                .compact();

        assertThatThrownBy(
                () -> jwtProvider.validateToken(invalidSignatureToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    void 잘못된_형식의_JWT_토큰을_검증한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        String malformedToken = "malformedToken";

        assertThatThrownBy(
                () -> jwtProvider.validateToken(malformedToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }
}
