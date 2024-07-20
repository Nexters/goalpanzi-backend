package com.nexters.goalpanzi.application.auth.apple;

import com.nexters.goalpanzi.util.Nonce;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AppleClaimsValidatorTest {

    private static final String NONCE_KEY = "nonce";
    private final AppleClaimsValidator appleClaimsValidator = new AppleClaimsValidator("iss", "aud");

    @Test
    void Claims_검증이_가능하다() {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(NONCE_KEY, Nonce.generate());

        Claims claims = Jwts.claims(claimsMap)
                .setIssuer("iss")
                .setAudience("aud");

        assertThat(appleClaimsValidator.isValid(claims)).isTrue();
    }
}