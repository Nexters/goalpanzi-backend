package com.nexters.goalpanzi.application.mission.event;

public record JoinMissionEvent(
        Long missionId,
        String deviceToken,
        String nickname
) {
}
