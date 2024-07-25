package com.nexters.goalpanzi.config;

import com.nexters.goalpanzi.common.filter.JwtAuthenticationFilter;
import com.nexters.goalpanzi.common.jwt.JwtParser;
import com.nexters.goalpanzi.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {

    private final JwtProvider jwtProvider;
    private final JwtParser jwtParser;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtAuthenticationFilter(jwtProvider, jwtParser));
        filterRegistrationBean.addUrlPatterns("/api/*");
        return filterRegistrationBean;
    }
}
