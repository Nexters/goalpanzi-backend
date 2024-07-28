package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.CreateMissionCommand;
import com.nexters.goalpanzi.application.mission.dto.MissionResponse;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;

    public MissionResponse createMission(final CreateMissionCommand command) {
        Mission mission = Mission.create(
                command.hostMemberId(),
                command.description(),
                command.missionStartDate(),
                command.missionEndDate(),
                command.timeOfDay(),
                command.missionDays(),
                command.boardCount()
        );

        return MissionResponse.from(missionRepository.save(mission));
    }
}
