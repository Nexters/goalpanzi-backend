package com.nexters.goalpanzi.application.mission.event;

public record CompleteMissionEvent(
        Long missionId,
        String nickname
) {
}
