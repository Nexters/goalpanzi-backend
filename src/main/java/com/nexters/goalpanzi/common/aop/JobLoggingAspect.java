package com.nexters.goalpanzi.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class JobLoggingAspect {

    @Around("execution(* com.nexters.goalpanzi.schedule.*.executeInternal(..))")
    public void execute(final ProceedingJoinPoint joinPoint) throws Throwable {
        String jobName = joinPoint.getTarget().getClass().getSimpleName();

        log.info("{} started.", jobName);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            joinPoint.proceed();
        } catch (Exception e) {
            log.error("Error occurred while executing {}", jobName, e);
        }

        stopWatch.stop();
        log.info("{} finished. Elapsed time: {} ms", jobName, stopWatch.getTime());
    }
}
