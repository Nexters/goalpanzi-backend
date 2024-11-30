package com.nexters.goalpanzi.infrastructure.firebase;

public interface PushNotificationSender {

    void sendIndividualNotification(final String title, final String body, final String token);

    void sendGroupNotification(final String title, final String body, final String topic);

    void sendIndividualData(final String title, final String body, final String token, final Long missionId, final Long memberId);

    void sendGroupData(final String title, final String body, final String topic, final Long missionId, final Long memberId);
}
