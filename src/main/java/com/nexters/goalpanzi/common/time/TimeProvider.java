package com.nexters.goalpanzi.common.time;

import java.time.LocalDateTime;

public interface TimeProvider {

    LocalDateTime now();

    int getHour();
}
