package com.nexters.goalpanzi.application.mission.dto.request;

import com.nexters.goalpanzi.domain.mission.MissionStatus;
import lombok.Getter;

@Getter
public enum MissionFilter {
    PENDING("대기"),
    ONGOING("진행중"),
    COMPLETED("완료");

    private final String description;

    MissionFilter(final String description) {
        this.description = description;
    }

    public MissionStatus toMissionStatus() {
        return MissionStatus.valueOf(this.name());
    }
}