package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.CreateMissionRequest;
import com.nexters.goalpanzi.application.mission.dto.MissionResponse;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;

    public MissionResponse createMission(final Long hostId, final CreateMissionRequest request) {
        Mission mission = Mission.create(
                hostId,
                request.description(),
                request.missionStartDate(),
                request.missionEndDate(),
                request.timeOfDay(),
                request.missionDays(),
                request.boardCount()
        );

        return MissionResponse.from(missionRepository.save(mission));
    }
}
