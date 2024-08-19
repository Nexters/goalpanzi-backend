package com.nexters.goalpanzi.application.mission.dto.request;

import com.nexters.goalpanzi.domain.mission.MissionStatus;
import lombok.Getter;

@Getter
public enum MissionFilter {
    PENDING("대기"),
    ONGOING("진행중"),
    COMPLETED("완료"),
    ALL("모두");

    private final String description;

    MissionFilter(final String description) {
        this.description = description;
    }

    public MissionStatus toMissionStatus() {
        if (this.equals(ALL)) {
            return null;
        }
        return MissionStatus.valueOf(this.name());
    }
}