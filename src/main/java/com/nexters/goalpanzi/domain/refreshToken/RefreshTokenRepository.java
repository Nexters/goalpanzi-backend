package com.nexters.goalpanzi.domain.refreshToken;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepository {
  private static final String refreshTokenPostfix = ":refresh_token";

  private final RedisTemplate<String, String> redisTemplate;

  public RefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void save(String uuid, String refreshToken, long ttl) {
    String key = makeKey(uuid);
    redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(ttl));
  }

  public String find(String uuid) {
    String key = makeKey(uuid);
    return redisTemplate.opsForValue().get(key);
  }

  private String makeKey(String uuid) {
    return uuid + refreshTokenPostfix;
  }
}
