package com.nexters.goalpanzi.config.jwt;

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
public class JwtManager {

    private enum TokenType {ACCESS, REFRESH}

    private final String secret;
    private final long accessExpiresIn;
    private final long refreshExpiresIn;

    public JwtManager(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.expires-in}") long accessExpiresIn,
            @Value("${jwt.refresh-token.expires-in}") long refreshExpiresIn
    ) {
        this.secret = secret;
        this.accessExpiresIn = accessExpiresIn;
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public Jwt generateTokens(String subject) {
        return Jwt.builder()
                .accessToken(createToken(subject, TokenType.ACCESS))
                .refreshToken(createToken(subject, TokenType.REFRESH))
                .refreshExpiresIn(refreshExpiresIn)
                .build();
    }

    private String createToken(String subject, TokenType tokenType) {
        long currMillis = System.currentTimeMillis();
        long expiresIn = tokenType == TokenType.ACCESS ? accessExpiresIn : refreshExpiresIn;

        Date iat = new Date(currMillis);
        Date exp = new Date(currMillis + expiresIn);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isExpired(token);
    }

    public String getSubject(String token) {
        return getClaims(token)
                .getSubject();
    }

    public Boolean isExpired(String token) {
        Date now = new Date();
        return getClaims(token)
                .getExpiration()
                .before(now);
    }

    private Claims getClaims(String token) {
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
