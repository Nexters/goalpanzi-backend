package com.nexters.goalpanzi.application.device;

import com.nexters.goalpanzi.config.redis.RedisInitializer;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.DeviceSubscription;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.device.repository.DeviceSubscriptionRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.MEMBER_ID;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
class DeviceSubscriptionServiceTest {

    @Autowired
    private DeviceSubscriptionService deviceSubscriptionService;

    @MockBean
    private DeviceRepository deviceRepository;

    @MockBean
    private DeviceSubscriptionRepository deviceSubscriptionRepository;

    @MockBean
    private TopicSubscriber topicSubscriber;

    private static Long MISSION_ID = 1L;

    @Test
    void 멤버의_디바이스_중_알림이_활성화된_디바이스_토큰은_미션을_구독한다() {
        Mission mockMission = mock(Mission.class);
        when(mockMission.getId()).thenReturn(MISSION_ID);

        Device mockDevice = mock(Device.class);
        when(mockDevice.getDeviceToken()).thenReturn(DEVICE_TOKEN);
        when(mockDevice.getPushActivationStatus()).thenReturn(true);

        when(deviceRepository.findAllByMemberId(MEMBER_ID)).thenReturn(List.of(mockDevice));

        deviceSubscriptionService.subscribeToMission(MEMBER_ID, mockMission);

        verify(topicSubscriber)
                .subscribeToTopic(List.of(DEVICE_TOKEN), TopicGenerator.getTopic(MISSION_ID));
    }

    @Test
    void 멤버의_디바이스_중_알림이_비활성화된_디바이스_토큰은_미션을_구독하지_않는다() {
        Mission mockMission = mock(Mission.class);
        when(mockMission.getId()).thenReturn(MISSION_ID);

        Device mockDevice = mock(Device.class);
        when(mockDevice.getPushActivationStatus()).thenReturn(false);

        when(deviceRepository.findAllByMemberId(MEMBER_ID)).thenReturn(List.of(mockDevice));

        deviceSubscriptionService.subscribeToMission(MEMBER_ID, mockMission);

        verify(topicSubscriber)
                .subscribeToTopic(List.of(), TopicGenerator.getTopic(MISSION_ID));
    }

    @Test
    void 특정_미션을_구독한_디바이스를_모두_찾아_구독을_해지한다() {
        Device mockDevice = mock(Device.class);
        when(mockDevice.getDeviceToken()).thenReturn(DEVICE_TOKEN);

        DeviceSubscription mockDeviceSubscription = mock(DeviceSubscription.class);
        when(mockDeviceSubscription.getDevice()).thenReturn(mockDevice);

        when(deviceSubscriptionRepository.findAllWithDeviceAndMissionByMissionId(MISSION_ID))
                .thenReturn(List.of(mockDeviceSubscription));

        deviceSubscriptionService.unsubscribeFromMission(MISSION_ID);

        verify(topicSubscriber)
                .unsubscribeFromTopic(List.of(DEVICE_TOKEN), TopicGenerator.getTopic(MISSION_ID));
    }
}