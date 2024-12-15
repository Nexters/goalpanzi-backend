package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.config.redis.RedisInitializer;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRetryMessageRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_CANCELLATION_WARNING;
import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_READY;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
@MockBeans({
        @MockBean(MissionRetryMessageRepository.class)
})
class MissionMemberServiceTest {

    @Autowired
    private MissionMemberService missionMemberService;

    @MockBean
    private MissionValidator missionValidator;

    @MockBean
    private MissionMemberRepository missionMemberRepository;

    @MockBean
    private MissionRepository missionRepository;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private PushMessageSender pushMessageSender;

    @MockBean
    private TopicSubscriber topicSubscriber;

    private static Long MISSION_ID = 1L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                missionMemberService, "eventPublisher", eventPublisher
        );
    }

    @Test
    void 호스트가_아닌_멤버가_미션에_참여했을_때_JoinMissionEvent를_발행한다() {
        InvitationCode INVITATION_CODE = InvitationCode.generate();
        Long HOST_ID = MEMBER_ID + 1;

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(MEMBER_ID);
        when(mockMember.getNickname()).thenReturn(NICKNAME_MEMBER_A);

        Member mockHostMember = mock(Member.class);
        when(mockHostMember.getId()).thenReturn(HOST_ID);
        when(mockHostMember.getDeviceToken()).thenReturn(DEVICE_TOKEN);
        when(mockHostMember.isPushActivated()).thenReturn(true);

        Mission mockMission = mock(Mission.class);
        when(mockMission.getId()).thenReturn(MISSION_ID);
        when(mockMission.getHostMemberId()).thenReturn(HOST_ID);
        when(mockMission.isHostMember(MEMBER_ID)).thenReturn(false);

        when(memberRepository.getMember(mockMember.getId())).thenReturn(mockMember);
        when(memberRepository.getMember(mockHostMember.getId())).thenReturn(mockHostMember);

        when(missionRepository.findByInvitationCode(INVITATION_CODE)).thenReturn(Optional.of(mockMission));
        when(missionMemberRepository.findByMemberIdAndMissionId(MEMBER_ID, MISSION_ID)).thenReturn(Optional.empty());

        missionMemberService.joinMission(MEMBER_ID, INVITATION_CODE);

        verify(eventPublisher).publishEvent(
                eq(new JoinMissionEvent(MISSION_ID, DEVICE_TOKEN, NICKNAME_MEMBER_A))
        );
    }

    @Test
    void 호스트가_미션에_참여했을_때_JoinMissionEvent를_발행하지_않는다() {
        InvitationCode INVITATION_CODE = InvitationCode.generate();

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(MEMBER_ID);
        when(mockMember.getNickname()).thenReturn(NICKNAME_HOST);
        when(mockMember.getDeviceToken()).thenReturn(DEVICE_TOKEN);
        when(mockMember.isPushActivated()).thenReturn(true);

        Mission mockMission = mock(Mission.class);
        when(mockMission.getId()).thenReturn(MISSION_ID);
        when(mockMission.getHostMemberId()).thenReturn(MEMBER_ID);
        when(mockMission.isHostMember(MEMBER_ID)).thenReturn(true);

        when(memberRepository.getMember(mockMember.getId())).thenReturn(mockMember);

        when(missionRepository.findByInvitationCode(INVITATION_CODE)).thenReturn(Optional.of(mockMission));
        when(missionMemberRepository.findByMemberIdAndMissionId(MEMBER_ID, MISSION_ID)).thenReturn(Optional.empty());

        missionMemberService.joinMission(MEMBER_ID, INVITATION_CODE);

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void 최소_인원을_채워_미션이_곧_시작될_경우_MISSION_READY_푸시_알림을_보낸다() {
        Mission mockMission = mock(Mission.class);

        when(mockMission.isReadyTime(any(LocalDateTime.class))).thenReturn(true);
        when(mockMission.getId()).thenReturn(MISSION_ID);

        when(missionRepository.getReadyMissions()).thenReturn(List.of(mockMission));
        when(missionValidator.hasEnoughMember(MISSION_ID)).thenReturn(true);

        missionMemberService.sendReadyPushMessage();

        verify(pushMessageSender).sendGroupData(
                MISSION_READY.getTitle(),
                MISSION_READY.getBody(),
                TopicGenerator.getTopic(MISSION_ID),
                MISSION_ID
        );
    }

    @Test
    void 최소_인원을_채우지_못해_미션이_취소될_위험이_있는_경우_MISSION_CANCELLATION_WARNING_푸시_알림을_보낸다() {
        Mission mockMission = mock(Mission.class);

        when(mockMission.isReadyTime(any(LocalDateTime.class))).thenReturn(true);
        when(mockMission.getId()).thenReturn(MISSION_ID);

        when(missionRepository.getReadyMissions()).thenReturn(List.of(mockMission));
        when(missionValidator.hasEnoughMember(MISSION_ID)).thenReturn(false);

        missionMemberService.sendCancellationWarningPushMessage();

        verify(pushMessageSender).sendGroupData(
                MISSION_CANCELLATION_WARNING.getTitle(),
                MISSION_CANCELLATION_WARNING.getBody(),
                TopicGenerator.getTopic(MISSION_ID),
                MISSION_ID
        );
    }
}