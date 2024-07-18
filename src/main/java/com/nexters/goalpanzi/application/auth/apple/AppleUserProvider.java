package com.nexters.goalpanzi.application.auth.apple;

import com.nexters.goalpanzi.application.auth.SocialUserInfo;
import com.nexters.goalpanzi.application.auth.SocialUserProvider;
import com.nexters.goalpanzi.domain.member.SocialType;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AppleUserProvider implements SocialUserProvider {

    private static final String EMAIL = "email";

    private final AppleTokenManager appleTokenManager;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;
    private final AppleApiCaller appleApiCaller;

    public SocialType getSocialType() {
        return SocialType.APPLE;
    }

    public SocialUserInfo getSocialUserInfo(String identityToken) {
        Map<String, String> tokenHeader = appleTokenManager.getHeader(identityToken);
        ApplePublicKeys applePublicKeys = appleApiCaller.getApplePublicKeys();

        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(tokenHeader, applePublicKeys);
        Claims claims = appleTokenManager.getClaimsIfValid(identityToken, publicKey);
        validateClaims(claims);

        return new SocialUserInfo(claims.getSubject(), claims.get(EMAIL, String.class));
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new RuntimeException("Apple OAuth Claims 값이 올바르지 않습니다.");
        }
    }
}
