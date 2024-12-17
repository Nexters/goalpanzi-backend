package com.nexters.goalpanzi.application.mission.event;

public record ReserveMissionRetryPushMessageEvent(
        Long memberId,
        String deviceToken
) {
}
