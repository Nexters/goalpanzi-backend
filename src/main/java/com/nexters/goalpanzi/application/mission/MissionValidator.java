package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.exception.BadRequestException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.nexters.goalpanzi.domain.mission.Mission.MAX_MISSION_MEMBER;

@RequiredArgsConstructor
@Component
public class MissionValidator {

    private final MissionMemberRepository missionMemberRepository;
    private final MissionRepository missionRepository;

    public void validateJoinableMission(final InvitationCode invitationCode) {
        Mission mission = missionRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION.toString()));
        validateMissionPeriod(mission);
        validateMaxPersonnel(mission);
    }

    public void validateMaxPersonnel(final Mission mission) {
        if (getMissionMemberSize(mission.getId()) > MAX_MISSION_MEMBER) {
            throw new BadRequestException(ErrorCode.EXCEED_MAX_PERSONNEL.toString());
        }
    }

    public void validateMissionPeriod(final Mission mission) {
        if (mission.isMissionPeriod()) {
            throw new IllegalArgumentException(ErrorCode.CAN_NOT_JOIN_MISSION.toString());
        }
    }

    private int getMissionMemberSize(final Long missionId) {
        return missionMemberRepository.findAllByMissionId(missionId).size();
    }
}
