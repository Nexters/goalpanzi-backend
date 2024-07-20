package com.nexters.goalpanzi.config;

import com.nexters.goalpanzi.config.jwt.JwtFilter;
import com.nexters.goalpanzi.config.jwt.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
  private final JwtUtil jwtUtil;

  public WebConfig(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Bean
  public FilterRegistrationBean<JwtFilter> JwtFilter() {
    FilterRegistrationBean<JwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();
    filterRegistrationBean.setFilter(new JwtFilter(jwtUtil));
    filterRegistrationBean.addUrlPatterns("/api/*");
    return filterRegistrationBean;
  }
}
