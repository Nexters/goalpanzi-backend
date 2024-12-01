package com.nexters.goalpanzi.config.redisson;

import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@ImportAutoConfiguration(
        classes = {RedissonAutoConfigurationV2.class}
)
public class RedissonTestConfig {

    @Bean
    public RedissonLockTestBean redissonLockTestBean() {
        return new RedissonLockTestBean();
    }
}
