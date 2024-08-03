package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;

    @Transactional
    public MissionDetailResponse createMission(final CreateMissionCommand command) {
        Mission mission = Mission.create(
                command.hostMemberId(),
                command.description(),
                command.missionStartDate(),
                command.missionEndDate(),
                command.timeOfDay(),
                command.missionDays(),
                command.boardCount(),
                generateInvitationCode()
        );

        return MissionDetailResponse.from(missionRepository.save(mission));
    }

    private InvitationCode generateInvitationCode() {
        InvitationCode invitationCode;
        do {
            invitationCode = InvitationCode.generate();
        } while (alreadyExistInvitationCode(invitationCode));
        return invitationCode;
    }

    private boolean alreadyExistInvitationCode(final InvitationCode invitationCode) {
        return missionRepository.findByInvitationCode(invitationCode).isPresent();
    }

    public MissionDetailResponse getMission(final Long missionId) {
        Mission mission = missionRepository.getMission(missionId);

        return MissionDetailResponse.from(mission);
    }
}
