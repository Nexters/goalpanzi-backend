package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.common.time.TimeProvider;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.SocialType;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.*;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_MEMBER_A;
import static com.nexters.goalpanzi.fixture.MemberFixture.SOCIAL_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class MissionVerificationValidatorTest extends IntegrationTest {

    @Autowired
    private MissionVerificationValidator sut;

    @Autowired
    private MissionVerificationRepository missionVerificationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionMemberRepository missionMemberRepository;

    @MockBean
    private TimeProvider timeProvider;

    @AfterEach
    void tearDown() {
        missionVerificationRepository.deleteAllInBatch();
        missionMemberRepository.deleteAllInBatch();
        missionRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Nested
    class validate {

        @Nested
        @DisplayName("미션 마지막 칸까지 인증을 모두 마치면")
        class whenMissionCompleted {

            @Test
            void ALREADY_COMPLETED_MISSION_예외를_반환한다() {
                final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.GOOGLE));
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
                final MissionMember missionMember = missionMemberRepository.save(new MissionMember(member, mission, BOARD_COUNT));

                thenThrownBy(() -> sut.validate(missionMember))
                        .isInstanceOf(BadRequestException.class)
                        .hasMessage(ErrorCode.ALREADY_COMPLETED_MISSION.getMessage());
            }
        }

        @Nested
        @DisplayName("미션을 진행하는 상황에서")
        class whenMissionInProgress {

            @Nested
            @DisplayName("중복 인증을 시도하면")
            class whenTryDuplicateVerification {

                @Test
                void DUPLICATE_VERIFICATION_예외를_반환한다() {
                    final LocalDateTime now = LocalDateTime.now();
                    final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.GOOGLE));
                    final Mission mission = missionRepository.save(Mission.create(
                            member.getId(),
                            DESCRIPTION,
                            now,
                            now.plusDays(30),
                            TimeOfDay.EVERYDAY,
                            WEEK,
                            BOARD_COUNT,
                            InvitationCode.generate()
                    ));
                    final MissionMember missionMember = missionMemberRepository.save(new MissionMember(member, mission, 1));
                    missionVerificationRepository.save(new MissionVerification(member, mission, UPLOADED_IMAGE_URL, 1));

                    given(timeProvider.now()).willReturn(now);
                    thenThrownBy(() -> sut.validate(missionMember))
                            .isInstanceOf(BadRequestException.class)
                            .hasMessage(ErrorCode.DUPLICATE_VERIFICATION.getMessage());
                }
            }

            @Nested
            @DisplayName("오늘 처음 인증을 시도하는 경우")
            class whenVerifyForTheFirstTimeToday {

                @Nested
                @DisplayName("미션 기간이 아니면")
                class whenNotMissionPeriod {

                    @Test
                    void NOT_VERIFICATION_PERIOD_예외를_반환한다() {
                        final LocalDateTime now = LocalDateTime.now();
                        final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.GOOGLE));
                        final Mission mission = missionRepository.save(Mission.create(
                                member.getId(),
                                DESCRIPTION,
                                now.minusDays(31),
                                now.minusDays(1),
                                TimeOfDay.EVERYDAY,
                                WEEK,
                                BOARD_COUNT,
                                InvitationCode.generate()
                        ));
                        final MissionMember missionMember = missionMemberRepository.save(new MissionMember(member, mission, 1));

                        given(timeProvider.now()).willReturn(now);
                        thenThrownBy(() -> sut.validate(missionMember))
                                .isInstanceOf(BadRequestException.class)
                                .hasMessage(ErrorCode.NOT_VERIFICATION_PERIOD.getMessage());
                    }
                }

                @Nested
                @DisplayName("미션 기간이면서")
                class whenMissionPeriod {

                    @Nested
                    @DisplayName("미션 인증 요일이 아니면")
                    class whenNotMissionDay {

                        @Test
                        void NOT_VERIFICATION_DAY_예외를_반환한다() {
                            final LocalDateTime now = LocalDateTime.now();
                            final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.GOOGLE));
                            final Mission mission = missionRepository.save(Mission.create(
                                    member.getId(),
                                    DESCRIPTION,
                                    now,
                                    now.plusDays(30),
                                    TimeOfDay.EVERYDAY,
                                    List.of(DayOfWeek.FRIDAY),
                                    BOARD_COUNT,
                                    InvitationCode.generate()
                            ));
                            final MissionMember missionMember = missionMemberRepository.save(new MissionMember(member, mission, 1));

                            given(timeProvider.now()).willReturn(getNextNonFriday(now));
                            thenThrownBy(() -> sut.validate(missionMember))
                                    .isInstanceOf(BadRequestException.class)
                                    .hasMessage(ErrorCode.NOT_VERIFICATION_DAY.getMessage());
                        }

                        private static LocalDateTime getNextNonFriday(final LocalDateTime today) {
                            LocalDateTime nextDay = today;

                            while (DayOfWeek.valueOf(nextDay.getDayOfWeek().name()).equals(DayOfWeek.FRIDAY)) {
                                nextDay = nextDay.plusDays(1);
                            }
                            return nextDay;
                        }
                    }

                    @Nested
                    @DisplayName("미션 인증 요일이고")
                    class whenMissionDay {

                        @Nested
                        @DisplayName("인증 시간이 아니면")
                        class whenNotMissionTime {

                            @Test
                            void NOT_VERIFICATION_TIME_예외를_반환한다() {
                                final LocalDateTime now = LocalDateTime.now();
                                final Member member = memberRepository.save(Member.socialLogin(SOCIAL_ID, EMAIL_MEMBER_A, SocialType.GOOGLE));
                                final Mission mission = missionRepository.save(Mission.create(
                                        member.getId(),
                                        DESCRIPTION,
                                        now,
                                        now.plusDays(30),
                                        TimeOfDay.MORNING,
                                        WEEK,
                                        BOARD_COUNT,
                                        InvitationCode.generate()
                                ));
                                final MissionMember missionMember = missionMemberRepository.save(new MissionMember(member, mission, 1));

                                given(timeProvider.now()).willReturn(LocalDateTime.of(now.toLocalDate(), LocalTime.MAX));
                                thenThrownBy(() -> sut.validate(missionMember))
                                        .isInstanceOf(BadRequestException.class)
                                        .hasMessage(ErrorCode.NOT_VERIFICATION_TIME.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }
}
