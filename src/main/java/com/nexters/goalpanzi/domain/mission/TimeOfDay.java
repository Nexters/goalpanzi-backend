package com.nexters.goalpanzi.domain.mission;

import lombok.Getter;

import java.util.Arrays;

import static com.nexters.goalpanzi.exception.ErrorCode.INVALID_UPLOAD_TIME_OF_DAY;

@Getter
public enum TimeOfDay {
    MORNING("00:00", "12:00"),
    AFTERNOON("12:00", "24:00"),
    EVERYDAY("00:00", "24:00"),
    ;

    private final String startTime;
    private final String endTime;

    TimeOfDay(final String startTime, final String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static TimeOfDay of(final String startTime, final String endTime) {
        return Arrays.stream(TimeOfDay.values())
                .filter(timeOfDay -> timeOfDay.startTime.equals(startTime) && timeOfDay.endTime.equals(endTime))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(INVALID_UPLOAD_TIME_OF_DAY.getMessage()));
    }
}
