package com.nexters.goalpanzi.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexters.goalpanzi.common.auth.jwt.JwtParser;
import com.nexters.goalpanzi.common.auth.jwt.JwtProvider;
import com.nexters.goalpanzi.common.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Configuration
    static class TestConfig implements WebMvcConfigurer {

        @Bean
        public JwtProvider jwtProvider() {
            return JwtProvider.builder()
                    .secret("secret")
                    .accessExpiresInDays(1)
                    .refreshExpiresInDays(1)
                    .build();
        }

        @Bean
        public JwtParser jwtParser() {
            return new JwtParser();
        }


        @Bean
        public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
            FilterRegistrationBean<JwtAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
            filterRegistrationBean.setFilter(new JwtAuthenticationFilter(jwtProvider(), jwtParser(), new ObjectMapper()));
            filterRegistrationBean.addUrlPatterns("/api/*");
            return filterRegistrationBean;
        }
    }

    @Test
    void 인증받지_않은_사용자_요청을_처리한다() throws Exception {
        mockMvc.perform(get("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 잘못된_JWT_토큰이_붙은_사용자_요청을_처리한다() throws Exception {
        mockMvc.perform(get("/api/protected")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + "invalidToken"))
                .andExpect(status().isUnauthorized());
    }
}
