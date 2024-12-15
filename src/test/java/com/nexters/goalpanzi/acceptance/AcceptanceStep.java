package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.presentation.auth.dto.GoogleLoginRequest;
import com.nexters.goalpanzi.presentation.member.dto.UpdateDeviceTokenRequest;
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

import static com.nexters.goalpanzi.fixture.DeviceFixture.DEPRECATED_DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MissionFixture.DESCRIPTION;
import static com.nexters.goalpanzi.fixture.TokenFixture.BEARER;

public class AcceptanceStep {

    public static ExtractableResponse<Response> 구글_로그인(GoogleLoginRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/auth/login/google")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 회원_탈퇴(Long memberId, String accessToken) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .when().delete("/api/member")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
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
        CreateMissionRequest request = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), TimeOfDay.EVERYDAY, List.of(DayOfWeek.FRIDAY), 5);
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
                .when().get("/api/missions/" + missionId + "/verifications")
                .then().log().all()
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
                .when().get("/api/missions/" + missionId + "/board")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 디바이스_토큰_갱신(String accessToken) {
        UpdateDeviceTokenRequest request = new UpdateDeviceTokenRequest(DEPRECATED_DEVICE_TOKEN, DEVICE_TOKEN);
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .body(request)
                .when().patch("/api/member/device-token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
