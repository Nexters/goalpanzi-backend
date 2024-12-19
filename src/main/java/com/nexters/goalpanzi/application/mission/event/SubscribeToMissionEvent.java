package com.nexters.goalpanzi.application.mission.event;

import com.nexters.goalpanzi.domain.mission.Mission;

public record SubscribeToMissionEvent(
        Long memberId,
        Mission mission
) {
}
