package com.nexters.goalpanzi.application.mission.event;

public record VerifyMissionEvent(
        Long missionId,
        Integer verifiedMemberCount
) {
}
