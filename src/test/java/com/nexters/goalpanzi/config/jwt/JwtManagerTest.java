package com.nexters.goalpanzi.config.jwt;

import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtManagerTest {

    @Test
    void JWT_토큰을_생성한다() {
        JwtManager jwtManager = JwtManager.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        Jwt jwt = jwtManager.generateTokens("subject");

        assertThat(jwt).isNotNull();
    }

    @Test
    void 유효한_JWT_토큰을_검증한다() {
        JwtManager jwtManager = JwtManager.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        Jwt jwt = jwtManager.generateTokens("subject");
        Boolean isValidAccessToken = jwtManager.validateToken(jwt.accessToken());
        Boolean isValidRefreshToken = jwtManager.validateToken(jwt.refreshToken());

        assertThat(isValidAccessToken).isEqualTo(true);
        assertThat(isValidRefreshToken).isEqualTo(true);
    }

    @Test
    void 유효한_JWT_토큰에서_subject를_추출한다() {
        JwtManager jwtManager = JwtManager.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        Jwt jwt = jwtManager.generateTokens("subject");
        String accessTokenSubject = jwtManager.getSubject(jwt.accessToken());
        String refreshTokenSubject = jwtManager.getSubject(jwt.refreshToken());

        assertThat(accessTokenSubject).isEqualTo("subject");
        assertThat(refreshTokenSubject).isEqualTo("subject");
    }

    @Test
    void 만료된_JWT_토큰을_검증한다() throws InterruptedException {
        JwtManager jwtManager = JwtManager.builder()
                .secret("secret")
                .accessExpiresIn(1000)
                .refreshExpiresIn(1000)
                .build();

        Jwt jwt = jwtManager.generateTokens("subject");
        Thread.sleep(2000);
        Boolean isExpiredAccessToken = jwtManager.validateToken(jwt.accessToken());
        Boolean isExpiredRefreshToken = jwtManager.validateToken(jwt.refreshToken());

        assertThat(isExpiredAccessToken).isEqualTo(false);
        assertThat(isExpiredRefreshToken).isEqualTo(false);
    }

    @Test
    void 만료된_JWT_토큰에서_subject를_추출한다() throws InterruptedException {
        JwtManager jwtManager = JwtManager.builder()
                .secret("secret")
                .accessExpiresIn(1000)
                .refreshExpiresIn(1000)
                .build();

        Jwt jwt = jwtManager.generateTokens("subject");
        Thread.sleep(2000);
        String accessTokenSubject = jwtManager.getSubject(jwt.accessToken());
        String refreshTokenSubject = jwtManager.getSubject(jwt.refreshToken());

        assertThat(accessTokenSubject).isEqualTo("subject");
        assertThat(refreshTokenSubject).isEqualTo("subject");
    }

    @Test
    void 서명이_잘못된_JWT_토큰을_검증한다() {
        JwtManager jwtManager = JwtManager.builder()
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
                () -> jwtManager.validateToken(invalidSignatureToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    void 잘못된_형식의_JWT_토큰을_검증한다() {
        JwtManager jwtManager = JwtManager.builder()
                .secret("secret")
                .accessExpiresIn(60000)
                .refreshExpiresIn(60000)
                .build();

        String malformedToken = "malformedToken";

        assertThatThrownBy(
                () -> jwtManager.validateToken(malformedToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }
}
