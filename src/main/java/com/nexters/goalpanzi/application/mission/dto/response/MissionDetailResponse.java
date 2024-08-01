package com.nexters.goalpanzi.application.mission.dto.response;

import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;

import java.time.LocalDateTime;
import java.util.List;

public record MissionDetailResponse(
        Long hostMemberId,
        String description,
        LocalDateTime missionStartDate,
        LocalDateTime missionEndDate,
        TimeOfDay timeOfDay,
        List<DayOfWeek> missionDays,
        Integer boardCount,
        String invitationCode
) {

    public static MissionDetailResponse from(final Mission mission) {
        return new MissionDetailResponse(
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