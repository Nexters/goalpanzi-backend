package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class MissionVerificationValidator {

    private final MissionVerificationRepository missionVerificationRepository;

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
        return missionVerificationRepository.findByMemberIdAndMissionIdAndDate(memberId, missionId, LocalDate.now()).isPresent();
    }

    private void validateTime(final Mission mission) {
        if (!mission.isMissionPeriod()) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_PERIOD);
        }
        if (!mission.isMissionDay()) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_DAY);
        }
        if (!mission.isMissionTime()) {
            throw new BadRequestException(ErrorCode.NOT_VERIFICATION_TIME);
        }
    }
}
