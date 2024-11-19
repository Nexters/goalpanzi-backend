package com.nexters.goalpanzi.infrastructure.mission;

import com.nexters.goalpanzi.domain.mission.repository.MissionRetryMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class MissionRetryMessageRepositoryImpl implements MissionRetryMessageRepository {

    private static final String MISSION_RETRY_MESSAGE_PREFIX = "mission_retry_message:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String memberId, String deviceToken, long ttl) {
        LocalDate sendDate = LocalDate.now().plusDays(7);
        String key = makeKey(sendDate, memberId);
        redisTemplate.opsForValue().set(key, deviceToken, Duration.ofMillis(ttl));
    }

    public String find(String memberId) {
        String pattern = makeAnyDatePattern(memberId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.isEmpty()) {
            return null;
        }
        System.out.println(keys);
        String key = String.valueOf(keys.iterator().next());
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String memberId) {
        String pattern = makeAnyDatePattern(memberId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.isEmpty()) {
            return true;
        }
        String key = String.valueOf(keys.iterator().next());
        return redisTemplate.delete(key);
    }

    public Set<String> keys(LocalDate sendDate) {
        String pattern = makeAnyMemberPattern(sendDate);
        return redisTemplate.keys(pattern);
    }

    private String makeKey(LocalDate sendDate, String memberId) {
        return MISSION_RETRY_MESSAGE_PREFIX + sendDate + ":" + memberId;
    }

    private String makeAnyDatePattern(String memberId) {
        return MISSION_RETRY_MESSAGE_PREFIX + "*:" + memberId;
    }

    private String makeAnyMemberPattern(LocalDate sendDate) {
        return MISSION_RETRY_MESSAGE_PREFIX + sendDate + ":*";
    }
}
