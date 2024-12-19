package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.OsType;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.presentation.auth.dto.GoogleLoginRequest;
import com.nexters.goalpanzi.presentation.device.dto.UpdateDeviceTokenRequest;
import com.nexters.goalpanzi.presentation.device.dto.UpdatePushActivationStatusRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.구글_로그인;
import static com.nexters.goalpanzi.acceptance.AcceptanceStep.디바이스_토큰_갱신;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_IDENTIFIER;
import static com.nexters.goalpanzi.fixture.DeviceFixture.DEVICE_TOKEN;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL_HOST;
import static com.nexters.goalpanzi.fixture.TokenFixture.BEARER;
import static org.assertj.core.api.Assertions.assertThat;

public class DeviceAcceptanceTest extends AcceptanceTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void 디바이스_토큰을_갱신한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginRequest(EMAIL_HOST, DEVICE_IDENTIFIER)).as(LoginResponse.class);

        UpdateDeviceTokenRequest request = new UpdateDeviceTokenRequest(DEVICE_IDENTIFIER, DEVICE_TOKEN, OsType.AOS);
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .body(request)
                .when().patch("/api/device/device-token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Device device = deviceRepository.getDevice(login.memberId(), request.deviceIdentifier());
        assertThat(device.getDeviceToken()).isEqualTo(DEVICE_TOKEN);
    }

    @Test
    void 푸시_알림_활성화_여부를_수정한다() {
        LoginResponse login = 구글_로그인(new GoogleLoginRequest(EMAIL_HOST, DEVICE_IDENTIFIER)).as(LoginResponse.class);
        디바이스_토큰_갱신(DEVICE_IDENTIFIER, login.accessToken());

        UpdatePushActivationStatusRequest request = new UpdatePushActivationStatusRequest(DEVICE_IDENTIFIER, true);
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + login.accessToken())
                .body(request)
                .when().patch("/api/device/push-activation-status")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        Device device = deviceRepository.getDevice(login.memberId(), request.deviceIdentifier());
        assertThat(device.getPushActivationStatus()).isTrue();
    }
}
