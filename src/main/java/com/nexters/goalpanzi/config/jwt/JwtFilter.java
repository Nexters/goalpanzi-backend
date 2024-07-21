package com.nexters.goalpanzi.config.jwt;

import com.nexters.goalpanzi.exception.BaseException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEMA = "Bearer ";
    private static final String TOKEN_REISSUE_URI = "/api/auth/token:reissue";
    private static final String CONTENT_TYPE = "application/json";
    private static final String ATTRIBUTE_NAME = "altKey";
    private static final String UNAUTHORIZED_MESSAGE = "{\"error\": \"Unauthorized\"}";

    private static final List<String> whitelist = List.of("/api/auth/login");

    private final JwtManager jwtManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (isWhitelisted(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = resolveToken(request);
            if (isTokenReissueRequest(requestURI, token)) {
                request.setAttribute(ATTRIBUTE_NAME, jwtManager.getSubject(token));
                filterChain.doFilter(request, response);
                return;
            }

            if (isAuthenticated(token)) {
                request.setAttribute(ATTRIBUTE_NAME, jwtManager.getSubject(token));
                filterChain.doFilter(request, response);
                return;
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(CONTENT_TYPE);
            response.getWriter().write(UNAUTHORIZED_MESSAGE);
        } catch (BaseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(CONTENT_TYPE);
            response.getWriter().write(e.getMessage());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (isBearerToken(authorizationHeader)) {
            return authorizationHeader.substring(AUTHORIZATION_SCHEMA.length());
        }
        return null;
    }

    private Boolean isBearerToken(String header) {
        return StringUtils.hasText(header) && header.startsWith(AUTHORIZATION_SCHEMA);
    }

    private Boolean isTokenReissueRequest(String requestURI, String token) {
        return requestURI.equals(TOKEN_REISSUE_URI)
                && StringUtils.hasText(token)
                && jwtManager.isExpired(token);
    }

    private Boolean isAuthenticated(String token) {
        return StringUtils.hasText(token) && jwtManager.validateToken(token);
    }

    private Boolean isWhitelisted(String requestURI) {
        return whitelist.stream().anyMatch(requestURI::startsWith);
    }
}
