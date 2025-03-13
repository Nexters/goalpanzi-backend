package com.nexters.goalpanzi.common.redisson;

import com.nexters.goalpanzi.common.annotation.RedissonLock;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class RedissonLockTestBean {

    @RedissonLock(waitTime = 1L)
    void serializeFunction(final Object args) throws InterruptedException {
        Thread.sleep(2000);
    }
}
