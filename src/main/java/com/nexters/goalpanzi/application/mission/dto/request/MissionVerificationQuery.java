package com.nexters.goalpanzi.application.mission.dto.request;

import com.nexters.goalpanzi.domain.mission.VerificationOrderBy;

import java.time.LocalDate;

public record MissionVerificationQuery(
        Long memberId,
        Long missionId,
        LocalDate date,
        VerificationOrderBy orderBy
) {
}
