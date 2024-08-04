package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.presentation.mission.dto.JoinMissionRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.*;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL;
import static com.nexters.goalpanzi.fixture.TokenFixture.BEARER;
import static org.assertj.core.api.Assertions.assertThat;

public class MissionMemberAcceptanceTest extends AcceptanceTest {

    @Test
    void 초대코드로_미션에_참여한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);
        MissionDetailResponse mission = 미션_생성(login.accessToken()).as(MissionDetailResponse.class);

        JoinMissionRequest joinRequest = new JoinMissionRequest(mission.invitationCode());
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .body(joinRequest)
                .when().post("/api/mission-members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void 참여하고_있는_미션을_조회한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);
        MissionDetailResponse mission = 미션_생성(login.accessToken()).as(MissionDetailResponse.class);
        미션_참여(mission.invitationCode(), login.accessToken());

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
}
