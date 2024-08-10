package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.domain.mission.BoardOrderBy;
import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.domain.mission.VerificationOrderBy;
import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import com.nexters.goalpanzi.presentation.mission.dto.JoinMissionRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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

    public static ExtractableResponse<Response> 프로필_설정(UpdateProfileRequest request, String accessToken) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .body(request)
                .when().patch("/api/member/profile")
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

    public static ExtractableResponse<Response> 미션_인증(MultipartFile imageFile, Long missionId, String accessToken) {
        try {
            return RestAssured.given().log().all()
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .multiPart("imageFile", imageFile.getOriginalFilename(), imageFile.getInputStream(), imageFile.getContentType())
                    .when().post("/api/missions/" + missionId + "/verifications/me")
                    .then().log().all()
                    .extract();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExtractableResponse<Response> 일자별_미션_인증_조회(Long missionId, LocalDate date, String accessToken) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .queryParam("date", date.toString())
                .queryParam("orderBy", VerificationOrderBy.CREATED_AT_DESC)
                .when().get("/api/missions/" + missionId + "/verifications")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 내_미션_인증_조회(Integer number, Long missionId, String accessToken) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .when().get("/api/missions/" + missionId + "/verifications/me/" + number)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 보드판_조회(Long missionId, String accessToken) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .queryParam("orderBy", BoardOrderBy.CREATED_AT)
                .when().get("/api/missions/" + missionId + "/board")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
