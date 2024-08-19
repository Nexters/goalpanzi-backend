package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.common.time.TimeUtil;
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

    public static MissionStatus fromMission(final Mission mission) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime missionStart = TimeUtil.combineDateAndTime(
                mission.getMissionStartDate(), TimeUtil.of(mission.getUploadStartTime())
        );
        LocalDateTime missionEnd = TimeUtil.combineDateAndTime(
                mission.getMissionEndDate(), TimeUtil.of(mission.getUploadEndTime())
        );

        if (now.isBefore(missionStart)) {
            return PENDING;
        } else if (now.isAfter(missionEnd)) {
            return COMPLETED;
        } else {
            return ONGOING;
        }
    }
}
