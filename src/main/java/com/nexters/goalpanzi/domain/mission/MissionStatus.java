package com.nexters.goalpanzi.domain.mission;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public enum MissionStatus {

    PENDING("대기"),
    ONGOING("진행중"),
    COMPLETED("완료");

    private final String description;

    MissionStatus(final String description) {
        this.description = description;
    }

    public static MissionStatus fromDate(LocalDateTime missionStartDate, LocalDateTime missionEndDate) {
        LocalDateTime today = LocalDateTime.now();
        if (today.isBefore(missionStartDate)) {
            return PENDING;
        } else if (!today.isAfter(missionEndDate)) {
            return ONGOING;
        } else {
            return COMPLETED;
        }
    }
}
