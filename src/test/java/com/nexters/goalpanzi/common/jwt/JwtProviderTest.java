package com.nexters.goalpanzi.config.jwt;

import com.nexters.goalpanzi.common.auth.jwt.Jwt;
import com.nexters.goalpanzi.common.auth.jwt.JwtProvider;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static com.nexters.goalpanzi.fixture.MemberFixture.MEMBER_ID;
import static com.nexters.goalpanzi.fixture.TokenFixture.SECRET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class JwtProviderTest {

    @Test
    void JWT_토큰을_생성한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret(SECRET)
                .accessExpiresInDays(1)
                .refreshExpiresInDays(1)
                .build();

        Jwt jwt = jwtProvider.generateTokens(MEMBER_ID.toString());

        System.out.println(jwt.accessToken());

        assertThat(jwt).isNotNull();
    }

    @Test
    void 유효한_JWT_토큰을_검증한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret(SECRET)
                .accessExpiresInDays(1)
                .refreshExpiresInDays(1)
                .build();

        Jwt jwt = jwtProvider.generateTokens(MEMBER_ID.toString());
        Boolean isValidAccessToken = jwtProvider.validateToken(jwt.accessToken());
        Boolean isValidRefreshToken = jwtProvider.validateToken(jwt.refreshToken());

        assertAll(
                () -> assertThat(isValidAccessToken).isTrue(),
                () -> assertThat(isValidRefreshToken).isTrue()
        );
    }

    @Test
    void 유효한_JWT_토큰에서_subject를_추출한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret(SECRET)
                .accessExpiresInDays(1)
                .refreshExpiresInDays(1)
                .build();

        Jwt jwt = jwtProvider.generateTokens(MEMBER_ID.toString());
        String accessTokenSubject = jwtProvider.getSubject(jwt.accessToken());
        String refreshTokenSubject = jwtProvider.getSubject(jwt.refreshToken());

        assertAll(
                () -> assertThat(accessTokenSubject).isEqualTo(MEMBER_ID.toString()),
                () -> assertThat(refreshTokenSubject).isEqualTo(MEMBER_ID.toString())
        );
    }

    @Test
    void 만료된_JWT_토큰을_검증한다() throws InterruptedException {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret(SECRET)
                .accessExpiresInDays(0)
                .refreshExpiresInDays(0)
                .build();

        Jwt jwt = jwtProvider.generateTokens(MEMBER_ID.toString());

        Boolean isExpiredAccessToken = jwtProvider.validateToken(jwt.accessToken());
        Boolean isExpiredRefreshToken = jwtProvider.validateToken(jwt.refreshToken());

        assertAll(
                () -> assertThat(isExpiredAccessToken).isFalse(),
                () -> assertThat(isExpiredRefreshToken).isFalse()
        );
    }

    @Test
    void 만료된_JWT_토큰에서_subject를_추출한다() throws InterruptedException {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret(SECRET)
                .accessExpiresInDays(0)
                .refreshExpiresInDays(0)
                .build();

        Jwt jwt = jwtProvider.generateTokens(MEMBER_ID.toString());

        String accessTokenSubject = jwtProvider.getSubject(jwt.accessToken());
        String refreshTokenSubject = jwtProvider.getSubject(jwt.refreshToken());

        assertAll(
                () -> assertThat(accessTokenSubject).isEqualTo(MEMBER_ID.toString()),
                () -> assertThat(refreshTokenSubject).isEqualTo(MEMBER_ID.toString())
        );
    }

    @Test
    void 서명이_잘못된_JWT_토큰을_검증한다() {
        JwtProvider jwtProvider = JwtProvider.builder()
                .secret(SECRET)
                .accessExpiresInDays(1)
                .refreshExpiresInDays(1)
                .build();

        long currMillis = System.currentTimeMillis();
        Date iat = new Date(currMillis);
        Date exp = new Date(currMillis + 60000);

        String invalidSignatureToken = Jwts.builder()
                .setSubject(MEMBER_ID.toString())
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
                .secret(SECRET)
                .accessExpiresInDays(1)
                .refreshExpiresInDays(1)
                .build();

        String malformedToken = "malformedToken";

        assertThatThrownBy(
                () -> jwtProvider.validateToken(malformedToken))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_TOKEN.getMessage());
    }
}
