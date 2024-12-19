package com.nexters.goalpanzi.application.mission.event;

public record UpdateMissionRetryPushMessageEvent(
        Long memberId,
        String deviceToken
) {
}
