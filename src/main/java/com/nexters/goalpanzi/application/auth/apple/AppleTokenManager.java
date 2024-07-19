package com.nexters.goalpanzi.application.auth.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.goalpanzi.exception.BusinessException;
import com.nexters.goalpanzi.exception.ErrorCode;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AppleTokenManager {

    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final AppleClaimsValidator appleClaimsValidator;

    public Map<String, String> getHeader(String identityToken) {
        try {
            String encodedHeader = identityToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[0];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            return OBJECT_MAPPER.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new BusinessException(ErrorCode.INVALID_APPLE_TOKEN);
        }
    }

    public Claims getClaimsIfValid(String idToken, PublicKey publicKey) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(idToken)
                    .getBody();
            validateClaims(claims);
            return claims;
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.EXPIRED_APPLE_TOKEN);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_APPLE_TOKEN);
        }
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new RuntimeException("Apple OAuth Claims 값이 올바르지 않습니다.");
        }
    }
}
