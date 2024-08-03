package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.*;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL;
import static com.nexters.goalpanzi.fixture.MissionFixture.DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MissionAcceptanceTest extends AcceptanceTest {

    @Test
    void 미션을_생성한다() {
        LoginResponse loginResponse = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);

        CreateMissionRequest request = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(5), TimeOfDay.EVERYDAY, List.of(DayOfWeek.FRIDAY), 5);

        MissionDetailResponse actual = 미션_생성(request, loginResponse.accessToken()).as(MissionDetailResponse.class);

        assertAll(
                () -> assertThat(actual.description()).isEqualTo(DESCRIPTION),
                () -> assertThat(actual.boardCount()).isEqualTo(5),
                () -> assertThat(actual.hostMemberId()).isEqualTo(1L));
    }

    @Test
    void 미션을_조회한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);

        CreateMissionRequest request = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(),
                LocalDateTime.now().plusDays(5), TimeOfDay.EVERYDAY,
                List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), 20);

        MissionDetailResponse mission = 미션_생성(request, login.accessToken()).as(MissionDetailResponse.class);

        MissionDetailResponse actual = 미션_조회(mission.missionId(), login.accessToken()).as(MissionDetailResponse.class);

        assertAll(
                () -> assertThat(actual.description()).isEqualTo(DESCRIPTION),
                () -> assertThat(actual.boardCount()).isEqualTo(20),
                () -> assertThat(actual.missionDays()).containsExactly(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                () -> assertThat(actual.hostMemberId()).isEqualTo(1L));
    }
}
