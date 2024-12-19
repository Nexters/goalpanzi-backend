package com.nexters.goalpanzi.infrastructure.mission;

import com.nexters.goalpanzi.config.redis.RedisInitializer;
import com.nexters.goalpanzi.domain.mission.repository.MissionRetryMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataRedisTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
class MissionRetryMessageRepositoryImplTest {

    @Autowired
    private MissionRetryMessageRepository missionRetryMessageRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @TestConfiguration
    static class RedisTestConfig {
        @Bean
        public MissionRetryMessageRepository missionRetryMessageRepository(RedisTemplate<String, String> redisTemplate) {
            return new MissionRetryMessageRepositoryImpl(redisTemplate);
        }
    }

    @BeforeEach
    public void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void 특정_멤버와_관련있는_device_token을_모두_조회한다() {
        missionRetryMessageRepository.save("memberId", "deviceToken1", 6000);
        missionRetryMessageRepository.save("memberId", "deviceToken2", 6000);

        List<String> tokens = missionRetryMessageRepository.findAllByMemberId("memberId");

        assertAll(
                () -> assertThat(tokens.size()).isEqualTo(2),
                () -> assertThat(tokens).containsExactlyInAnyOrder("deviceToken1", "deviceToken2")
        );
    }

    @Test
    void 특정_디바이스와_관련있는_device_token을_모두_조회한다() {
        missionRetryMessageRepository.save("memberId1", "deviceToken", 6000);
        missionRetryMessageRepository.save("memberId2", "deviceToken", 6000);

        List<String> tokens = missionRetryMessageRepository.findAllByDeviceToken("deviceToken");

        assertAll(
                () -> assertThat(tokens.size()).isEqualTo(2),
                () -> assertThat(tokens).allMatch(it -> it.equals("deviceToken"))
        );
    }

    @Test
    void key에_7일_후의_날짜가_포함된_key_집합을_조회한다() {
        missionRetryMessageRepository.save("memberId1", "deviceToken", 6000);
        missionRetryMessageRepository.save("memberId2", "deviceToken", 6000);

        Set<String> keys = missionRetryMessageRepository.keys(LocalDate.now().plusDays(7));

        assertThat(keys.size()).isEqualTo(2);
    }

    @Test
    void 특정_멤버와_관련있는_device_token을_모두_삭제한다() {
        missionRetryMessageRepository.save("memberId1", "deviceToken1", 6000);
        missionRetryMessageRepository.save("memberId1", "deviceToken2", 6000);
        missionRetryMessageRepository.save("memberId2", "deviceToken2", 6000);

        Long deletions = missionRetryMessageRepository.deleteAllByMemberId("memberId1");

        assertThat(deletions).isEqualTo(2);
    }

    @Test
    void 특정_디바이스와_관련있는_device_token을_모두_삭제한다() {
        missionRetryMessageRepository.save("memberId1", "deviceToken1", 6000);
        missionRetryMessageRepository.save("memberId1", "deviceToken2", 6000);
        missionRetryMessageRepository.save("memberId2", "deviceToken2", 6000);

        Long deletions = missionRetryMessageRepository.deleteAllByDeviceToken("deviceToken1");

        assertThat(deletions).isEqualTo(1);
    }
}