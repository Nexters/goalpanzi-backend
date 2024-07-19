package com.nexters.goalpanzi.application.auth.apple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ApplePublicKeyGenerator {

    private static final String ALG_HEADER_KEY = "alg";
    private static final String KID_HEADER_KEY = "kid";
    private static final int POSITIVE_SIGNUM = 1;

    private final AppleApiCaller appleApiCaller;
    private final AppleTokenManager appleTokenManager;

    public PublicKey generatePublicKey(final String identityToken) {
        Map<String, String> tokenHeaders = appleTokenManager.getHeader(identityToken);
        ApplePublicKeys applePublicKeys = appleApiCaller.getApplePublicKeys();
        ApplePublicKey matchesKey =
                applePublicKeys.getMatchesKey(tokenHeaders.get(ALG_HEADER_KEY), tokenHeaders.get(KID_HEADER_KEY));

        return generatePublicKeyWithApplePublicKey(matchesKey);
    }

    private PublicKey generatePublicKeyWithApplePublicKey(final ApplePublicKey applePublicKey) {
        byte[] n = Base64.getUrlDecoder().decode(applePublicKey.n());
        byte[] e = Base64.getUrlDecoder().decode(applePublicKey.e());
        RSAPublicKeySpec publicKeySpec =
                new RSAPublicKeySpec(new BigInteger(POSITIVE_SIGNUM, n), new BigInteger(POSITIVE_SIGNUM, e));

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.kty());
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new RuntimeException("응답 받은 Apple Public Key로 PublicKey를 생성할 수 없습니다.");
        }
    }
}