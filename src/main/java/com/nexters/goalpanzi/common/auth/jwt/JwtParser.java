package com.nexters.goalpanzi.common.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtParser {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEMA = "Bearer ";

    public String resolveToken(final HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (isBearerToken(authorizationHeader)) {
            return authorizationHeader.substring(AUTHORIZATION_SCHEMA.length());
        }
        return null;
    }

    private Boolean isBearerToken(final String header) {
        return StringUtils.hasText(header) && header.startsWith(AUTHORIZATION_SCHEMA);
    }
}
