package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.common.time.TimeProvider;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.nexters.goalpanzi.domain.mission.Mission.MAX_MISSION_MEMBER;
import static com.nexters.goalpanzi.domain.mission.Mission.MIN_MISSION_MEMBER;

@RequiredArgsConstructor
@Component
public class MissionValidator {

    private final MissionMemberRepository missionMemberRepository;
    private final MissionRepository missionRepository;
    private final TimeProvider timeProvider;

    public void validateJoinableMission(final InvitationCode invitationCode) {
        Mission mission = missionRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION));
        validateMissionPeriod(mission);
        validateMaxPersonnel(mission);
    }

    public void validateMaxPersonnel(final Mission mission) {
        if (getMissionMemberSize(mission.getId()) > MAX_MISSION_MEMBER) {
            throw new BadRequestException(ErrorCode.EXCEED_MAX_PERSONNEL);
        }
    }

    public void validateMissionPeriod(final Mission mission) {
        LocalDateTime now = timeProvider.now();
        if (mission.isMissionPeriod(now) || mission.isExpired(now.toLocalDate())) {
            throw new BadRequestException(ErrorCode.CAN_NOT_JOIN_MISSION);
        }
    }

    public boolean hasEnoughMember(final Long missionId) {
        return getMissionMemberSize(missionId) >= MIN_MISSION_MEMBER;
    }

    private int getMissionMemberSize(final Long missionId) {
        return missionMemberRepository.findAllByMissionId(missionId).size();
    }
}
