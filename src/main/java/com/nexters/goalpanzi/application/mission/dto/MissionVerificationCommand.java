package com.nexters.goalpanzi.application.mission.dto;

import java.time.LocalDate;

public record MissionVerificationCommand(
        Long memberId,
        Long missionId,
        LocalDate date
) {
}
