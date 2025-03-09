package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.firebase.Topic;
import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.ViewMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.common.time.TimeProvider;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.OsType;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import com.nexters.goalpanzi.infrastructure.firebase.PushMessageProxy;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery.SortType;
import static com.nexters.goalpanzi.domain.firebase.PushMessage.*;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;

class MissionVerificationServiceTest extends IntegrationTest {

    @Autowired
    private MissionVerificationService sut;

    @Autowired
    private MissionVerificationRepository missionVerificationRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionMemberRepository missionMemberRepository;

    @Autowired
    private MissionVerificationViewRepository missionVerificationViewRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @MockBean
    private PushMessageProxy pushMessageProxy;

    @MockBean
    private TimeProvider timeProvider;

    @Autowired
    private MissionVerificationValidator missionVerificationValidator;

    @Autowired
    private MissionVerificationResponseSorter missionVerificationResponseSorter;

    @AfterEach
    void tearDown() {
        missionVerificationViewRepository.deleteAllInBatch();
        missionVerificationRepository.deleteAllInBatch();
        missionMemberRepository.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        deviceRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    class getMyVerification {

        @Nested
        @DisplayName("보드칸 번호에 해당하는 나의 미션 인증 내역이 있다면")
        class whenNumberFound {

            @Test
            void 나의_미션_인증_내역을_조회한다() {
                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                final Mission mission = missionRepository.save(Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.save(new MissionMember(member, mission, 1));
                final MissionVerification verification = missionVerificationRepository.save(new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1));

                final MissionVerificationResponse actual = sut.getMyVerification(
                        new MyMissionVerificationQuery(member.getId(), mission.getId(), 1)
                );

                BDDAssertions.then(actual)
                        .extracting("imageUrl", "verifiedAt")
                        .containsExactly(verification.getImageUrl(), verification.getCreatedAt());
            }
        }

        @Nested
        @DisplayName("보드칸 번호에 해당하는 나의 미션 인증 내역이 없다면")
        class whenNumberNotFound {

            @Test
            void NOT_FOUND_VERIFICATION_예외를_반환한다() {
                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                final Mission mission = missionRepository.save(Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.save(new MissionMember(member, mission, 0));

                thenThrownBy(() -> sut.getMyVerification(
                        new MyMissionVerificationQuery(member.getId(), mission.getId(), 1)))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage(ErrorCode.NOT_FOUND_VERIFICATION.getMessage());
            }
        }
    }

    @Nested
    class getVerifications {

        @Nested
        @DisplayName("date가 null이면")
        class whenDateIsNull {

            @Transactional
            @Test
            void 오늘_인증한_미션_인증_내역들을_조회한다() {
                final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                hostMember.updateNickname(NICKNAME_HOST);
                final Member member = memberRepository.save(Member.socialLogin("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE));
                member.updateNickname(NICKNAME_MEMBER_A);
                final Mission mission = missionRepository.save(Mission.create(
                        hostMember.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.saveAll(List.of(
                        new MissionMember(hostMember, mission, 1),
                        new MissionMember(member, mission, 1)
                ));
                missionVerificationRepository.saveAll(List.of(
                        new MissionVerification(hostMember, mission, UPLOADED_IMAGE_URL, 1),
                        new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1)
                ));

                final MissionVerificationsResponse actual = sut.getVerifications(
                        new MissionVerificationQuery(member.getId(), mission.getId(), null, SortType.VERIFIED_AT, Sort.Direction.DESC)
                );

                BDDAssertions.then(actual.missionVerifications())
                        .hasSize(2)
                        .extracting("nickname")
                        .containsExactlyInAnyOrder(NICKNAME_HOST, NICKNAME_MEMBER_A);
            }
        }

        @Nested
        @DisplayName("date가 null이 아니면")
        class whenDateIsNotNull {

            @Transactional
            @Test
            void 해당_일자의_미션_인증_내역들을_조회한다() {
                final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                hostMember.updateNickname(NICKNAME_HOST);
                final Member member = memberRepository.save(Member.socialLogin("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE));
                member.updateNickname(NICKNAME_MEMBER_A);
                final Mission mission = missionRepository.save(Mission.create(
                        hostMember.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.saveAll(List.of(
                        new MissionMember(hostMember, mission, 1),
                        new MissionMember(member, mission, 1)
                ));
                missionVerificationRepository.saveAll(List.of(
                        new MissionVerification(hostMember, mission, UPLOADED_IMAGE_URL, 1),
                        new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1)
                ));

                final MissionVerificationsResponse actual = sut.getVerifications(
                        new MissionVerificationQuery(member.getId(), mission.getId(), LocalDate.now(), SortType.VERIFIED_AT, Sort.Direction.DESC)
                );

                BDDAssertions.then(actual.missionVerifications())
                        .hasSize(2)
                        .extracting("nickname")
                        .containsExactlyInAnyOrder(NICKNAME_HOST, NICKNAME_MEMBER_A);
            }
        }

        @Nested
        @DisplayName("정렬 방향이 오래된 순이라면")
        class whenSortDirectionIsAsc {

            @Transactional
            @Test
            void 인증_시간_오름차순으로_인증_내역들을_조회한다() {
                final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                hostMember.updateNickname(NICKNAME_HOST);
                final Member member = memberRepository.save(Member.socialLogin("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE));
                member.updateNickname(NICKNAME_MEMBER_A);
                final Mission mission = missionRepository.save(Mission.create(
                        hostMember.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.saveAll(List.of(
                        new MissionMember(hostMember, mission, 1),
                        new MissionMember(member, mission, 1)
                ));
                missionVerificationRepository.save(new MissionVerification(hostMember, mission, UPLOADED_IMAGE_URL, 1));
                missionVerificationRepository.save(new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1));

                final MissionVerificationsResponse actual = sut.getVerifications(
                        new MissionVerificationQuery(member.getId(), mission.getId(), LocalDate.now(), SortType.VERIFIED_AT, Sort.Direction.ASC)
                );
                BDDAssertions.then(actual.missionVerifications())
                        .hasSize(2)
                        .extracting("nickname")
                        .containsExactlyInAnyOrder(NICKNAME_HOST, NICKNAME_MEMBER_A);
            }
        }

        @Nested
        @DisplayName("정렬 방향이 최신순이라면")
        class whenSortDirectionIsDesc {

            @Transactional
            @Test
            void 인증_시간_내림차순으로_인증_내역들을_조회한다() {
                final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                hostMember.updateNickname(NICKNAME_HOST);
                final Member member = memberRepository.save(Member.socialLogin("socialId2", EMAIL_MEMBER_A, SocialType.GOOGLE));
                member.updateNickname(NICKNAME_MEMBER_A);
                final Mission mission = missionRepository.save(Mission.create(
                        hostMember.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.saveAll(List.of(
                        new MissionMember(hostMember, mission, 1),
                        new MissionMember(member, mission, 1)
                ));
                missionVerificationRepository.save(new MissionVerification(hostMember, mission, UPLOADED_IMAGE_URL, 1));
                missionVerificationRepository.save(new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1));

                final MissionVerificationsResponse actual = sut.getVerifications(
                        new MissionVerificationQuery(member.getId(), mission.getId(), LocalDate.now(), SortType.VERIFIED_AT, Sort.Direction.ASC)
                );
                BDDAssertions.then(actual.missionVerifications())
                        .hasSize(2)
                        .extracting("nickname")
                        .containsExactlyInAnyOrder(NICKNAME_MEMBER_A, NICKNAME_HOST);
            }
        }
    }

    @Nested
    class createVerification {

        @Nested
        @DisplayName("오늘 미션 인증을 하지 않았다면")
        class whenNotVerified {

            @Test
            void 미션_인증한다() {
                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                final Mission mission = missionRepository.save(Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.save(new MissionMember(member, mission, 0));

                given(timeProvider.now()).willReturn(LocalDateTime.now());
                sut.createVerification(
                        new CreateMissionVerificationCommand(member.getId(), mission.getId(), IMAGE_FILE)
                );

                BDDAssertions.then(missionVerificationRepository.findAll()).hasSize(1);
            }
        }

        @Nested
        @DisplayName("오늘 미션 인증을 했다면")
        class whenVerified {

            @Test
            void DUPLICATE_VERIFICATION_예외를_반환한다() {
//                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
//                final Mission mission = missionRepository.save(Mission.create(
//                        member.getId(),
//                        DESCRIPTION,
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(30),
//                        TimeOfDay.EVERYDAY,
//                        WEEK,
//                        BOARD_COUNT,
//                        InvitationCode.generate()
//                ));
//                missionMemberRepository.save(new MissionMember(member, mission, 1));
//                missionVerificationRepository.save(new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1));
//
//                given(objectStorageClient.uploadFile(IMAGE_FILE)).willReturn(UPLOADED_IMAGE_URL);
//
//                thenThrownBy(() -> sut.createVerification(
//                        new CreateMissionVerificationCommand(member.getId(), mission.getId(), IMAGE_FILE)))
//                        .isInstanceOf(BadRequestException.class)
//                        .hasMessage(ErrorCode.DUPLICATE_VERIFICATION.getMessage());
            }
        }
    }

    @Nested
    class viewMissionVerification {

        @Nested
        @DisplayName("미션 인증 내역이 있다면")
        class whenVerificationFound {

            @Test
            void 미션_인증_내역을_확인한다() {
                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                final Mission mission = missionRepository.save(Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.save(new MissionMember(member, mission, 1));
                final MissionVerification verification = missionVerificationRepository.save(new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1));

                sut.viewMissionVerification(new ViewMissionVerificationCommand(verification.getId(), member.getId()));

                BDDAssertions.then(missionVerificationViewRepository.findAll()).hasSize(1);
            }
        }

        @Nested
        @DisplayName("미션 인증 내역이 없다면")
        class whenVerificationNotFound {

            @Test
            void NOT_FOUND_VERIFICATION_예외를_반환한다() {
                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));

                thenThrownBy(() -> sut.viewMissionVerification(new ViewMissionVerificationCommand(1L, member.getId())))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessage(ErrorCode.NOT_FOUND_VERIFICATION.getMessage());
            }
        }
    }

    @Nested
    class sendVerificationPushMessages {

        @Nested
        @DisplayName("미션 인증 푸시 시간이면서")
        class whenPushTime {

            @Nested
            @DisplayName("친구 중 한 명이라도 미션을 인증했다면")
            class whenAnyOneVerified {

                @Test
                void MISSION_VERIFIED_푸시_알림을_전송한다() {
                    final LocalDateTime start = LocalDate.now().atStartOfDay();
                    final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
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
                            new MissionMember(member, mission, 1)
                    ));
                    missionVerificationRepository.save(new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1));

                    given(timeProvider.getHour()).willReturn(15);
                    given(timeProvider.now()).willReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)));
                    sut.sendVerificationPushMessage();

                    final Map<String, String> data = new HashMap<>();
                    data.put("missionId", mission.getId().toString());

                    BDDMockito.then(pushMessageProxy)
                            .should()
                            .sendGroupNotificationWithData(
                                    MISSION_VERIFIED.getTitle(1),
                                    MISSION_VERIFIED.getBody(),
                                    data,
                                    Topic.generate(mission.getId())
                            );
                }
            }

            @Nested
            @DisplayName("아무도 미션 인증을 하지 않았다면")
            class whenNoOneVerified {

                @Test
                void MISSION_NO_ONE_VERIFIED_푸시_알림을_전송한다() {
                    final LocalDateTime start = LocalDate.now().atStartOfDay();
                    final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
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

                    given(timeProvider.getHour()).willReturn(15);
                    given(timeProvider.now()).willReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)));
                    sut.sendVerificationPushMessage();

                    final Map<String, String> data = new HashMap<>();
                    data.put("missionId", mission.getId().toString());

                    BDDMockito.then(pushMessageProxy)
                            .should()
                            .sendGroupNotificationWithData(
                                    MISSION_NO_ONE_VERIFIED.getTitle(),
                                    MISSION_NO_ONE_VERIFIED.getBody(),
                                    data,
                                    Topic.generate(mission.getId())
                            );
                }
            }
        }

        @Nested
        @DisplayName("미션 인증 푸시 시간이 아니라면")
        class whenNotPushTime {

            @Test
            void 푸시_알림을_전송하지_않는다() {
                final LocalDateTime start = LocalDate.now().atStartOfDay();
                final Member member = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
                final Mission mission = missionRepository.save(Mission.create(
                        member.getId(),
                        DESCRIPTION,
                        start,
                        start.plusDays(30),
                        TimeOfDay.EVERYDAY,
                        WEEK,
                        BOARD_COUNT,
                        InvitationCode.generate()
                ));
                missionMemberRepository.save(new MissionMember(member, mission, 0));

                given(timeProvider.getHour()).willReturn(12);
                given(timeProvider.now()).willReturn(LocalDateTime.of(LocalDate.now(), LocalTime.NOON));
                sut.sendVerificationPushMessage();

                BDDMockito.then(pushMessageProxy)
                        .should(never())
                        .sendGroupNotificationWithData(anyString(), anyString(), any(), anyString());
            }
        }
    }

    @Nested
    class sendVerificationWarningPushMessage {

        @Nested
        @DisplayName("미션을 인증하지 않았으면서")
        class whenNotVerified {

            @Nested
            @DisplayName("인증 마감 경고 시간이면")
            class whenPushTime {

                @Test
                void MISSION_VERIFICATION_WARNING_푸시_알림을_전송한다() {
                    final LocalDateTime start = LocalDate.now().atStartOfDay();
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
                    missionMemberRepository.save(new MissionMember(hostMember, mission, 0));
                    missionMemberRepository.save(new MissionMember(member, mission, 0));

                    given(timeProvider.now()).willReturn(start.minusMinutes(30));
                    sut.sendVerificationWarningPushMessage();

                    final Map<String, String> data = new HashMap<>();
                    data.put("missionId", mission.getId().toString());

                    BDDMockito.then(pushMessageProxy)
                            .should()
                            .sendIndividualNotificationWithData(
                                    MISSION_VERIFICATION_WARNING.getTitle(),
                                    MISSION_VERIFICATION_WARNING.getBody(),
                                    data,
                                    DEVICE_TOKEN
                            );
                }
            }

            @Nested
            @DisplayName("인증 마감 경고 시간이 아니라면")
            class whenNotPushTime {

                @Test
                void 푸시_알림을_전송하지_않는다() {
                    final LocalDateTime start = LocalDate.now().atStartOfDay();
                    final Member hostMember = memberRepository.save(Member.socialLogin("socialId1", EMAIL_HOST, SocialType.GOOGLE));
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

                    given(timeProvider.now()).willReturn(start.minusHours(3));
                    sut.sendVerificationWarningPushMessage();

                    BDDMockito.then(pushMessageProxy)
                            .should(never())
                            .sendGroupNotificationWithData(anyString(), anyString(), any(), anyString());
                }
            }
        }
    }
}