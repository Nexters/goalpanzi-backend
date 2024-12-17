package com.nexters.goalpanzi.application.device.event.handler;

import com.nexters.goalpanzi.application.device.DeviceSubscriptionService;
import com.nexters.goalpanzi.application.member.event.UpdateDeviceTokenEvent;
import com.nexters.goalpanzi.application.member.event.UpdatePushActivationStatusEvent;
import com.nexters.goalpanzi.config.event.SyncEventConfig;
import com.nexters.goalpanzi.config.redis.RedisInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

import static org.mockito.Mockito.verify;

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

    @Test
    void 기존_디바이스_토큰이_null인_상황에서_디바이스_토큰을_갱신하면_토픽_구독만_진행한다() {
        UpdateDeviceTokenEvent event = new UpdateDeviceTokenEvent(1L, 1L, null);

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        verify(deviceSubscriptionService)
                .subscribeToMyMissions(event.memberId(), event.deviceId());
    }

    @Test
    void 기존_디바이스_토큰이_null이_아닌_상황에서_디바이스_토큰을_갱신하면_기존_디바이스_토큰이_구독한_토픽을_구독_취소하고_새로운_디바이스_토큰으로_토픽을_구독한다() {
        UpdateDeviceTokenEvent event = new UpdateDeviceTokenEvent(1L, 1L, "deprecatedDeviceToken");

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        verify(deviceSubscriptionService)
                .unsubscribeFromMyMissions(event.memberId(), event.deviceId(), event.deprecatedDeviceToken());
        verify(deviceSubscriptionService)
                .subscribeToMyMissions(event.memberId(), event.deviceId());
    }

    @Test
    void 푸시_알림을_활성화하는_경우_토픽을_구독한다() {
        UpdatePushActivationStatusEvent event = new UpdatePushActivationStatusEvent(1L, 1L, true, "deviceToken");

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        verify(deviceSubscriptionService)
                .subscribeToMyMissions(event.memberId(), event.deviceId());
    }

    @Test
    void 푸시_알림을_비활성화하는_경우_토픽을_구독_취소한다() {
        UpdatePushActivationStatusEvent event = new UpdatePushActivationStatusEvent(1L, 1L, false, "deviceToken");

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        verify(deviceSubscriptionService)
                .unsubscribeFromMyMissions(event.memberId(), event.deviceId(), event.deviceToken());
    }
}