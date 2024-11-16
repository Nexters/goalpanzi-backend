package com.nexters.goalpanzi.common.aop;

import com.nexters.goalpanzi.common.annotation.RedissonLock;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.infrastructure.redisson.LockKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class RedissonLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(com.nexters.goalpanzi.common.annotation.RedissonLock)")
    public void lockMissionVerification(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        RedissonLock annotation = methodSignature.getMethod().getAnnotation(RedissonLock.class);
        String lockTarget = annotation.value();

        var args = joinPoint.getArgs();
        LockKey lockKey = LockKey.of(lockTarget, args);
        RLock lock = redissonClient.getLock(lockKey.toString());

        boolean lockable = lock.tryLock(annotation.waitTime(), annotation.timeUnit());
        if (!lockable) {
            throw new RuntimeException(ErrorCode.FAILED_TO_ACQUIRE_REDISSON_LOCK.getMessage());
        }
        log.info("{} acquired {} lock with key: {}.", methodName, lockTarget, lockKey);

        joinPoint.proceed();

        lock.unlock();
        log.info("{} released {} lock with key: {}.", methodName, lockTarget, lockKey);
    }
}
