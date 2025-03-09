package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.Topic;
import com.nexters.goalpanzi.application.mission.event.JoinMissionEvent;
import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.common.time.TimeProvider;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.OsType;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_CANCELLATION_WARNING;
import static com.nexters.goalpanzi.domain.firebase.PushMessage.MISSION_READY;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;

class MissionMemberServiceTest extends IntegrationTest {

    @Autowired
    private MissionMemberService sut;

    @MockBean
    private TimeProvider timeProvider;

    @Autowired
    private MissionValidator missionValidator;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionMemberRepository missionMemberRepository;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private PushMessageProxy pushMessageProxy;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                sut, "eventPublisher", eventPublisher
        );
    }

    @AfterEach
    void tearDown() {
        missionMemberRepository.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        deviceRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    class joinMission {

        @Nested
        @DisplayName("호스트가 아닌 멤버가 미션에 참여하면")
        class whenMemberJoinMission {

            @Nested
            @DisplayName("푸시 알림을 활성화한 경우")
            class whenPushNotificationActivated {

                @Transactional
                @Test
                void JoinMissionEvent를_게시한다() {
                    final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                    deviceRepository.save(new Device(hostMember, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
                    final Member member = memberRepository.save(Member.socialLogin("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE));
                    member.updateNickname(NICKNAME_MEMBER_A);

                    final InvitationCode invitationCode = InvitationCode.generate();
                    final Mission mission = missionRepository.save(Mission.create(
                            hostMember.getId(),
                            DESCRIPTION,
                            LocalDateTime.now().minusDays(30),
                            LocalDateTime.now().minusDays(1),
                            TimeOfDay.EVERYDAY,
                            WEEK,
                            BOARD_COUNT,
                            invitationCode
                    ));
                    missionMemberRepository.save(new MissionMember(hostMember, mission, 0));

                    given(timeProvider.now()).willReturn(LocalDateTime.now());
                    sut.joinMission(member.getId(), invitationCode);

                    then(eventPublisher)
                            .should()
                            .publishEvent(eq(new JoinMissionEvent(mission.getId(), DEVICE_TOKEN, NICKNAME_MEMBER_A)));
                }
            }

            @Nested
            @DisplayName("푸시 알림을 비활성화한 경우")
            class whenPushNotificationDeactivated {

                @Transactional
                @Test
                void JoinMissionEvent를_게시하지_않는다() {
                    final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                    final Device device = deviceRepository.save(new Device(hostMember, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
                    device.updatePushActivationStatus(false);
                    final Member member = memberRepository.save(Member.socialLogin("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE));
                    member.updateNickname(NICKNAME_MEMBER_A);

                    final InvitationCode invitationCode = InvitationCode.generate();
                    final Mission mission = missionRepository.save(Mission.create(
                            hostMember.getId(),
                            DESCRIPTION,
                            LocalDateTime.now().minusDays(30),
                            LocalDateTime.now().minusDays(1),
                            TimeOfDay.EVERYDAY,
                            WEEK,
                            BOARD_COUNT,
                            invitationCode
                    ));
                    missionMemberRepository.save(new MissionMember(hostMember, mission, 0));

                    given(timeProvider.now()).willReturn(LocalDateTime.now());
                    sut.joinMission(member.getId(), invitationCode);

                    then(eventPublisher)
                            .should(never())
                            .publishEvent(any(JoinMissionEvent.class));
                }
            }
        }

        @Nested
        @DisplayName("호스트가 미션에 참여하면")
        class whenHostJoinMission {

            @Transactional
            @Test
            void JoinMissionEvent를_게시하지_않는다() {
                final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                final InvitationCode invitationCode = InvitationCode.generate();
                final Mission mission = missionRepository.save(Mission.create(
                        hostMember.getId(),
                        DESCRIPTION,
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now().minusDays(1),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        invitationCode
                ));

                given(timeProvider.now()).willReturn(LocalDateTime.now());

                sut.joinMission(hostMember.getId(), invitationCode);

                then(eventPublisher)
                        .should(never())
                        .publishEvent(any(JoinMissionEvent.class));
            }
        }
    }

    @Nested
    class sendReadyPushMessage {

        @Nested
        @DisplayName("최소 인원을 채워 미션이 곧 시작될 예정이면")
        class whenSatisfyMinimum {

            @Test
            void MISSION_READY_푸시_알림을_전송한다() {
                final LocalDateTime start = LocalDateTime.now();
                final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                deviceRepository.save(new Device(hostMember, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));
                final Member member = memberRepository.save(Member.socialLogin("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE));

                final Mission mission = missionRepository.save(Mission.create(
                        hostMember.getId(),
                        DESCRIPTION,
                        start,
                        start.plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.saveAll(List.of(
                        new MissionMember(hostMember, mission, 0),
                        new MissionMember(member, mission, 0)
                ));

                given(timeProvider.now()).willReturn(start.toLocalDate().atStartOfDay().minusHours(1));
                sut.sendReadyPushMessage();

                final Map<String, String> data = new HashMap<>();
                data.put("missionId", mission.getId().toString());

                then(pushMessageProxy)
                        .should()
                        .sendGroupNotificationWithData(
                                MISSION_READY.getTitle(),
                                MISSION_READY.getBody(),
                                data,
                                Topic.generate(mission.getId())
                        );
            }
        }
    }

    @Nested
    class sendCancellationWarningPushMessage {

        @Nested
        @DisplayName("최소 인원을 채우지 못해 미션이 곧 삭제될 예정이면")
        class whenNotSatisfyMinimum {

            @Test
            void MISSION_CANCELLATION_WARNING_푸시_알림을_전송한다() {
                final LocalDateTime start = LocalDateTime.now();
                final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                deviceRepository.save(new Device(hostMember, DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS));

                final Mission mission = missionRepository.save(Mission.create(
                        hostMember.getId(),
                        DESCRIPTION,
                        start,
                        start.plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.save(new MissionMember(hostMember, mission, 0));

                given(timeProvider.now()).willReturn(start.toLocalDate().atStartOfDay().minusHours(1));
                sut.sendCancellationWarningPushMessage();

                final Map<String, String> data = new HashMap<>();
                data.put("missionId", mission.getId().toString());

                then(pushMessageProxy)
                        .should()
                        .sendGroupNotificationWithData(
                                MISSION_CANCELLATION_WARNING.getTitle(),
                                MISSION_CANCELLATION_WARNING.getBody(),
                                data,
                                Topic.generate(mission.getId())
                        );
            }
        }
    }
}