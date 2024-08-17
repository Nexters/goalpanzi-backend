package com.nexters.goalpanzi.common.auth.jwt;

import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import io.jsonwebtoken.*;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Builder
@Component
public class JwtProvider {

    private enum TokenType {ACCESS, REFRESH}

    private final String secret;

    private final long accessExpiresInDays;
    private final long refreshExpiresInDays;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.expires-in-days}") long accessExpiresInDays,
            @Value("${jwt.refresh-token.expires-in-days}") long refreshExpiresInDays
    ) {
        this.secret = secret;
        this.accessExpiresInDays = accessExpiresInDays;
        this.refreshExpiresInDays = refreshExpiresInDays;
    }

    public com.nexters.goalpanzi.common.auth.jwt.Jwt generateTokens(final String subject) {
        return Jwt.builder()
                .accessToken(createToken(subject, TokenType.ACCESS))
                .refreshToken(createToken(subject, TokenType.REFRESH))
                .refreshExpiresIn(computeExpiresIn(TokenType.REFRESH))
                .build();
    }

    private String createToken(final String subject, final TokenType tokenType) {
        long currMillis = System.currentTimeMillis();
        long expiresIn = computeExpiresIn(tokenType);

        Date iat = new Date(currMillis);
        Date exp = new Date(currMillis + expiresIn);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private long computeExpiresIn(final TokenType tokenType) {
        long expiresInDays = tokenType == TokenType.ACCESS ? accessExpiresInDays : refreshExpiresInDays;
        return expiresInDays * 1000 * 60 * 60 * 24;
    }

    public Boolean validateToken(final String token) {
        return !isExpired(token);
    }

    public String getSubject(final String token) {
        return getClaims(token)
                .getSubject();
    }

    public Boolean isExpired(final String token) {
        Date now = new Date();
        return getClaims(token)
                .getExpiration()
                .before(now);
    }

    private Claims getClaims(final String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }
    }
}
