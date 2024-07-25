package com.nexters.goalpanzi.acceptance.fixture;

import com.nexters.goalpanzi.common.util.Nonce;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Map;

import static com.nexters.goalpanzi.application.auth.apple.AppleClaimsValidator.NONCE_KEY;

public class TokenFixture {

    public static String generateAppleToken() throws NoSuchAlgorithmException {
        Date issuedAt = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        return Jwts.builder()
                .setHeaderParam("kid", "T8tIJ1zSrO")
                .addClaims(Map.of("email", "test@test.com", NONCE_KEY, Nonce.generate()))
                .setIssuer("iss")
                .setIssuedAt(issuedAt)
                .setAudience("aud")
                .setExpiration(new Date(issuedAt.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }
}
