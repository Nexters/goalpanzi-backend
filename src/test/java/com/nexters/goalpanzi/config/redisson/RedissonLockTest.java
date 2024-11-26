package com.nexters.goalpanzi.config.redisson;

import com.nexters.goalpanzi.config.redis.RedisInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = RedissonTestConfig.class
)
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
public class RedissonLockTest {

    private static final int THREAD_CNT = 2;

    @Autowired
    private RedissonLockTestBean redissonLockTestBean;

    private ExecutorService executorService;
    private AtomicInteger acquiredLockCnt;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(THREAD_CNT);
        acquiredLockCnt = new AtomicInteger(0);
    }

    @Test
    void 여러_스레드가_동시에_공유_자원에_접근할_수_없다() throws InterruptedException {
        for (int i = 0; i < THREAD_CNT; i++) {
            executorService.submit(() -> {
                try {
                    redissonLockTestBean.serializeFunction("Shared Resource");
                    acquiredLockCnt.incrementAndGet();
                } catch (InterruptedException ignored) {
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(acquiredLockCnt.get()).isNotEqualTo(THREAD_CNT);
    }

    @Test
    void 여러_스레드가_동시에_서로_다른_자원에_접근할_수_있다() throws InterruptedException {
        for (int i = 0; i < THREAD_CNT; i++) {
            String resource = "Resource" + i;
            executorService.submit(() -> {
                try {
                    redissonLockTestBean.serializeFunction(resource);
                    acquiredLockCnt.incrementAndGet();
                } catch (InterruptedException ignored) {
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(acquiredLockCnt.get()).isEqualTo(THREAD_CNT);
    }
}
