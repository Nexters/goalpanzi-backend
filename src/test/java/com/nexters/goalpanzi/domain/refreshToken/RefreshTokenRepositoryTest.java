package com.nexters.goalpanzi.domain.refreshToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
public class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @TestConfiguration
    static class RedisTestConfig {
        @Bean
        public RefreshTokenRepository refreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
            return new RefreshTokenRepository(redisTemplate);
        }
    }

    @BeforeEach
    public void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void findSuccess() {
        String uuid = "test-uuid";
        String refreshToken = "test-refreshToken";
        long ttl = 60000;

        refreshTokenRepository.save(uuid, refreshToken, ttl);
        String foundToken = refreshTokenRepository.find(uuid);

        assertThat(foundToken).isEqualTo(refreshToken);
    }

    @Test
    void findFail() {
        String uuid = "test-uuid";

        String foundToken = refreshTokenRepository.find(uuid);

        assertThat(foundToken).isNull();
    }
}
