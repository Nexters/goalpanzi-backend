package com.nexters.goalpanzi.application.auth.apple;

import com.nexters.goalpanzi.common.auth.Nonce;
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

//    @Test
//    void Nonce_값이_잘못된_경우_검증에_실패한다() {
//        Map<String, Object> claimsMap = new HashMap<>();
//        claimsMap.put(NONCE_KEY, "abcde");
//
//        Claims claims = Jwts.claims(claimsMap)
//                .setIssuer("iss")
//                .setAudience("aud");
//
//        assertThat(appleClaimsValidator.isValid(claims)).isFalse();
//    }
}