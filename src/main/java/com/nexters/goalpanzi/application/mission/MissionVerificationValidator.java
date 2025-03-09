package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.common.time.TimeProvider;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j // TODO 오류 확인 후 삭제
@RequiredArgsConstructor
@Component
public class MissionVerificationValidator {

    private final MissionVerificationRepository missionVerificationRepository;
    private final TimeProvider timeProvider;

    public void validate(final MissionMember missionMember) {
        Mission mission = missionMember.getMission();

        validateCompletion(mission, missionMember);
        validateDuplication(mission, missionMember);
        validateTime(mission);
    }

    private void validateCompletion(final Mission mission, final MissionMember missionMember) {
        if (isCompletedMission(mission, missionMember)) {
            throw new BadRequestException(ErrorCode.ALREADY_COMPLETED_MISSION);
        }
    }

    private boolean isCompletedMission(final Mission mission, final MissionMember missionMember) {
        return missionMember.getVerificationCount() >= mission.getBoardCount();
    }

    private void validateDuplication(final Mission mission, final MissionMember missionMember) {
        if (isDuplicatedVerification(missionMember.getId(), mission.getId())) {
            throw new BadRequestException(ErrorCode.DUPLICATE_VERIFICATION);
        }
    }

    private boolean isDuplicatedVerification(final Long memberId, final Long missionId) {
//        TODO
//        return missionVerificationRepository.findByMemberIdAndMissionIdAndDate(memberId, missionId, LocalDate.now()).isPresent();
        log.info("Check Duplication of Mission Verification.");
        LocalDate today = LocalDate.now();
        Optional<MissionVerification> missionVerification = missionVerificationRepository.findByMemberIdAndMissionIdAndDate(memberId, missionId, today);
        log.info("[{}] Is Duplicated : {}", today, missionVerification.isPresent());
        return missionVerification.isPresent();
    }

    private void validateTime(final Mission mission) {
        LocalDateTime now = timeProvider.now();
        if (!mission.isMissionPeriod(now)) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_PERIOD);
        }
        if (!mission.isMissionDay(now.toLocalDate())) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_DAY);
        }
        if (!mission.isMissionTime(now.toLocalTime())) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_TIME);
        }
    }
}
