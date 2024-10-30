package com.nexters.goalpanzi.application.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationSenderImpl implements PushNotificationSender {

    public void sendIndividualMessage(final String title, final String body, final String token) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_INDIVIDUAL_MESSAGE);
        }
    }

    public void sendGroupMessage(final String title, final String body, final String topic) {
        Notification notification = makeNotification(title, body);
        Message message = Message.builder()
                .setNotification(notification)
                .setTopic(topic)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ErrorCode.FAILED_TO_SEND_GROUP_MESSAGE);
        }
    }

    private Notification makeNotification(final String title, final String body) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}
