package com.nexters.goalpanzi.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.*;
import static com.nexters.goalpanzi.fixture.MemberFixture.*;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MissionBoardAcceptanceTest extends AcceptanceTest {

    @MockBean
    private ObjectStorageClient objectStorageClient;

    @Test
    void 보드판_정보를_조회한다() throws JsonProcessingException {
        when(objectStorageClient.uploadFile(any(MultipartFile.class))).thenReturn(UPLOADED_IMAGE_URL);

        LoginResponse hostLogin = 구글_로그인(new GoogleLoginCommand(EMAIL_HOST)).as(LoginResponse.class);
        CreateMissionRequest missionRequest = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(), LocalDateTime.now().plusDays(1), TimeOfDay.EVERYDAY, WEEK, 1);
        프로필_설정(new UpdateProfileRequest(NICKNAME_HOST, CHARACTER_HOST), hostLogin.accessToken());
        MissionDetailResponse mission = 미션_생성(missionRequest, hostLogin.accessToken()).as(MissionDetailResponse.class);
        미션_인증(IMAGE_FILE, mission.missionId(), hostLogin.accessToken());

        LoginResponse memberALogin = 구글_로그인(new GoogleLoginCommand(EMAIL_MEMBER_A)).as(LoginResponse.class);
        프로필_설정(new UpdateProfileRequest(NICKNAME_MEMBER_A, CHARACTER_MEMBER_A), memberALogin.accessToken());
        미션_참여(mission.invitationCode(), memberALogin.accessToken());
        미션_인증(IMAGE_FILE, mission.missionId(), memberALogin.accessToken());

        LoginResponse memberBLogin = 구글_로그인(new GoogleLoginCommand(EMAIL_MEMBER_B)).as(LoginResponse.class);
        프로필_설정(new UpdateProfileRequest(NICKNAME_MEMBER_B, CHARACTER_MEMBER_B), memberBLogin.accessToken());
        미션_참여(mission.invitationCode(), memberBLogin.accessToken());

        List<MissionBoardResponse> board = 보드판_조회(mission.missionId(), hostLogin.accessToken());

        assertAll(
                () -> assertThat(board.size()).isEqualTo(mission.boardCount() + 1),
                () -> assertThat(board.get(0).eventItem()).isNull(),
                () -> assertThat(board.get(0).number()).isEqualTo(0),
                () -> assertThat(board.get(0).missionMembers().size()).isEqualTo(1),
                () -> assertThat(board.get(1).eventItem()).isNotNull(),
                () -> assertThat(board.get(1).number()).isEqualTo(1),
                () -> assertThat(board.get(1).missionMembers().size()).isEqualTo(2),
                () -> assertThat(board.get(1).missionMembers().get(0).nickname()).isEqualTo(NICKNAME_HOST),
                () -> assertThat(board.get(1).missionMembers().get(1).nickname()).isEqualTo(NICKNAME_MEMBER_A)
        );
    }
}
