package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.exception.ErrorResponse;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import com.nexters.goalpanzi.presentation.mission.dto.JoinMissionRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.구글_로그인;
import static com.nexters.goalpanzi.acceptance.AcceptanceStep.미션_생성;
import static com.nexters.goalpanzi.exception.ErrorCode.CAN_NOT_JOIN_MISSION;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_MEMBER_A;
import static com.nexters.goalpanzi.fixture.MissionFixture.DESCRIPTION;
import static com.nexters.goalpanzi.fixture.TokenFixture.BEARER;
import static org.assertj.core.api.Assertions.assertThat;

public class MissionMemberAcceptanceTest extends AcceptanceTest {

    @Autowired
    private MissionMemberRepository missionMemberRepository;

    @Test
    void 초대코드로_미션에_참여한다() {
        LoginResponse loginHost = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
        MissionDetailResponse mission = 미션_생성(loginHost.accessToken()).as(MissionDetailResponse.class);
        LoginResponse loginMember = 구글_로그인(new GoogleLoginCommand(EMAIL_MEMBER_A)).as(LoginResponse.class);

        JoinMissionRequest joinRequest = new JoinMissionRequest(mission.invitationCode());
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + loginMember.accessToken())
                .body(joinRequest)
                .when().post("/api/mission-members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        assertThat(
                missionMemberRepository.findByMemberIdAndMissionId(loginMember.memberId(), mission.missionId())
        ).isPresent();
    }

    @Test
    void 참여하고있는_미션을_조회한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
        미션_생성(login.accessToken()).as(MissionDetailResponse.class);

        MissionsResponse actual = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .when().get("/api/mission-members/me")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(MissionsResponse.class);

        assertThat(actual.missions()).hasSize(1);
    }

    @Test
    void 미션기간이_아닌경우_참여가_불가능하다() {
        LoginResponse loginHost = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
        CreateMissionRequest request = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(5), TimeOfDay.EVERYDAY, List.of(DayOfWeek.FRIDAY), 5);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + loginHost.accessToken())
                .body(request)
                .when().post("/api/missions")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .onFailMessage(CAN_NOT_JOIN_MISSION.toString())
                .extract();
    }
}
