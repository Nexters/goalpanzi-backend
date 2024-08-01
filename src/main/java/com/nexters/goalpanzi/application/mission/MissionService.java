package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;

    public MissionDetailResponse createMission(final CreateMissionCommand command) {
        Mission mission = Mission.create(
                command.hostMemberId(),
                command.description(),
                command.missionStartDate(),
                command.missionEndDate(),
                command.timeOfDay(),
                command.missionDays(),
                command.boardCount()
        );

        return MissionDetailResponse.from(missionRepository.save(mission));
    }
}
