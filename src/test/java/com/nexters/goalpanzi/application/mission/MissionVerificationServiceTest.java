package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.config.redis.RedisInitializer;
import com.nexters.goalpanzi.domain.firebase.Device;
import com.nexters.goalpanzi.domain.firebase.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.*;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MissionFixture.UPLOADED_IMAGE_URL;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
@MockBeans({
        @MockBean(MemberRepository.class),
        @MockBean(MissionVerificationViewRepository.class),
        @MockBean(ObjectStorageClient.class),
        @MockBean(MissionVerificationValidator.class),
        @MockBean(MissionVerificationResponseSorter.class)
})
class MissionVerificationServiceTest {

    @Autowired
    private MissionVerificationService missionVerificationService;

    @MockBean
    private MissionVerificationRepository missionVerificationRepository;

    @MockBean
    private MissionRepository missionRepository;

    @MockBean
    private DeviceRepository deviceRepository;

    @MockBean
    private MissionMemberRepository missionMemberRepository;

    @MockBean
    private PushMessageSender pushMessageSender;

    private final Long MISSION_ID = 1L;

    @Test
    void 미션_인증_푸시_시간이고_친구_중_한_명이라도_미션을_인증한_경우_MISSION_VERIFIED_푸시_알림을_보낸다() {
        Mission mockMission = mock(Mission.class);
        when(mockMission.getId()).thenReturn(MISSION_ID);
        when(mockMission.isMissionDay()).thenReturn(true);
        when(mockMission.isVerificationStatusPushTime(anyInt())).thenReturn(true);

        MissionVerification missionVerification = new MissionVerification(
                mock(Member.class),
                mockMission,
                UPLOADED_IMAGE_URL,
                1
        );
        List<MissionVerification> verifications = List.of(missionVerification);

        when(missionRepository.getInProgressMissions()).thenReturn(List.of(mockMission));
        when(missionVerificationRepository.findAllByMissionIdAndDate(MISSION_ID, LocalDate.now())).thenReturn(verifications);

        missionVerificationService.sendVerificationPushMessage();

        verify(pushMessageSender).sendGroupData(
                MISSION_VERIFIED.getTitle(verifications.size()),
                MISSION_VERIFIED.getBody(),
                TopicGenerator.getTopic(MISSION_ID),
                MISSION_ID
        );
    }

    @Test
    void 미션_인증_푸시_시간이고_아무도_미션을_인증하지_않은_경우_MISSION_NO_ONE_VERIFIED_푸시_알림을_보낸다() {
        Mission mockMission = mock(Mission.class);
        when(mockMission.getId()).thenReturn(MISSION_ID);
        when(mockMission.isMissionDay()).thenReturn(true);
        when(mockMission.isVerificationStatusPushTime(anyInt())).thenReturn(true);

        when(missionRepository.getInProgressMissions()).thenReturn(List.of(mockMission));
        when(missionVerificationRepository.findAllByMissionIdAndDate(MISSION_ID, LocalDate.now())).thenReturn(List.of());

        missionVerificationService.sendVerificationPushMessage();

        verify(pushMessageSender).sendGroupData(
                MISSION_NO_ONE_VERIFIED.getTitle(),
                MISSION_NO_ONE_VERIFIED.getBody(),
                TopicGenerator.getTopic(MISSION_ID),
                MISSION_ID
        );
    }

    @Test
    void 미션을_인증하지_않았고_인증_마감_경고_시간인_경우_MISSION_VERIFICATION_WARNING_푸시_알림을_보낸다() {
        Long MEMBER_ID = 2L;

        Mission mockMission = mock(Mission.class);
        when(mockMission.getId()).thenReturn(MISSION_ID);
        when(mockMission.isMissionDay()).thenReturn(true);
        when(mockMission.isVerificationWarningPushTime(any(LocalTime.class))).thenReturn(true);

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(MEMBER_ID);

        Device mockDevice = mock(Device.class);
        when(mockDevice.getDeviceToken()).thenReturn(DEVICE_TOKEN);
        when(mockDevice.getPushActivationStatus()).thenReturn(true);

        MissionMember mockMissionMember = mock(MissionMember.class);
        when(mockMissionMember.getMember()).thenReturn(mockMember);

        List<MissionMember> missionMembers = List.of(mockMissionMember);

        when(missionRepository.getInProgressMissions()).thenReturn(List.of(mockMission));
        when(missionMemberRepository.findAllByMissionId(MISSION_ID)).thenReturn(missionMembers);
        when(missionVerificationRepository.findByMemberIdAndMissionIdAndDate(MEMBER_ID, MISSION_ID, LocalDate.now())).thenReturn(Optional.empty());

        when(deviceRepository.findAllByMemberId(MEMBER_ID)).thenReturn(List.of(mockDevice));

        missionVerificationService.sendVerificationWarningPushMessage();

        verify(pushMessageSender).sendIndividualData(
                MISSION_VERIFICATION_WARNING.getTitle(),
                MISSION_VERIFICATION_WARNING.getBody(),
                DEVICE_TOKEN,
                MISSION_ID
        );
    }
}