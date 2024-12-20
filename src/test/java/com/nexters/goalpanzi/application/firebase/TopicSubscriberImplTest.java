package com.nexters.goalpanzi.application.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class TopicSubscriberImplTest {

    private TopicSubscriberImpl topicSubscriber;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        topicSubscriber = new TopicSubscriberImpl();

        mockStatic(FirebaseMessaging.class);
        when(FirebaseMessaging.getInstance()).thenReturn(firebaseMessaging);
    }

    @Test
    void 비어있는_토큰_리스트를_전달하는_경우_FirebaseMessaging을_호출하지_않는다() {
        topicSubscriber.subscribeToTopic(List.of(), "topic");
        topicSubscriber.unsubscribeFromTopic(List.of(), "topic");

        verifyNoInteractions(firebaseMessaging);
    }
}