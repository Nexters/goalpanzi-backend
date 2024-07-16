package com.nexters.goalpanzi.application.auth.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@Component
public class AppleTokenManager {
    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Map<String, String> getHeader(String identityToken) {
        try {
            String encodedHeader = identityToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[0];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            return OBJECT_MAPPER.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            // TODO: 커스텀 Exception 정의
            throw new RuntimeException("Apple OAuth Identity Token 형식이 올바르지 않습니다.");
        }
    }

    public Claims getClaimsIfValid(String idToken, PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // TODO: 커스텀 Exception 정의
            throw new RuntimeException("Apple OAuth 로그인 중 Identity Token 유효기간이 만료됐습니다.");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // TODO: 커스텀 Exception 정의
            throw new RuntimeException("Apple OAuth Identity Token 값이 올바르지 않습니다.");
        }
    }
}
