package com.nexters.goalpanzi.application.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopicSubscriberImpl implements TopicSubscriber {

    public void subscribeToTopic(final List<String> registrationTokens, final String topic) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SUBSCRIBE_TO_TOPIC);
        }
    }

    public void unsubscribeFromTopic(final List<String> registrationTokens, final String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(registrationTokens, topic);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_UNSUBSCRIBE_FROM_TOPIC);
        }
    }
}
