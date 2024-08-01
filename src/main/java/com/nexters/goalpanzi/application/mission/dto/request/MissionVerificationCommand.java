package com.nexters.goalpanzi.application.mission.dto.request;

import java.time.LocalDate;

public record MissionVerificationCommand(
        Long memberId,
        Long missionId,
        LocalDate date
) {
}
