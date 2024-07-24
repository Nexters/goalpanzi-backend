package com.nexters.goalpanzi.application.auth.apple;

import com.nexters.goalpanzi.common.util.Nonce;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppleClaimsValidator {

    static final String NONCE_KEY = "nonce";

    private final String iss;
    private final String clientId;

    public AppleClaimsValidator(
            @Value("${oauth.apple.iss}") String iss,
            @Value("${oauth.apple.client-id}") String clientId
    ) {
        this.iss = iss;
        this.clientId = clientId;
    }

    public boolean isValid(final Claims claims) {
        return claims.getIssuer().contains(iss) &&
                claims.getAudience().equals(clientId) &&
                Nonce.isValid(claims.get(NONCE_KEY, String.class));
    }
}
