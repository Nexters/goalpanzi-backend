package com.nexters.goalpanzi.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLock {
    String value() default "Unknown";

    long waitTime() default 5;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
