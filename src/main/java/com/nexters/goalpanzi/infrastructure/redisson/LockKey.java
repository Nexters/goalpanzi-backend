package com.nexters.goalpanzi.infrastructure.redisson;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public class LockKey {

    private static final String LOCK_PREFIX = "LOCK";

    private final String target;
    private final String value;

    public static LockKey of(final String target, final Object... args) {
        return new LockKey(target, Arrays.toString(args));
    }

    @Override
    public String toString() {
        return LOCK_PREFIX + ":" + target + ":" + value;
    }
}
