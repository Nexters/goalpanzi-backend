package com.nexters.goalpanzi.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.goalpanzi.common.auth.jwt.JwtParser;
import com.nexters.goalpanzi.common.auth.jwt.JwtProvider;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_REISSUE_URI = "/api/auth/token:reissue";
    private static final String CONTENT_TYPE = "application/json";
    private static final String ATTRIBUTE_NAME = "altKey";

    private static final List<String> whitelist = List.of("/api/auth/login");

    private final JwtProvider jwtProvider;
    private final JwtParser jwtParser;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (isWhitelisted(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtParser.resolveToken(request);
            if (isTokenReissueRequest(requestURI, token)) {
                request.setAttribute(ATTRIBUTE_NAME, jwtProvider.getSubject(token));
                filterChain.doFilter(request, response);
                return;
            }

            if (isAuthenticated(token)) {
                request.setAttribute(ATTRIBUTE_NAME, jwtProvider.getSubject(token));
                filterChain.doFilter(request, response);
                return;
            }
            respondWithUnauthorized(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            respondWithUnauthorized(response);
        }
    }

    private void respondWithUnauthorized(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ErrorCode.INVALID_TOKEN.getMessage()))
        );
    }

    private Boolean isTokenReissueRequest(String requestURI, String token) {
        return requestURI.equals(TOKEN_REISSUE_URI)
                && StringUtils.hasText(token)
                && jwtProvider.isExpired(token);
    }

    private Boolean isAuthenticated(String token) {
        return StringUtils.hasText(token) && jwtProvider.validateToken(token);
    }

    private Boolean isWhitelisted(String requestURI) {
        return whitelist.stream().anyMatch(requestURI::startsWith);
    }
}
