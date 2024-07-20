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

    public SocialType getSocialType() {
        return SocialType.APPLE;
    }

    public SocialUserInfo getSocialUserInfo(final String identityToken) {
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(identityToken);
        Claims claims = appleTokenManager.getClaimsIfValid(identityToken, publicKey);

        return new SocialUserInfo(claims.getSubject(), claims.get(EMAIL, String.class));
    }
}
