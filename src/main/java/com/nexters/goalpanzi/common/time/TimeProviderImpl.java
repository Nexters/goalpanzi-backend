package com.nexters.goalpanzi.common.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeProviderImpl implements TimeProvider {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Override
    public int getHour() {
        return LocalDateTime.now().getHour();
    }
}
