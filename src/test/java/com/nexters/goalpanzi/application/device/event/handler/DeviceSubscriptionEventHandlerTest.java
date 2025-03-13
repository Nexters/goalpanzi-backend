package com.nexters.goalpanzi.application.device.event.handler;

import com.nexters.goalpanzi.application.device.DeviceSubscriptionService;
import com.nexters.goalpanzi.application.device.event.UpdateDeviceTokenEvent;
import com.nexters.goalpanzi.application.device.event.UpdatePushActivationStatusEvent;
import com.nexters.goalpanzi.config.RedisInitializer;
import com.nexters.goalpanzi.config.SyncEventConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.then;

@SpringBootTest(
        classes = {SyncEventConfig.class}
)
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
class DeviceSubscriptionEventHandlerTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private DeviceSubscriptionEventHandler deviceSubscriptionEventHandler;

    @MockBean
    private DeviceSubscriptionService deviceSubscriptionService;

    @Nested
    class handleUpdateDeviceTokenEvent {

        @Nested
        @DisplayName("기존 디바이스 토큰이 null이면")
        class whenOriginalDeviceTokenIsNull {

            @Test
            void 토픽_구독만_진행한다() {
                final UpdateDeviceTokenEvent event = new UpdateDeviceTokenEvent(1L, 1L, null);

                transactionTemplate.execute(status -> {
                    eventPublisher.publishEvent(event);
                    return null;
                });

                then(deviceSubscriptionService)
                        .should()
                        .subscribeToMyMissions(event.memberId(), event.deviceId());
            }
        }

        @Nested
        @DisplayName("기존 디바이스 토큰이 null이 아니면")
        class whenOriginalDeviceTokenIsNotNull {

            @Test
            void 기존_디바이스_토큰이_구독한_토픽을_구독_취소하고_새로운_디바이스_토큰으로_토픽을_구독한다() {
                final UpdateDeviceTokenEvent event = new UpdateDeviceTokenEvent(1L, 1L, "deprecatedDeviceToken");

                transactionTemplate.execute(status -> {
                    eventPublisher.publishEvent(event);
                    return null;
                });

                assertAll(
                        () -> then(deviceSubscriptionService)
                                .should()
                                .unsubscribeFromMyMissions(event.memberId(), event.deviceId(), event.deprecatedDeviceToken()),
                        () -> then(deviceSubscriptionService)
                                .should()
                                .subscribeToMyMissions(event.memberId(), event.deviceId())
                );
            }
        }
    }

    @Nested
    class handleUpdatePushActivationStatusEvent {

        @Nested
        @DisplayName("푸시 알림을 활성화하면")
        class whenActivatePush {

            @Test
            void 토픽을_구독한다() {
                final UpdatePushActivationStatusEvent event = new UpdatePushActivationStatusEvent(1L, 1L, true, "deviceToken");

                transactionTemplate.execute(status -> {
                    eventPublisher.publishEvent(event);
                    return null;
                });

                then(deviceSubscriptionService)
                        .should()
                        .subscribeToMyMissions(event.memberId(), event.deviceId());
            }
        }

        @Nested
        @DisplayName("푸시 알림을 비활성화하면")
        class whenDeactivatePush {

            @Test
            void 토픽을_구독_취소한다() {
                final UpdatePushActivationStatusEvent event = new UpdatePushActivationStatusEvent(1L, 1L, false, "deviceToken");

                transactionTemplate.execute(status -> {
                    eventPublisher.publishEvent(event);
                    return null;
                });

                then(deviceSubscriptionService)
                        .should()
                        .unsubscribeFromMyMissions(event.memberId(), event.deviceId(), event.deviceToken());
            }
        }
    }
}