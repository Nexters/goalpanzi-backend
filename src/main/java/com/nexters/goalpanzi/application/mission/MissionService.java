package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.MissionDto;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MissionService {

    private final MissionRepository missionRepository;

    public MissionDto.MissionResponse createMission(final MissionDto.Create request) {
        Mission mission = Mission.create(
                request.hostMemberId(),
                request.description(),
                request.missionStartDate(),
                request.missionEndDate(),
                request.timeOfDay(),
                request.missionDays(),
                request.boardCount()
        );

        return MissionDto.MissionResponse.from(missionRepository.save(mission));
    }
}
