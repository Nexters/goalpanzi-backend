package com.nexters.goalpanzi.application.mission.event.handler;

import com.nexters.goalpanzi.application.member.event.UpdateDeviceTokenEvent;
import com.nexters.goalpanzi.application.member.event.UpdatePushActivationStatusEvent;
import com.nexters.goalpanzi.application.mission.MissionMemberService;
import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.config.event.SyncEventConfig;
import com.nexters.goalpanzi.config.redis.RedisInitializer;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        classes = {SyncEventConfig.class}
)
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
@MockBeans({
        @MockBean(MissionVerificationService.class)
})
class MissionMemberEventHandlerTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MissionMemberEventHandler missionMemberEventHandler;

    @MockBean
    private MissionMemberService missionMemberService;

    @MockBean
    private PushMessageSender pushMessageSender;

    @Test
    void 기존_디바이스_토큰이_null인_상황에서_디바이스_토큰을_갱신하면_토픽_구독만_진행한다() {
        UpdateDeviceTokenEvent event = new UpdateDeviceTokenEvent(1L, null, "deviceToken");

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        verify(missionMemberService, times(1))
                .subscribeToMyMissions(event.memberId(), event.deviceToken());
    }

    @Test
    void 기존_디바이스_토큰이_null이_아닌_상황에서_디바이스_토큰을_갱신하면_기존_디바이스_토큰이_구독한_토픽을_구독_취소하고_새로운_디바이스_토큰으로_토픽을_구독한다() {
        UpdateDeviceTokenEvent event = new UpdateDeviceTokenEvent(1L, "deprecatedDeviceToken", "deviceToken");

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        verify(missionMemberService, times(1))
                .unsubscribeFromMyMissions(event.memberId(), event.deprecatedDeviceToken());
        verify(missionMemberService, times(1))
                .subscribeToMyMissions(event.memberId(), event.deviceToken());
    }

    @Test
    void 푸시_알림을_활성화하는_경우_토픽을_구독한다() {
        UpdatePushActivationStatusEvent event = new UpdatePushActivationStatusEvent(1L, "deviceToken", true);

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        verify(missionMemberService, times(1))
                .subscribeToMyMissions(event.memberId(), event.deviceToken());
    }

    @Test
    void 푸시_알림을_비활성화하는_경우_토픽을_구독_취소한다() {
        UpdatePushActivationStatusEvent event = new UpdatePushActivationStatusEvent(1L, "deviceToken", false);

        transactionTemplate.execute(status -> {
            eventPublisher.publishEvent(event);
            return null;
        });

        verify(missionMemberService, times(1))
                .unsubscribeFromMyMissions(event.memberId(), event.deviceToken());
    }
}