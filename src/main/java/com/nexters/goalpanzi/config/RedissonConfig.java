package com.nexters.goalpanzi.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    private static final String HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        config.useSingleServer().setAddress(makeAddress(redisProperties));
        return Redisson.create(config);
    }

    private String makeAddress(RedisProperties redisProperties) {
        return HOST_PREFIX + redisProperties.getHost() + ":" + redisProperties.getPort();
    }
}
