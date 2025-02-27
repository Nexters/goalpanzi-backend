package com.nexters.goalpanzi.application.device;

import com.nexters.goalpanzi.application.firebase.Topic;
import com.nexters.goalpanzi.config.redis.RedisInitializer;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.DeviceSubscription;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.device.repository.DeviceSubscriptionRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static com.nexters.goalpanzi.domain.mission.MissionStatus.CREATED;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
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
    private MissionRepository missionRepository;

    @MockBean
    private MissionMemberRepository missionMemberRepository;

    @MockBean
    private PushMessageProxy pushMessageProxy;

    private static final Long MISSION_ID = 1L;
    private static final Long DEVICE_ID = 1L;

    private Mission MOCK_MISSION;
    private Member MOCK_MEMBER;

    @BeforeEach
    void setUp() {
        MOCK_MISSION = mock(Mission.class);
        when(MOCK_MISSION.getId()).thenReturn(MISSION_ID);

        MOCK_MEMBER = mock(Member.class);
        when(MOCK_MEMBER.getId()).thenReturn(MEMBER_ID);
    }

    @Test
    void 멤버의_디바이스_중_알림이_활성화된_디바이스_토큰은_미션을_구독한다() {
        Device mockDevice = mock(Device.class);
        when(mockDevice.getDeviceToken()).thenReturn(DEVICE_TOKEN);
        when(mockDevice.getPushActivationStatus()).thenReturn(true);

        when(deviceRepository.findAllByMemberId(MEMBER_ID)).thenReturn(List.of(mockDevice));

        deviceSubscriptionService.subscribeToMission(MEMBER_ID, MOCK_MISSION);

        verify(pushMessageProxy)
                .subscribeToTopic(List.of(DEVICE_TOKEN), Topic.generate(MISSION_ID));
    }

    @Test
    void 멤버의_디바이스_중_알림이_비활성화된_디바이스_토큰은_미션을_구독하지_않는다() {
        Device mockDevice = mock(Device.class);
        when(mockDevice.getPushActivationStatus()).thenReturn(false);

        when(deviceRepository.findAllByMemberId(MEMBER_ID)).thenReturn(List.of(mockDevice));

        deviceSubscriptionService.subscribeToMission(MEMBER_ID, MOCK_MISSION);

        verify(pushMessageProxy)
                .subscribeToTopic(List.of(), Topic.generate(MISSION_ID));
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

        verify(pushMessageProxy)
                .unsubscribeFromTopic(List.of(DEVICE_TOKEN), Topic.generate(MISSION_ID));
    }

    @Test
    void 미션_호스트는_삭제한_미션에_대해_구독을_해지한다() {
        Device mockDevice = mock(Device.class);
        when(mockDevice.getId()).thenReturn(DEVICE_ID);
        when(mockDevice.getDeviceToken()).thenReturn(DEVICE_TOKEN);
        when(mockDevice.getPushActivationStatus()).thenReturn(true);

        DeviceSubscription mockDeviceSubscription = mock(DeviceSubscription.class);
        when(mockDeviceSubscription.getDevice()).thenReturn(mockDevice);

        when(deviceRepository.findAllByMemberId(MEMBER_ID)).thenReturn(List.of(mockDevice));
        when(deviceSubscriptionRepository.findAllWithDeviceByMissionIdAndDeviceIds(MISSION_ID, List.of(mockDevice.getId())))
                .thenReturn(List.of(mockDeviceSubscription));

        deviceSubscriptionService.unsubscribeFromDeletedMissionForHost(MEMBER_ID, MISSION_ID);

        verify(pushMessageProxy)
                .unsubscribeFromTopic(List.of(DEVICE_TOKEN), Topic.generate(MISSION_ID));
    }

    @Test
    void 구독했거나_구독_가능한_미션을_찾아_구독을_시작한다() {
        Long SUBSCRIBED_MISSION_ID = 1L;
        Long UNSUBSCRIBED_MISSION_ID = 2L;

        Mission mockSubscribedMission = mock(Mission.class);
        when(mockSubscribedMission.getId()).thenReturn(SUBSCRIBED_MISSION_ID);

        Mission mockUnsubscribedMission = mock(Mission.class);
        when(mockUnsubscribedMission.getId()).thenReturn(UNSUBSCRIBED_MISSION_ID);

        MissionMember mockMissionMember = mock(MissionMember.class);
        when(mockMissionMember.getMissionStatus()).thenReturn(CREATED);
        when(mockMissionMember.getMission()).thenReturn(mockUnsubscribedMission);

        Device mockDevice = mock(Device.class);
        when(mockDevice.getId()).thenReturn(DEVICE_ID);
        when(mockDevice.getDeviceToken()).thenReturn(DEVICE_TOKEN);

        DeviceSubscription mockDeviceSubscription = mock(DeviceSubscription.class);
        when(mockDeviceSubscription.getMission()).thenReturn(mockSubscribedMission);

        when(deviceRepository.findByMemberIdAndDeviceIdentifier(MEMBER_ID, DEVICE_IDENTIFIER)).thenReturn(Optional.of(mockDevice));
        when(deviceSubscriptionRepository.findAllWithMissionAndDeviceByDeviceId(DEVICE_ID))
                .thenReturn(List.of(mockDeviceSubscription));

        when(missionMemberRepository.findAllWithMissionByMemberId(MEMBER_ID))
                .thenReturn(List.of(mockMissionMember));
        when(missionRepository.findAllById(List.of(UNSUBSCRIBED_MISSION_ID)))
                .thenReturn(List.of(mockUnsubscribedMission));

        deviceSubscriptionService.subscribeToMyMissions(MEMBER_ID, DEVICE_IDENTIFIER);

        verify(pushMessageProxy)
                .subscribeToTopic(List.of(DEVICE_TOKEN), Topic.generate(SUBSCRIBED_MISSION_ID));
        verify(pushMessageProxy)
                .subscribeToTopic(List.of(DEVICE_TOKEN), Topic.generate(UNSUBSCRIBED_MISSION_ID));
    }

    @Test
    void 구독한_미션을_모두_구독_해제한다() {
        Device mockDevice = mock(Device.class);
        when(mockDevice.getId()).thenReturn(DEVICE_ID);
        when(mockDevice.getMember()).thenReturn(MOCK_MEMBER);
        when(mockDevice.getDeviceToken()).thenReturn(DEVICE_TOKEN);
        when(mockDevice.getPushActivationStatus()).thenReturn(true);

        DeviceSubscription mockDeviceSubscription = mock(DeviceSubscription.class);
        when(mockDeviceSubscription.getMission()).thenReturn(MOCK_MISSION);

        when(deviceRepository.findAllWithMemberByDeviceIdentifier(DEVICE_IDENTIFIER))
                .thenReturn(List.of(mockDevice));
        when(deviceSubscriptionRepository.findAllWithMissionAndDeviceByDeviceId(DEVICE_ID))
                .thenReturn(List.of(mockDeviceSubscription));

        deviceSubscriptionService.unsubscribeFromMyMissions(MEMBER_ID, DEVICE_IDENTIFIER);

        verify(pushMessageProxy)
                .unsubscribeFromTopic(List.of(DEVICE_TOKEN), Topic.generate(MISSION_ID));
    }
}