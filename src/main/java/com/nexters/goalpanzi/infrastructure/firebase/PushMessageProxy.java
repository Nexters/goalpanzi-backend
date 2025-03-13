package com.nexters.goalpanzi.infrastructure.firebase;

import java.util.List;
import java.util.Map;

public interface PushMessageProxy {

    void sendIndividualNotification(final String title, final String body, final String token);

    void sendGroupNotification(final String title, final String body, final String topic);

    void sendIndividualData(final Map<String, String> data, final String token);

    void sendGroupData(final Map<String, String> data, final String topic);

    void sendIndividualNotificationWithData(final String title, final String body, final Map<String, String> data, final String token);

    void sendGroupNotificationWithData(final String title, final String body, final Map<String, String> data, final String topic);

    void subscribeToTopic(final List<String> registrationTokens, final String topic);

    void unsubscribeFromTopic(final List<String> registrationTokens, final String topic);
}
