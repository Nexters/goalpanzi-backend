package com.nexters.goalpanzi.infrastructure.redis;

public interface RedisRepository {

    void save(String key, String value, long ttl);

    String find(String key);

    Boolean delete(String key);
}
