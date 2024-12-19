package com.nexters.goalpanzi.application.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PushMessageSenderImpl implements PushMessageSender {

    public void sendIndividualNotification(String title, String body, String token) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_INDIVIDUAL_MESSAGE, e);
        }
    }

    public void sendGroupNotification(String title, String body, String topic) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .setTopic(topic)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_GROUP_MESSAGE, e);
        }
    }

    public void sendIndividualData(Map<String, String> data, String token) {
        Message message = Message.builder()
                .putAllData(data)
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_INDIVIDUAL_MESSAGE, e);
        }
    }

    public void sendGroupData(Map<String, String> data, String topic) {
        Message message = Message.builder()
                .putAllData(data)
                .setTopic(topic)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_GROUP_MESSAGE, e);
        }
    }

    public void sendIndividualNotificationWithData(String title, String body, Map<String, String> data, String token) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(data)
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_INDIVIDUAL_MESSAGE, e);
        }
    }

    public void sendGroupNotificationWithData(String title, String body, Map<String, String> data, String topic) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(data)
                .setTopic(topic)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_GROUP_MESSAGE, e);
        }
    }

    private Notification makeNotification(final String title, final String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}
