package com.nexters.goalpanzi.presentation.fcmtest;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "푸시 알림 테스트")
@RequiredArgsConstructor
@RequestMapping("/internal/fcm-test")
@RestController
public class FcmTestController {

    private final PushMessageSender pushMessageSender;
    private final TopicSubscriber topicSubscriber;

    @Operation(summary = "개별 메시지 전송")
    @GetMapping("individual-message")
    ResponseEntity<Void> sendIndividualNotification(
            @Schema(description = "deviceToken", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam final String deviceToken) {
        pushMessageSender.sendIndividualNotification("개별 메시지 테스트", deviceToken + "으로 개별 메시지를 전송합니다.", deviceToken);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "그룹 메시지 전송")
    @GetMapping("group-message")
    ResponseEntity<Void> sendGroupNotification(
            @Schema(description = "deviceToken", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam final String deviceToken
    ) {
        String topic = "topic-test";
        topicSubscriber.subscribeToTopic(List.of(deviceToken), topic);
        pushMessageSender.sendGroupNotification("그룹 메시지 테스트", topic + "으로 그룹 메시지를 전송합니다.", topic);
        topicSubscriber.unsubscribeFromTopic(List.of(deviceToken), topic);

        return ResponseEntity.ok().build();
    }

    @Operation
    @GetMapping("data")
    ResponseEntity<Void> sendData(
            @Schema(description = "deviceToken", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam final String deviceToken
    ) {
        pushMessageSender.sendIndividualData("Data 타입 테스트", "Data 타입 전송 테스트입니다.", deviceToken, 1L);

        return ResponseEntity.ok().build();
    }

    @Operation
    @GetMapping("notification-with-data")
    ResponseEntity<Void> sendNotificationWithData(
            @Schema(description = "deviceToken", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam final String deviceToken
    ) {
        pushMessageSender.sendNotificationWithData(
                "혼합 메시지 테스트",
                "notification { title: string, body: string }, data { missionId: string }",
                TopicGenerator.getTopic(1L)
        );

        return ResponseEntity.ok().build();
    }
}
