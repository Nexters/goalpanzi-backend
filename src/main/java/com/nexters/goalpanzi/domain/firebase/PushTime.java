package com.nexters.goalpanzi.domain.firebase;

import lombok.Getter;

@Getter
public enum PushTime {
    MORNING(9),
    AFTERNOON(15),
    EVERYDAY(15);

    private final int hour;

    PushTime(final int hour) {
        this.hour = hour;
    }
}
