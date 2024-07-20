package com.nexters.goalpanzi.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_SCHEMA = "Bearer ";
    private static final String TOKEN_REISSUE_URI = "/api/auth/token:reissue";
    private static final String CONTENT_TYPE = "application/json";
    private static final String ATTRIBUTE_NAME = "uuid";
    private static final String UNAUTHORIZED_MESSAGE = "{\"error\": \"Unauthorized\"}";

    private static final List<String> whitelist = List.of("/api/auth/login");

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String requestURI = httpServletRequest.getRequestURI();
        if (isWhitelisted(requestURI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = resolveToken(httpServletRequest);
        if (isTokenReissueRequest(requestURI, token)) {
            httpServletRequest.setAttribute(ATTRIBUTE_NAME, jwtUtil.getSubject(token));
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (isAuthenticated(token)) {
            httpServletRequest.setAttribute(ATTRIBUTE_NAME, jwtUtil.getSubject(token));
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(CONTENT_TYPE);
        httpServletResponse.getWriter().write(UNAUTHORIZED_MESSAGE);
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
                && jwtUtil.isExpired(token);
    }

    private Boolean isAuthenticated(String token) {
        return StringUtils.hasText(token) && jwtUtil.validateToken(token);
    }

    private Boolean isWhitelisted(String requestURI) {
        return whitelist.stream().anyMatch(requestURI::startsWith);
    }
}
