package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.infrastructure.redis.RedisRepository;

import java.time.LocalDate;
import java.util.Set;

public interface MissionRetryMessageRepository extends RedisRepository {

    void update(String memberId, String deviceToken);

    Set<String> keys(LocalDate pushDate);
}
