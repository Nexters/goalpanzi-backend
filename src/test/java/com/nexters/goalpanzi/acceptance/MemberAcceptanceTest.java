package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.member.dto.response.ProfileResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.*;
import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static com.nexters.goalpanzi.fixture.TokenFixture.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MemberAcceptanceTest extends AcceptanceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 프로필을_설정한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
        프로필_설정(new UpdateProfileRequest(NICKNAME_HOST, CHARACTER_HOST), login.accessToken());

        Member actual = memberRepository.getMember(login.memberId());
        assertAll(
                () -> assertThat(actual.getCharacterType()).isEqualTo(CHARACTER_HOST),
                () -> assertThat(actual.getNickname()).isEqualTo(NICKNAME_HOST)
        );
    }

    @Test
    void 프로필을_조회한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
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
        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
        MissionDetailResponse mission = 미션_생성(login.accessToken()).as(MissionDetailResponse.class);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .when().delete("/api/member")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(memberRepository.findAll()).isEmpty();
    }
}
