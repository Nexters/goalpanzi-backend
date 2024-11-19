//package com.nexters.goalpanzi.infrastructure.mission;
//
//import com.nexters.goalpanzi.domain.mission.repository.MissionRetryMessageRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.time.LocalDate;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataRedisTest
//class MissionRetryMessageRepositoryImplTest {
//
//    @Autowired
//    private MissionRetryMessageRepository missionRetryMessageRepository;
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    @TestConfiguration
//    static class RedisTestConfig {
//        @Bean
//        public MissionRetryMessageRepository missionRetryMessageRepository(RedisTemplate<String, String> redisTemplate) {
//            return new MissionRetryMessageRepositoryImpl(redisTemplate);
//        }
//    }
//
//    @BeforeEach
//    public void setUp() {
//        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
//    }
//
//    @Test
//    void memberId로_key를_만들고_key에_해당하는_device_token을_조회한다() {
//        missionRetryMessageRepository.save("memberId", "deviceToken", 6000);
//
//        String foundToken = missionRetryMessageRepository.find("memberId");
//
//        assertThat(foundToken).isEqualTo("deviceToken");
//    }
//
//    @Test
//    void key에_7일_후의_날짜가_포함된_key_집합을_조회한다() {
//        missionRetryMessageRepository.save("memberId1", "deviceToken", 6000);
//        missionRetryMessageRepository.save("memberId2", "deviceToken", 6000);
//
//        Set<String> keys = missionRetryMessageRepository.keys(LocalDate.now().plusDays(7));
//
//        assertThat(keys.size()).isEqualTo(2);
//    }
//
//    @Test
//    void memberId로_key를_만들고_key에_해당하는_device_token을_삭제한다() {
//        missionRetryMessageRepository.save("memberId", "deviceToken", 6000);
//
//        Boolean deleteResult = missionRetryMessageRepository.delete("memberId");
//
//        assertThat(deleteResult).isTrue();
//    }
//}