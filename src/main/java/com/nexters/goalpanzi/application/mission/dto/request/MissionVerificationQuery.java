package com.nexters.goalpanzi.application.mission.dto.request;

import java.time.LocalDate;

public record MissionVerificationQuery(
        Long memberId,
        Long missionId,
        LocalDate date
) {
}
