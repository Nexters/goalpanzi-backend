package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.member.dto.response.ProfileResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.presentation.auth.dto.GoogleLoginRequest;
import com.nexters.goalpanzi.presentation.member.dto.UpdateDeviceTokenRequest;
import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
import com.nexters.goalpanzi.presentation.member.dto.UpdatePushActivationStatusRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.*;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEPRECATED_DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static com.nexters.goalpanzi.fixture.TokenFixture.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MemberAcceptanceTest extends AcceptanceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 프로필을_설정한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginRequest(EMAIL_HOST)).as(LoginResponse.class);
        프로필_설정(new UpdateProfileRequest(NICKNAME_HOST, CHARACTER_HOST), login.accessToken());

        Member actual = memberRepository.getMember(login.memberId());
        assertAll(
                () -> assertThat(actual.getCharacterType()).isEqualTo(CHARACTER_HOST),
                () -> assertThat(actual.getNickname()).isEqualTo(NICKNAME_HOST)
        );
    }

    @Test
    void 프로필을_조회한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginRequest(EMAIL_HOST)).as(LoginResponse.class);
        프로필_설정(new UpdateProfileRequest(NICKNAME_HOST, CHARACTER_HOST), login.accessToken());

        ProfileResponse actual = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .when().get("/api/member/profile")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(ProfileResponse.class);

        assertAll(
                () -> assertThat(actual.characterType()).isEqualTo(CHARACTER_HOST),
                () -> assertThat(actual.nickname()).isEqualTo(NICKNAME_HOST)
        );
    }

    @Test
    void 회원이_탈퇴한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginRequest(EMAIL_HOST)).as(LoginResponse.class);
        MissionDetailResponse mission = 미션_생성(login.accessToken()).as(MissionDetailResponse.class);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .when().delete("/api/member")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(memberRepository.findByIdAndDeletedAtIsNull(login.memberId())).isEmpty();
    }

    @Test
    void 디바이스_토큰을_갱신한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginRequest(EMAIL_HOST)).as(LoginResponse.class);

        UpdateDeviceTokenRequest request = new UpdateDeviceTokenRequest(DEPRECATED_DEVICE_TOKEN, DEVICE_TOKEN);
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .body(request)
                .when().patch("/api/member/device-token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member member = memberRepository.getMember(login.memberId());
        assertThat(member.getDeviceToken()).isEqualTo(DEVICE_TOKEN);
    }

    @Test
    void 푸시_알림_활성화_여부를_수정한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginRequest(EMAIL_HOST)).as(LoginResponse.class);
        디바이스_토큰_갱신(login.accessToken());

        UpdatePushActivationStatusRequest request = new UpdatePushActivationStatusRequest(true);
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .body(request)
                .when().patch("/api/member/push-activation-status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Member member = memberRepository.getMember(login.memberId());
        assertThat(member.isPushActivated()).isTrue();
    }
}
