package com.nexters.goalpanzi.common.time;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static LocalTime of(final String timeString) {
        if ("24:00".equals(timeString)) {
            return LocalTime.MAX;
        }
        return LocalTime.parse(timeString, TIME_FORMATTER);
    }

    public static LocalDateTime combineDateAndTime(final LocalDateTime date, final LocalTime time) {
        return LocalDateTime.of(
                date.getYear(),
                date.getMonth(),
                date.getDayOfMonth(),
                time.getHour(),
                time.getMinute()
        );
    }
}
