package com.nexters.goalpanzi.domain.mission;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.MemberFixture.MEMBER_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.BOARD_COUNT;
import static com.nexters.goalpanzi.fixture.MissionFixture.DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
                BOARD_COUNT
        );

        assertAll(
                () -> assertThat(mission.getInvitationCode().getCode()).hasSize(4),
                () -> assertThat(mission.getUploadStartTime()).isEqualTo("00:00"),
                () -> assertThat(mission.getUploadEndTime()).isEqualTo("24:00"))
        ;
    }
}