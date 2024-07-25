package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.GoogleLoginRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class AcceptanceStep {

    public static ExtractableResponse<Response> 소셜_로그인(GoogleLoginRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/auth/login/google")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
