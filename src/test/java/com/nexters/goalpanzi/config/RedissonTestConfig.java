package com.nexters.goalpanzi.config;

import com.nexters.goalpanzi.common.redisson.RedissonLockTestBean;
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
