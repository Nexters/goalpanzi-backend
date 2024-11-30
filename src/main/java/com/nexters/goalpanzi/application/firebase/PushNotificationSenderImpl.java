package com.nexters.goalpanzi.application.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nexters.goalpanzi.application.firebase.dto.response.Data;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationSenderImpl implements PushNotificationSender {

    public void sendIndividualNotification(final String title, final String body, final String token) {
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

    public void sendGroupNotification(final String title, final String body, final String topic) {
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

    private Notification makeNotification(final String title, final String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }

    public void sendIndividualData(final String title, final String body, final String token, final Long missionId, final Long memberId) {
        Data data = makeData(title, body, missionId, memberId);
        Message message = Message.builder()
                .putAllData(data.toMap())
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_INDIVIDUAL_MESSAGE, e);
        }
    }

    public void sendGroupData(final String title, final String body, final String topic, final Long missionId, final Long memberId) {
        Data data = makeData(title, body, missionId, memberId);
        Message message = Message.builder()
                .putAllData(data.toMap())
                .setTopic(topic)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_GROUP_MESSAGE, e);
        }
    }

    private Data makeData(final String title, final String body, final Long missionId, final Long memberId) {
        return new Data(title, body, memberId, missionId);
    }
}
