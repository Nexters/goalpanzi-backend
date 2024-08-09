package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.application.mission.event.DeleteMissionEvent;
import com.nexters.goalpanzi.application.mission.event.CreateMissionEvent;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.ForbiddenException;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MissionDetailResponse createMission(final CreateMissionCommand command) {
        Mission mission = missionRepository.save(
                Mission.create(
                        command.hostMemberId(),
                        command.description(),
                        command.missionStartDate(),
                        command.missionEndDate(),
                        command.timeOfDay(),
                        command.missionDays(),
                        command.boardCount(),
                        generateInvitationCode()
                )
        );
        eventPublisher.publishEvent(new CreateMissionEvent(
                mission.getHostMemberId(),
                mission.getInvitationCode().getCode())
        );

        return MissionDetailResponse.from(mission);
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

    public MissionDetailResponse getMissionByInvitationCode(final InvitationCode invitationCode) {
        Mission mission = missionRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION, invitationCode));

        return MissionDetailResponse.from(mission);
    }

    @Transactional
    public void deleteMission(final Long memberId, final Long missionId) {
        Mission mission = missionRepository.getMission(missionId);
        validateAuthority(memberId, mission);
        mission.delete();
        eventPublisher.publishEvent(new DeleteMissionEvent(mission.getId()));
    }

    private void validateAuthority(final Long memberId, final Mission mission) {
        if (!mission.getHostMemberId().equals(memberId)) {
            throw new ForbiddenException(ErrorCode.CANNOT_DELETE_MISSION);
        }
    }
}
