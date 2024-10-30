package com.nexters.goalpanzi.presentation.fcmtest;

import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "푸시 알림 테스트")
@RequiredArgsConstructor
@RequestMapping("/api/fcm-test")
@RestController
public class FcmTestController {

    private final PushNotificationSender pushNotificationSender;
    private final TopicSubscriber topicSubscriber;

    @Operation(summary = "개별 메시지 전송")
    @GetMapping("individual-message")
    ResponseEntity<Void> sendIndividualMessage(
            @Schema(description = "deviceToken", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestBody final String deviceToken) {
        pushNotificationSender.sendIndividualMessage("개별 메시지 테스트", deviceToken + "으로 개별 메시지를 전송합니다.", deviceToken);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "그룹 메시지 전송")
    @GetMapping("group-message")
    ResponseEntity<Void> sendGroupMessage(
            @Schema(description = "deviceToken", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestBody final String deviceToken
    ) {
        String topic = "topic-test";
        topicSubscriber.subscribeToTopic(List.of(deviceToken), topic);
        pushNotificationSender.sendGroupMessage("그룹 메시지 테스트", topic + "으로 그룹 메시지를 전송합니다.", topic);
        topicSubscriber.unsubscribeFromTopic(List.of(deviceToken), topic);

        return ResponseEntity.ok().build();
    }
}
