//package com.nexters.goalpanzi.acceptance;
//
//import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
//import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
//import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
//import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
//import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
//import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
//import com.nexters.goalpanzi.domain.mission.DayOfWeek;
//import com.nexters.goalpanzi.domain.mission.TimeOfDay;
//import com.nexters.goalpanzi.exception.ErrorCode;
//import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
//import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
//import io.restassured.response.ExtractableResponse;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static com.nexters.goalpanzi.acceptance.AcceptanceStep.*;
//import static com.nexters.goalpanzi.fixture.MemberFixture.*;
//import static com.nexters.goalpanzi.fixture.MissionFixture.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//public class MissionVerificationAcceptanceTest extends AcceptanceTest {
//
//    @MockBean
//    private ObjectStorageClient objectStorageClient;
//
//    @Test
//    void 미션_인증에_성공한다() {
//        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
//
//        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
//        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
//        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
//
//        ExtractableResponse<Response> response = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
//
//        assertThat(response.statusCode()).isEqualTo(200);
//    }
//
//    @Test
//    void 미션_기간이_아니므로_인증에_실패한다() {
//        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
//
//        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
//        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), TimeOfDay.EVERYDAY, WEEK, 1);
//        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
//
//        ExtractableResponse<Response> response = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
//
//        assertAll(
//                () -> assertThat(response.statusCode()).isEqualTo(400),
//                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.NOT_VERIFICATION_PERIOD.getMessage())
//        );
//    }
//
//    @Test
//    void 지정한_인증_일자가_아니므로_인증에_실패한다() {
//        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
//
//        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
//        LocalDate today = LocalDate.now();
//        List<DayOfWeek> missionDays = WEEK.stream().filter(d -> d != DayOfWeek.valueOf(today.getDayOfWeek().name())).toList();
//        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, missionDays, 1);
//        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
//
//        ExtractableResponse<Response> response = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
//
//        assertAll(
//                () -> assertThat(response.statusCode()).isEqualTo(400),
//                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.NOT_VERIFICATION_DAY.getMessage())
//        );
//    }
//
//    @Test
//    void 이미_완료된_미션이므로_인증에_실패한다() {
//        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
//
//        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
//        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
//        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
//
//        ExtractableResponse<Response> firstResponse = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
//        ExtractableResponse<Response> secondResponse = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
//
//        assertAll(
//                () -> assertThat(firstResponse.statusCode()).isEqualTo(200),
//                () -> assertThat(secondResponse.statusCode()).isEqualTo(400),
//                () -> assertThat(secondResponse.jsonPath().getString("message")).isEqualTo(ErrorCode.ALREADY_COMPLETED_MISSION.getMessage())
//        );
//    }
//
//    @Test
//    void 이미_인증한_미션이므로_인증에_실패한다() {
//        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
//
//        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
//        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 2);
//        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
//
//        ExtractableResponse<Response> firstResponse = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
//        ExtractableResponse<Response> secondResponse = 미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
//
//        assertAll(
//                () -> assertThat(firstResponse.statusCode()).isEqualTo(200),
//                () -> assertThat(secondResponse.statusCode()).isEqualTo(400),
//                () -> assertThat(secondResponse.jsonPath().getString("message")).isEqualTo(ErrorCode.DUPLICATE_VERIFICATION.getMessage())
//        );
//    }
//
//    @Test
//    void 특정_일자의_미션_인증_현황을_조회한다() {
//        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
//
//        LoginResponse hostLogin = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
//        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
//        프로필_설정(new UpdateProfileRequest(NICKNAME_HOST, CHARACTER_HOST), hostLogin.accessToken());
//        MissionDetailResponse mission = 미션_생성(missionRequest, hostLogin.accessToken()).as(MissionDetailResponse.class);
//        미션_인증(IMAGE_FILE, mission.missionId(), hostLogin.accessToken());
//
//        LoginResponse memberALogin = 구글_로그인(new GoogleLoginCommand(EMAIL_MEMBER_A)).as(LoginResponse.class);
//        프로필_설정(new UpdateProfileRequest(NICKNAME_MEMBER_A, CHARACTER_MEMBER_A), memberALogin.accessToken());
//        미션_참여(mission.invitationCode(), memberALogin.accessToken());
//        미션_인증(IMAGE_FILE, mission.missionId(), memberALogin.accessToken());
//
//        LoginResponse memberBLogin = 구글_로그인(new GoogleLoginCommand(EMAIL_MEMBER_B)).as(LoginResponse.class);
//        프로필_설정(new UpdateProfileRequest(NICKNAME_MEMBER_B, CHARACTER_MEMBER_B), memberBLogin.accessToken());
//        미션_참여(mission.invitationCode(), memberBLogin.accessToken());
//        미션_인증(IMAGE_FILE, mission.missionId(), memberBLogin.accessToken());
//
//        MissionVerificationsResponse verifications = 일자별_미션_인증_조회(mission.missionId(), LocalDate.now(), hostLogin.accessToken()).as(MissionVerificationsResponse.class);
//
//        List<String> nicknames = verifications.missionVerifications().stream()
//                .map(MissionVerificationResponse::nickname)
//                .toList();
//
//        assertAll(
//                () -> assertThat(verifications.missionVerifications().size()).isEqualTo(3),
//                () -> assertThat(nicknames).containsExactly(NICKNAME_HOST, NICKNAME_MEMBER_A, NICKNAME_MEMBER_B)
//        );
//    }
//
//    @Test
//    void 보드칸_번호에_해당하는_나의_미션_인증_내역을_조회한다() {
//        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
//
//        LoginResponse login = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
//        프로필_설정(new UpdateProfileRequest(NICKNAME_HOST, CHARACTER_HOST), login.accessToken());
//
//        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
//        MissionDetailResponse mission = 미션_생성(missionRequest, login.accessToken()).as(MissionDetailResponse.class);
//        미션_인증(IMAGE_FILE, mission.missionId(), login.accessToken());
//
//        MissionVerificationResponse verification = 내_미션_인증_조회(1, mission.missionId(), login.accessToken()).as(MissionVerificationResponse.class);
//
//        assertAll(
//                () -> assertThat(verification.nickname()).isEqualTo(NICKNAME_HOST),
//                () -> assertThat(verification.characterType()).isEqualTo(CHARACTER_HOST),
//                () -> assertThat(verification.imageUrl()).isEqualTo(UPLOADED_IMAGE_URL)
//        );
//    }
//
//    //    void 내가_참여하지_않은_미션의_인증_현황을_조회할_수_없다() {
////        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);
////
////        LoginResponse memberALogin = 구글_로그인(new GoogleLoginCommand(EMAIL_MEMBER_A)).as(LoginResponse.class);
////        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
////        프로필_설정(new UpdateProfileRequest(NICKNAME_MEMBER_A, CHARACTER_MEMBER_A), memberALogin.accessToken());
////        MissionDetailResponse mission = 미션_생성(missionRequest, memberALogin.accessToken()).as(MissionDetailResponse.class);
////        미션_인증(IMAGE_FILE, mission.missionId(), memberALogin.accessToken());
////
////        LoginResponse memberBLogin = 구글_로그인(new GoogleLoginCommand(EMAIL_MEMBER_B)).as(LoginResponse.class);
////
////        ExtractableResponse<Response> response = 일자별_미션_인증_조회(mission.missionId(), LocalDate.now(), memberBLogin.accessToken());
////
////        assertAll(
////                () -> assertThat(response.statusCode()).isEqualTo(403),
////                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.NOT_JOINED_MISSION_MEMBER.getMessage())
////        );
////    }
//}
