//package com.nexters.goalpanzi.infrastructure.auth;
//
//import com.nexters.goalpanzi.domain.auth.RefreshTokenRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataRedisTest
//public class RefreshTokenRepositoryImplTest {
//
//    @Autowired
//    private RefreshTokenRepository refreshTokenRepository;
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    @TestConfiguration
//    static class RedisTestConfig {
//        @Bean
//        public RefreshTokenRepository refreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
//            return new RefreshTokenRepositoryImpl(redisTemplate);
//        }
//    }
//
//    @BeforeEach
//    public void setUp() {
//        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
//    }
//
//    @Test
//    void 키에_해당하는_refresh_토큰을_조회한다() {
//        refreshTokenRepository.save("altKey", "refreshToken", 60000);
//
//        String foundToken = refreshTokenRepository.find("altKey");
//
//        assertThat(foundToken).isEqualTo("refreshToken");
//    }
//
//    @Test
//    void 키가_존재하지_않는_경우_Null을_반환한다() {
//        String foundToken = refreshTokenRepository.find("altKey");
//
//        assertThat(foundToken).isNull();
//    }
//
//    @Test
//    void 키에_해당하는_refresh_토큰을_삭제한다() {
//        refreshTokenRepository.save("altKey", "refreshToken", 60000);
//
//        Boolean deleteResult = refreshTokenRepository.delete("altKey");
//
//        assertThat(deleteResult).isTrue();
//    }
//
//    @Test
//    void refresh_토큰을_갱신한다() {
//        refreshTokenRepository.save("altKey", "refreshToken", 60000);
//        refreshTokenRepository.save("altKey", "revisedRefreshToken", 60000);
//
//        String foundToken = refreshTokenRepository.find("altKey");
//
//        assertThat(foundToken).isEqualTo("revisedRefreshToken");
//    }
//
//    @Test
//    void ttl이_만료되어_refresh_토큰을_조회할_수_없다() throws InterruptedException {
//        refreshTokenRepository.save("altKey", "refreshToken", 100);
//
//        Thread.sleep(100);
//        String foundToken = refreshTokenRepository.find("altKey");
//
//        assertThat(foundToken).isNull();
//    }
//}
