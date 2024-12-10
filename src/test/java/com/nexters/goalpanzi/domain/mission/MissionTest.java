package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.common.time.TimeUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.MemberFixture.MEMBER_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MissionTest {

    @Test
    void 미션을_생성한다() {
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                TimeOfDay.EVERYDAY,
                List.of(DayOfWeek.FRIDAY),
                BOARD_COUNT,
                InvitationCode.generate()
        );

        assertAll(
                () -> assertThat(mission.getInvitationCode().getCode()).hasSize(4),
                () -> assertThat(mission.getUploadStartTime()).isEqualTo("00:00"),
                () -> assertThat(mission.getUploadEndTime()).isEqualTo("24:00"))
        ;
    }

    @Test
    void 미션_보드칸수는_최소_1개_이다() {
        assertThrows(IllegalArgumentException.class, () -> Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                TimeOfDay.EVERYDAY,
                List.of(DayOfWeek.FRIDAY),
                0,
                InvitationCode.generate()
        ));
    }

    @Test
    void 미션_시작일보다_종료일이_더_커야한다() {
        assertThrows(IllegalArgumentException.class, () -> Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(7),
                TimeOfDay.EVERYDAY,
                List.of(DayOfWeek.FRIDAY),
                BOARD_COUNT,
                InvitationCode.generate()
        ));
    }

    @Test
    void 미션_종료일이_지나면_만료된_미션이다() {
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(7),
                TimeOfDay.EVERYDAY,
                List.of(DayOfWeek.FRIDAY),
                BOARD_COUNT,
                InvitationCode.generate()
        );
        assertThat(mission.isExpired()).isTrue();
    }

    @Test
    void 오늘이_미션_인증_요일이면_true를_반환한다() {
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        );
        assertThat(mission.isMissionDay()).isTrue();
    }

    @Test
    void 오늘이_미션_인증_요일이_아니면_false를_반환한다() {
        List<DayOfWeek> missionDays = WEEK.stream()
                .filter(d -> d != DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name())).toList();

        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                missionDays,
                BOARD_COUNT,
                InvitationCode.generate()
        );
        assertThat(mission.isMissionDay()).isFalse();
    }

    @Test
    void 현재_시간이_미션_인증_시간이면_true를_반환한다() {
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        );
        assertThat(mission.isMissionTime()).isTrue();
    }

    @Test
    void 현재_시간이_미션_인증_시간이_아니면_false를_반환한다() {
        TimeOfDay timeOfDay = (LocalDateTime.now().getHour() < 12) ? TimeOfDay.AFTERNOON : TimeOfDay.MORNING;

        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                timeOfDay,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        );
        assertThat(mission.isMissionTime()).isFalse();
    }

    @Test
    void 인증_시간이_오전인_미션은_푸시_알림_시간이_09시이다() {
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.MORNING,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        );
        assertThat(mission.isVerificationStatusPushTime(9)).isTrue();
    }

    @Test
    void 인증_시간이_오후인_미션은_푸시_알림_시간이_15시이다() {
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.AFTERNOON,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        );
        assertThat(mission.isVerificationStatusPushTime(15)).isTrue();
    }

    @Test
    void 인증_시간이_종일인_미션은_푸시_알림_시간이_15시이다() {
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        );
        assertThat(mission.isVerificationStatusPushTime(15)).isTrue();
    }

    @Test
    void 미션_시작까지_1시간_덜_남은_경우_미션_준비_시간이다() {
        LocalDateTime now = LocalDateTime.now();
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                now,
                now.plusDays(30),
                TimeOfDay.EVERYDAY,
                WEEK,
                BOARD_COUNT,
                InvitationCode.generate()
        );

        assertAll(
                () -> assertThat(mission.isReadyTime(mission.getMissionUploadStartDateTime().minusHours(1))).isTrue(),
                () -> assertThat(mission.isReadyTime(mission.getMissionUploadStartDateTime().minusMinutes(30))).isTrue()
        );
    }

    @Test
    void 미션_인증_마감까지_1시간_덜_남은_경우_미션_인증_경고_시간이다() {
        LocalDateTime now = LocalDateTime.now();
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                now,
                now.plusDays(30),
                TimeOfDay.EVERYDAY,
                List.of(DayOfWeek.FRIDAY),
                BOARD_COUNT,
                InvitationCode.generate()
        );
        LocalTime uploadEndTime = TimeUtil.of(mission.getUploadEndTime());

        assertAll(
                () -> assertThat(mission.isVerificationWarningPushTime(uploadEndTime.minusHours(1))).isTrue(),
                () -> assertThat(mission.isVerificationWarningPushTime(uploadEndTime.minusMinutes(30))).isTrue()
        );
    }

    @Test
    void 오늘_일자가_미션_마지막_날인지_검증한다() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(30);
        Mission mission = Mission.create(
                MEMBER_ID,
                DESCRIPTION,
                startDate,
                endDate,
                TimeOfDay.EVERYDAY,
                List.of(DayOfWeek.FRIDAY),
                BOARD_COUNT,
                InvitationCode.generate()
        );

        assertAll(
                () -> assertThat(mission.isEndDate(endDate.toLocalDate())).isTrue(),
                () -> assertThat(mission.isEndDate(endDate.minusDays(1).toLocalDate())).isFalse()
        );
    }
}