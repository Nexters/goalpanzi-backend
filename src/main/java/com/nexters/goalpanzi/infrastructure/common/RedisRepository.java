package com.nexters.goalpanzi.infrastructure.common;

public interface RedisRepository {

    void save(String key, String value, long ttl);

    String find(String key);

    Boolean delete(String key);
}
