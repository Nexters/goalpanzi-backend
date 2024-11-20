package com.nexters.goalpanzi.infrastructure.auth;

import com.nexters.goalpanzi.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final String REFRESH_TOKEN_POSTFIX = ":refresh_token";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String memberId, String refreshToken, long ttl) {
        String key = makeKey(memberId);
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(ttl));
    }

    public String find(String memberId) {
        String key = makeKey(memberId);
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String memberId) {
        String key = makeKey(memberId);
        return redisTemplate.delete(key);
    }

    private String makeKey(String memberId) {
        return memberId + REFRESH_TOKEN_POSTFIX;
    }
}
