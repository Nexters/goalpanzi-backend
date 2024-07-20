package com.nexters.goalpanzi.application.auth.apple;

import com.nexters.goalpanzi.util.Nonce;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Map;

import static com.nexters.goalpanzi.application.auth.apple.AppleClaimsValidator.NONCE_KEY;
import static org.assertj.core.api.Assertions.assertThat;

class AppleTokenManagerTest {

    private static final String ISS = "iss";
    private static final String CLIENT_ID = "com.test.test";

    private final AppleClaimsValidator appleClaimsValidator = new AppleClaimsValidator(ISS, CLIENT_ID);
    private final AppleTokenManager appleTokenManager = new AppleTokenManager(appleClaimsValidator);

    @Test
    void 애플_JWT_토큰_헤더를_파싱한다() throws NoSuchAlgorithmException {
        Date issuedAt = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", "86D88Kf")
                .claim("email", "test@test.com")
                .setIssuer("iss")
                .setIssuedAt(issuedAt)
                .setAudience("aud")
                .setExpiration(new Date(issuedAt.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        Map<String, String> actual = appleTokenManager.getHeader(identityToken);

        assertThat(actual).containsKeys("alg", "kid");
    }

    @Test
    void 애플_JWT_토큰_Claim을_파싱한다() throws NoSuchAlgorithmException {
        Date issuedAt = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", "86D88Kf")
                .addClaims(Map.of("email", "test@test.com", NONCE_KEY, Nonce.generate()))
                .setIssuer("iss")
                .setIssuedAt(issuedAt)
                .setAudience(CLIENT_ID)
                .setExpiration(new Date(issuedAt.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        Claims claims = appleTokenManager.getClaimsIfValid(identityToken, keyPair.getPublic());
        assertThat(claims.get("email")).isEqualTo("test@test.com");
    }
}