package com.nexters.goalpanzi.domain.mission;

import java.time.LocalDate;

public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static DayOfWeek from(final LocalDate date) {
        return DayOfWeek.valueOf(date.getDayOfWeek().name());
    }
}
