package com.nexters.goalpanzi.application.mission.dto.request;

public record MyMissionVerificationQuery(
        Long memberId,
        Long missionId,
        Integer number
) {
}
