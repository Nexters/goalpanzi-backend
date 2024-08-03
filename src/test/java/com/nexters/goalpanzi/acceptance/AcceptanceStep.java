package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import com.nexters.goalpanzi.presentation.mission.dto.JoinMissionRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.MissionFixture.DESCRIPTION;
import static com.nexters.goalpanzi.fixture.TokenFixture.BEARER;

public class AcceptanceStep {

    public static ExtractableResponse<Response> 구글_로그인(GoogleLoginCommand request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/auth/login/google")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 미션_생성(CreateMissionRequest request, String accessToken) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .body(request)
                .when().post("/api/missions")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 미션_생성(String accessToken) {
        CreateMissionRequest request = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(5), TimeOfDay.EVERYDAY, List.of(DayOfWeek.FRIDAY), 5);
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .body(request)
                .when().post("/api/missions")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 미션_참여(String invitationCode, String accessToken) {
        JoinMissionRequest joinRequest = new JoinMissionRequest(invitationCode);
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .body(joinRequest)
                .when().post("/api/mission-members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 미션_조회(Long missionId, String accessToken) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .when().get("/api/missions/" + missionId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
