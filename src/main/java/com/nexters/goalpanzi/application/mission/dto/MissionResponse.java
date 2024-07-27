package com.nexters.goalpanzi.application.mission.dto;

import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;

import java.time.LocalDateTime;
import java.util.List;

public record MissionResponse(
        Long hostMemberId,
        String description,
        LocalDateTime missionStartDate,
        LocalDateTime missionEndDate,
        TimeOfDay timeOfDay,
        List<DayOfWeek> missionDays,
        Integer boardCount,
        String invitationCode
) {
    public static MissionResponse from(final Mission mission) {
        return new MissionResponse(
                mission.getHostMemberId(),
                mission.getDescription(),
                mission.getMissionStartDate(),
                mission.getMissionEndDate(),
                TimeOfDay.of(mission.getUploadStartTime(), mission.getUploadEndTime()),
                mission.getMissionDays(),
                mission.getBoardCount(),
                mission.getInvitationCode().getCode()
        );
    }
}
