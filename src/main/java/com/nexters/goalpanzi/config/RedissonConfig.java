package com.nexters.goalpanzi.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@ConditionalOnProperty(name = "spring.data.redis.redisson.enabled", havingValue = "true")
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.redisson.file}")
    private Resource redissonConfigFile;

    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(redissonConfigFile.getInputStream());

        return Redisson.create(config);
    }
}
