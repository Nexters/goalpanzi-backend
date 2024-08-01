package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.LoginResponse;
import com.nexters.goalpanzi.application.mission.dto.MissionDetailResponse;
import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.구글_로그인;
import static com.nexters.goalpanzi.acceptance.AcceptanceStep.미션_생성;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL;
import static com.nexters.goalpanzi.fixture.MemberFixture.ID_TOKEN;
import static com.nexters.goalpanzi.fixture.MissionFixture.DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MissionAcceptanceTest extends AcceptanceTest {

    @Test
    void 미션을_생성한다() {
        LoginResponse loginResponse = 구글_로그인(new GoogleLoginCommand(ID_TOKEN, EMAIL)).as(LoginResponse.class);

        CreateMissionRequest request = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(5), TimeOfDay.EVERYDAY, List.of(DayOfWeek.FRIDAY), 5);

        MissionDetailResponse actual = 미션_생성(request, loginResponse.accessToken()).as(MissionDetailResponse.class);

        assertAll(
                () -> assertThat(actual.description()).isEqualTo(DESCRIPTION),
                () -> assertThat(actual.boardCount()).isEqualTo(5),
                () -> assertThat(actual.hostMemberId()).isEqualTo(1L));
    }
}
