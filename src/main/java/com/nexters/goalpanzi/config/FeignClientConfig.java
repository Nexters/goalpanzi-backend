package com.nexters.goalpanzi.config;

import com.nexters.goalpanzi.GoalpanziApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = GoalpanziApplication.class)
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class FeignClientConfig {
}