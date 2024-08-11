package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.SocialUserInfo;
import com.nexters.goalpanzi.application.auth.SocialUserProvider;
import com.nexters.goalpanzi.application.auth.SocialUserProviderFactory;
import com.nexters.goalpanzi.application.auth.dto.request.AppleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.fixture.TokenFixture;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.security.NoSuchAlgorithmException;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.구글_로그인;
import static com.nexters.goalpanzi.acceptance.AcceptanceStep.회원_탈퇴;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LoginAcceptanceTest extends AcceptanceTest {

    @SpyBean
    protected SocialUserProviderFactory socialUserProviderFactory;

    @MockBean
    protected SocialUserProvider socialUserProvider;

    @Test
    void 사용자가_애플_로그인을_정상적으로_한다() throws NoSuchAlgorithmException {
        String appleToken = TokenFixture.generateAppleToken();
        AppleLoginCommand request = new AppleLoginCommand(appleToken);

        when(socialUserProviderFactory.getProvider(any()))
                .thenReturn(socialUserProvider);
        when(socialUserProvider.getSocialUserInfo(any()))
                .thenReturn(new SocialUserInfo("123", "email"));

        LoginResponse actual = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/auth/login/apple")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LoginResponse.class);

        assertAll(
                () -> assertThat(actual.accessToken()).isNotEmpty(),
                () -> assertThat(actual.refreshToken()).isNotEmpty(),
                () -> assertThat(actual.isProfileSet()).isFalse()
        );
    }

    @Test
    void 사용자가_구글_로그인을_정상적으로_한다() {
        GoogleLoginCommand request = new GoogleLoginCommand(EMAIL_HOST);

        LoginResponse actual = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/auth/login/google")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(LoginResponse.class);

        assertAll(
                () -> assertThat(actual.accessToken()).isNotEmpty(),
                () -> assertThat(actual.refreshToken()).isNotEmpty(),
                () -> assertThat(actual.isProfileSet()).isFalse()
        );
    }

    @Test
    void 사용자가_탈퇴후_재가입한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
        회원_탈퇴(login.memberId(), login.accessToken());

        LoginResponse actual = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);

        assertThat(actual.memberId()).isNotNull();
    }
}
