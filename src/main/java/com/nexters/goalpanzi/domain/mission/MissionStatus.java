package com.nexters.goalpanzi.domain.mission;

import lombok.Getter;

@Getter
public enum MissionStatus {

    PENDING("대기"),
    ONGOING("진행중"),
    COMPLETED("완료");

    private final String description;

    MissionStatus(final String description) {
        this.description = description;
    }
}
