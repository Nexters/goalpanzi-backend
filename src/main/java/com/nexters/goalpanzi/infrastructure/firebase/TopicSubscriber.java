package com.nexters.goalpanzi.infrastructure.firebase;

import java.util.List;

public interface TopicSubscriber {

    void subscribeToTopic(final List<String> registrationTokens, final String topic);

    void unsubscribeFromTopic(final List<String> registrationTokens, final String topic);
}
