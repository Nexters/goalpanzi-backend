package com.nexters.goalpanzi.infrastructure.firebase;

import java.util.Map;

public interface PushMessageSender {

    void sendIndividualNotification(final String title, final String body, final String token);

    void sendGroupNotification(final String title, final String body, final String topic);

    void sendIndividualData(final Map<String, String> data, final String token);

    void sendGroupData(final Map<String, String> data, final String topic);

    void sendIndividualNotificationWithData(final String title, final String body, final Map<String, String> data, final String token);

    void sendGroupNotificationWithData(final String title, final String body, final Map<String, String> data, final String topic);
}
