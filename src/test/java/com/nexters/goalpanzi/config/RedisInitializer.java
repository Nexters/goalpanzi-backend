package com.nexters.goalpanzi.config.redis;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class RedisInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String REDIS_IMAGE = "redis:latest";
    private static final Integer REDIS_PORT = 6379;
    private static final GenericContainer REDIS = new GenericContainer(REDIS_IMAGE)
            .withExposedPorts(REDIS_PORT);

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        REDIS.start();
        TestPropertyValues.of(
                Map.of(
                        "spring.data.redis.host", REDIS.getHost(),
                        "spring.data.redis.port", REDIS.getFirstMappedPort().toString()
                )
        ).applyTo(applicationContext);
    }
}
