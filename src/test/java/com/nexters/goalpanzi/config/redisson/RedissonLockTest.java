package com.nexters.goalpanzi.config.redisson;

import com.nexters.goalpanzi.common.annotation.RedissonLock;
import com.nexters.goalpanzi.config.redis.RedisInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
public class RedissonLockTest {

    @RedissonLock(waitTime = 1L)
    void serializeFunction() throws InterruptedException {
        Thread.sleep(2000);
    }

    @Test
    void 여러_스레드가_동시에_공유_자원에_접근할_수_없다() {
        int threadCnt = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);

        AtomicInteger acquiredLockCnt = new AtomicInteger(0);
        for (int i = 0; i < threadCnt; i++) {
            executorService.submit(() -> {
                try {
                    serializeFunction();
                    acquiredLockCnt.incrementAndGet();
                } catch (InterruptedException ignored) {
                }
            });
        }

        assertThat(acquiredLockCnt.get()).isNotEqualTo(threadCnt);
    }
}
