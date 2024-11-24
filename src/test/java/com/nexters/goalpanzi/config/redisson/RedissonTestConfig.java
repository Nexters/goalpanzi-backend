package com.nexters.goalpanzi.config.redisson;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RedissonTestConfig {

    @Bean
    public RedissonLockTestBean redissonLockTestBean() {
        return new RedissonLockTestBean();
    }
}
