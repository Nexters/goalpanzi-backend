package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.fixture.MemberFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MissionVerificationValidatorTest {

    Mission mockMission;

    @MockBean
    private MissionVerificationRepository missionVerificationRepository;

    @Autowired
    private MissionVerificationValidator missionVerificationValidator;

    @BeforeEach()
    void setUp() {
        mockMission = mock(Mission.class);
        when(mockMission.getBoardCount()).thenReturn(10);
    }

    @Test
    void 이미_완주한_미션은_검증에_실패한다() {
        MissionMember missionMember = new MissionMember(MemberFixture.create(), mockMission, 10);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> missionVerificationValidator.validateVerificationSubmission(missionMember));
        assertEquals(ErrorCode.ALREADY_COMPLETED_MISSION.getMessage(), exception.getMessage());
    }

    @Test
    void 중복된_인증은_검증에_실패한다() {
        MissionMember missionMember = new MissionMember(MemberFixture.create(), mockMission, 1);

        when(missionVerificationRepository.findByMemberIdAndMissionIdAndDate(any(), any(), any(LocalDate.class)))
                .thenReturn(Optional.of(mock(MissionVerification.class)));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> missionVerificationValidator.validateVerificationSubmission(missionMember));
        assertEquals(ErrorCode.DUPLICATE_VERIFICATION.getMessage(), exception.getMessage());
    }

    @Test
    void 미션_기간이_아니므로_검증에_실패한다() {
        MissionMember missionMember = new MissionMember(MemberFixture.create(), mockMission, 1);

        when(mockMission.isMissionPeriod()).thenReturn(false);
        when(missionVerificationRepository.findByMemberIdAndMissionIdAndDate(any(), any(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> missionVerificationValidator.validateVerificationSubmission(missionMember));
        assertEquals(ErrorCode.NOT_VERIFICATION_PERIOD.getMessage(), exception.getMessage());
    }

    @Test
    void 지정한_미션_요일이_아니므로_검증에_실패한다() {
        MissionMember missionMember = new MissionMember(MemberFixture.create(), mockMission, 1);

        when(mockMission.isMissionPeriod()).thenReturn(true);
        when(mockMission.isMissionDay()).thenReturn(false);
        when(missionVerificationRepository.findByMemberIdAndMissionIdAndDate(any(), any(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> missionVerificationValidator.validateVerificationSubmission(missionMember));
        assertEquals(ErrorCode.NOT_VERIFICATION_DAY.getMessage(), exception.getMessage());
    }

    @Test
    void 지정한_미션_시간대가_아니므로_검증에_실패한다() {
        MissionMember missionMember = new MissionMember(MemberFixture.create(), mockMission, 1);

        when(mockMission.isMissionPeriod()).thenReturn(true);
        when(mockMission.isMissionDay()).thenReturn(true);
        when(mockMission.isMissionTime()).thenReturn(false);
        when(missionVerificationRepository.findByMemberIdAndMissionIdAndDate(any(), any(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> missionVerificationValidator.validateVerificationSubmission(missionMember));
        assertEquals(ErrorCode.NOT_VERIFICATION_TIME.getMessage(), exception.getMessage());
    }
}
