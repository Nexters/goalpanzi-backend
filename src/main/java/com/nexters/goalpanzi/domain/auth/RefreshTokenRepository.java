package com.nexters.goalpanzi.domain.auth;

import com.nexters.goalpanzi.domain.common.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository implements RedisRepository {

    private static final String REFRESH_TOKEN_POSTFIX = ":refresh_token";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String altKey, String refreshToken, long ttl) {
        String key = makeKey(altKey);
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(ttl));
    }

    public String find(String altKey) {
        String key = makeKey(altKey);
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String altKey) {
        String key = makeKey(altKey);
        return redisTemplate.delete(key);
    }

    private String makeKey(String altKey) {
        return altKey + REFRESH_TOKEN_POSTFIX;
    }
}
