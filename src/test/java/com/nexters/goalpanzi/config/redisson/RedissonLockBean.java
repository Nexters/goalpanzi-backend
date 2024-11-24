package com.nexters.goalpanzi.config.redisson;

import com.nexters.goalpanzi.common.annotation.RedissonLock;
import org.springframework.stereotype.Component;

@Component
public class RedissonLockBean {

    @RedissonLock(waitTime = 1L)
    void serializeFunction(final Object args) throws InterruptedException {
        Thread.sleep(2000);
    }
}
