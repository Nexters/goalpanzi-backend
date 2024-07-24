//package com.nexters.goalpanzi.acceptance;
//
//import com.nexters.goalpanzi.acceptance.fixture.TokenFixture;
//import com.nexters.goalpanzi.application.auth.SocialUserInfo;
//import com.nexters.goalpanzi.application.auth.apple.AppleUserProvider;
//import com.nexters.goalpanzi.application.auth.dto.AppleLoginRequest;
//import com.nexters.goalpanzi.application.auth.dto.LoginResponse;
//import io.restassured.RestAssured;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//
//import java.security.NoSuchAlgorithmException;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//
//public class LoginAcceptanceTest extends AcceptanceTest {
//
//    @Test
//    void 사용자가_애플_로그인을_정상적으로_한다() throws NoSuchAlgorithmException {
//        String appleToken = TokenFixture.generateAppleToken();
//        AppleLoginRequest request = new AppleLoginRequest(appleToken);
//        when(socialUserProvider.getSocialUserInfo(any()))
//                .thenReturn(new SocialUserInfo("123", "email"));
//
//        LoginResponse actual = RestAssured.given().log().all()
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(request)
//                .when().post("/api/auth/login/apple")
//                .then().log().all()
//                .statusCode(HttpStatus.OK.value())
//                .extract()
//                .as(LoginResponse.class);
//    }
//}
