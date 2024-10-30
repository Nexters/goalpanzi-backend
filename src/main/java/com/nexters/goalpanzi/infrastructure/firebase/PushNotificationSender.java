package com.nexters.goalpanzi.infrastructure.firebase;

public interface PushNotificationSender {

    void sendIndividualMessage(final String title, final String body, final String token);

    void sendGroupMessage(final String title, final String body, final String topic);
}
