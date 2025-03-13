package com.nexters.goalpanzi.application.firebase;

import com.google.firebase.messaging.*;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PushMessageProxyImpl implements PushMessageProxy {

    public void sendIndividualNotification(String title, String body, String token) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();

        sendMessage(message, ErrorCode.FAILED_TO_SEND_INDIVIDUAL_MESSAGE);
    }

    public void sendGroupNotification(String title, String body, String topic) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .setTopic(topic)
                .build();

        sendMessage(message, ErrorCode.FAILED_TO_SEND_GROUP_MESSAGE);
    }

    public void sendIndividualData(Map<String, String> data, String token) {
        Message message = Message.builder()
                .putAllData(data)
                .setToken(token)
                .build();

        sendMessage(message, ErrorCode.FAILED_TO_SEND_INDIVIDUAL_MESSAGE);
    }

    public void sendGroupData(Map<String, String> data, String topic) {
        Message message = Message.builder()
                .putAllData(data)
                .setTopic(topic)
                .build();

        sendMessage(message, ErrorCode.FAILED_TO_SEND_GROUP_MESSAGE);
    }

    public void sendIndividualNotificationWithData(String title, String body, Map<String, String> data, String token) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(data)
                .setToken(token)
                .build();

        sendMessage(message, ErrorCode.FAILED_TO_SEND_INDIVIDUAL_MESSAGE);
    }

    public void sendGroupNotificationWithData(String title, String body, Map<String, String> data, String topic) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(data)
                .setTopic(topic)
                .build();

        sendMessage(message, ErrorCode.FAILED_TO_SEND_GROUP_MESSAGE);
    }

    private Notification makeNotification(final String title, final String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

    private void sendMessage(final Message message, final ErrorCode errorCode) {
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                log.info("더 이상 사용되지 않는 토큰입니다.");
            } else {
                throw new BaseException(errorCode, e);
            }
        }
    }

    public void subscribeToTopic(final List<String> registrationTokens, final String topic) {
        if (registrationTokens.isEmpty()) {
            return;
        }

        try {
            FirebaseMessaging.getInstance().subscribeToTopic(registrationTokens, topic);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SUBSCRIBE_TO_TOPIC);
        }
    }

    public void unsubscribeFromTopic(final List<String> registrationTokens, final String topic) {
        if (registrationTokens.isEmpty()) {
            return;
        }

        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(registrationTokens, topic);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_UNSUBSCRIBE_FROM_TOPIC);
        }
    }
}
