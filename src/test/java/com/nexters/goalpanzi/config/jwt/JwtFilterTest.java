package com.nexters.goalpanzi.config.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class JwtFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Configuration
    static class TestConfig implements WebMvcConfigurer {

        @Bean
        public JwtUtil jwtUtil() {
            String secret = "test-secret";
            long accessExpiresIn = 86400000;
            long refreshExpiresIn = 1209600000;
            return new JwtUtil(secret, accessExpiresIn, refreshExpiresIn);
        }

        @Bean
        public FilterRegistrationBean<JwtFilter> jwtFilter() {
            FilterRegistrationBean<JwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();
            filterRegistrationBean.setFilter(new JwtFilter(jwtUtil()));
            filterRegistrationBean.addUrlPatterns("/api/*");
            return filterRegistrationBean;
        }
    }

    @Test
    void unauthorizedRequest() throws Exception {
        mockMvc.perform(get("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }
}
