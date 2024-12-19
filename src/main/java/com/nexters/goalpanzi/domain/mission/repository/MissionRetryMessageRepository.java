package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.infrastructure.redis.RedisRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface MissionRetryMessageRepository extends RedisRepository {

    List<String> findAllByMemberId(String memberId);

    List<String> findAllByDeviceToken(String deviceToken);

    Long deleteAllByMemberId(String memberId);

    Long deleteAllByDeviceToken(String deviceToken);

    void update(String memberId, String deviceToken);

    Set<String> keys(LocalDate pushDate);
}
