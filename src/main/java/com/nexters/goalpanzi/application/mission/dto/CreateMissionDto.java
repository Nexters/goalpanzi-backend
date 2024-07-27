package com.nexters.goalpanzi.application.mission.dto;

import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;

import java.time.LocalDateTime;
import java.util.List;

public record CreateMissionDto(
        Long hostMemberId,
        String description,
        LocalDateTime missionStartDate,
        LocalDateTime missionEndDate,
        TimeOfDay timeOfDay,
        List<DayOfWeek> missionDays,
        Integer boardCount
) {
}
