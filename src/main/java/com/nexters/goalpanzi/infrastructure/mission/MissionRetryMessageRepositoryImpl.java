package com.nexters.goalpanzi.infrastructure.mission;

import com.nexters.goalpanzi.domain.mission.repository.MissionRetryMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class MissionRetryMessageRepositoryImpl implements MissionRetryMessageRepository {

    private static final String MISSION_RETRY_MESSAGE_PREFIX = "mission_retry_message:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String memberId, String deviceToken, long ttl) {
        LocalDate pushDate = computePushDate();
        String key = makeKey(pushDate, memberId, deviceToken);
        redisTemplate.opsForValue().set(key, deviceToken, Duration.ofMillis(ttl));
    }

    public String find(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public List<String> findAllByMemberId(String memberId) {
        String pattern = makeAnyDateAndDeviceTokenPattern(memberId);
        return redisTemplate.opsForValue()
                .multiGet(redisTemplate.keys(pattern));
    }

    public List<String> findAllByDeviceToken(String deviceToken) {
        String pattern = makeAnyDateAndMemberPattern(deviceToken);
        return redisTemplate.opsForValue()
                .multiGet(redisTemplate.keys(pattern));
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Long deleteAllByMemberId(String memberId) {
        String pattern = makeAnyDateAndDeviceTokenPattern(memberId);
        return redisTemplate
                .delete(redisTemplate.keys(pattern));
    }

    public Long deleteAllByDeviceToken(String deviceToken) {
        String pattern = makeAnyDateAndMemberPattern(deviceToken);
        return redisTemplate
                .delete(redisTemplate.keys(pattern));
    }

    public void update(String memberId, String deviceToken) {
        String pattern = makeAnyDateAndDeviceTokenPattern(memberId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.isEmpty()) {
            return;
        }
        String key = String.valueOf(keys.iterator().next());
        Long ttl = redisTemplate.getExpire(key);
        delete(key);
        save(memberId, deviceToken, ttl);
    }

    public Set<String> keys(LocalDate pushDate) {
        String pattern = makeAnyMemberAndDeviceTokenPattern(pushDate);
        return redisTemplate.keys(pattern);
    }

    private String makeKey(LocalDate pushDate, String memberId, String deviceToken) {
        return MISSION_RETRY_MESSAGE_PREFIX + pushDate + ":" + memberId + ":" + deviceToken;
    }

    private String makeAnyDateAndDeviceTokenPattern(String memberId) {
        return MISSION_RETRY_MESSAGE_PREFIX + "*:" + memberId + ":*";
    }

    private String makeAnyMemberAndDeviceTokenPattern(LocalDate pushDate) {
        return MISSION_RETRY_MESSAGE_PREFIX + pushDate + ":*:*";
    }

    private String makeAnyDateAndMemberPattern(String deviceToken) {
        return MISSION_RETRY_MESSAGE_PREFIX + "*:*:" + deviceToken;
    }

    private LocalDate computePushDate() {
        return LocalDate.now().plusDays(7);
    }
}
