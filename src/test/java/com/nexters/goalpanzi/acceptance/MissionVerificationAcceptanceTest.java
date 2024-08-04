package com.nexters.goalpanzi.acceptance;

import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.ncp.ObjectStorageClient;
import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.*;
import static com.nexters.goalpanzi.fixture.MemberFixture.EMAIL;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MissionVerificationAcceptanceTest extends AcceptanceTest {

    @MockBean
    private ObjectStorageClient objectStorageClient;

    @Test
    void 미션_인증에_성공한다() {
        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);

        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);
        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
        미션_참여(mission.invitationCode(), login.accessToken());

        ExtractableResponse<Response> response = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void 지정한_인증_일자가_아니므로_인증에_실패한다() {
        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);

        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);
        LocalDate today = LocalDate.now();
        List<DayOfWeek> missionDays = WEEK.stream().filter(d -> d != DayOfWeek.fromJavaDayOfWeek(today.getDayOfWeek())).toList();
        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, missionDays, 1);
        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
        미션_참여(mission.invitationCode(), login.accessToken());

        ExtractableResponse<Response> response = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(400),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.NOT_VERIFICATION_DAY.getMessage())
        );
    }

    @Test
    void 이미_완료된_미션이므로_인증에_실패한다() {
        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);

        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);
        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
        미션_참여(mission.invitationCode(), login.accessToken());

        ExtractableResponse<Response> firstResponse = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
        ExtractableResponse<Response> secondResponse = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());

        assertAll(
                () -> assertThat(firstResponse.statusCode()).isEqualTo(200),
                () -> assertThat(secondResponse.statusCode()).isEqualTo(400),
                () -> assertThat(secondResponse.jsonPath().getString("message")).isEqualTo(ErrorCode.ALREADY_COMPLETED_MISSION.getMessage())
        );
    }

    @Test
    void 이미_인증한_미션이므로_인증에_실패한다() {
        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);

        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);
        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 2);
        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
        미션_참여(mission.invitationCode(), login.accessToken());

        ExtractableResponse<Response> firstResponse = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
        ExtractableResponse<Response> secondResponse = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());

        assertAll(
                () -> assertThat(firstResponse.statusCode()).isEqualTo(200),
                () -> assertThat(secondResponse.statusCode()).isEqualTo(400),
                () -> assertThat(secondResponse.jsonPath().getString("message")).isEqualTo(ErrorCode.DUPLICATE_VERIFICATION.getMessage())
        );
    }

//    TODO 프로필 생성 후 확인 필요
//    @Test
//    void 특정_일자의_미션_인증_현황을_조회한다() {
//        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
//
//        LoginResponse login1 = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);
//
//        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
//        MissionDetailResponse mission = 미션_생성(missionRequest, login1.accessToken()).as(MissionDetailResponse.class);
//        미션_참여(mission.invitationCode(), login1.accessToken());
//        미션_인증(IMAGE_FILE, mission.missionId(), login1.accessToken());
//
//        LoginResponse login2 = 구글_로그인(new GoogleLoginCommand(EMAIL2)).as(LoginResponse.class);
//        미션_참여(mission.invitationCode(), login2.accessToken());
//        미션_인증(IMAGE_FILE, mission.missionId(), login2.accessToken());
//
//        List<MissionVerificationResponse> verifications = 일자별_미션_인증_조회(mission.missionId(), LocalDate.now(), login1.accessToken()).as(List.class);
//
//        assertAll(
//                () -> assertThat(verifications.size()).isEqualTo(2)
//        );
//    }

    @Test
    void 보드칸_번호에_해당하는_나의_미션_인증_내역을_조회한다() {
        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);

        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL)).as(LoginResponse.class);
        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
        미션_참여(mission.invitationCode(), login.accessToken());
        미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());

        MissionVerificationResponse verification = 내_미션_인증_조회(1, mission.missionId(), login.accessToken()).as(MissionVerificationResponse.class);

        assertAll(
                // TODO 추후 닉네임, 장기말 타입 검증도 추가
                () -> assertThat(verification.imageUrl()).isEqualTo(UPLOADED_IMAGE_URL)
        );
    }
}
