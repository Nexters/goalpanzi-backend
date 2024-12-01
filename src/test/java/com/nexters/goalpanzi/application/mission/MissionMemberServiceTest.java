package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.config.redis.RedisInitializer;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRetryMessageRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageSender;
import com.nexters.goalpanzi.infrastructure.firebase.TopicSubscriber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_CANCELLATION_WARNING;
import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_READY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(
        initializers = {RedisInitializer.class}
)
@MockBeans({
        @MockBean(MemberRepository.class),
        @MockBean(MissionRetryMessageRepository.class),
        @MockBean(ApplicationEventPublisher.class)
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
    private PushMessageSender pushMessageSender;

    @MockBean
    private TopicSubscriber topicSubscriber;

    private static Long MISSION_ID = 1L;

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