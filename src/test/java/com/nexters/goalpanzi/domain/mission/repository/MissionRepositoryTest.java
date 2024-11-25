package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.MemberFixture.MEMBER_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class MissionRepositoryTest {

    @Autowired
    private MissionRepository missionRepository;

    private final Mission READY_MISSION = Mission.create(
            MEMBER_ID,
            DESCRIPTION,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(31),
            TimeOfDay.EVERYDAY,
            WEEK,
            BOARD_COUNT,
            InvitationCode.generate()
    );
    private final Mission IN_PROGRESS_MISSION = Mission.create(
            MEMBER_ID,
            DESCRIPTION,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(29),
            TimeOfDay.EVERYDAY,
            WEEK,
            BOARD_COUNT,
            InvitationCode.generate()
    );
    private final Mission COMPLETED_MISSION = Mission.create(
            MEMBER_ID,
            DESCRIPTION,
            LocalDateTime.now().minusDays(30),
            LocalDateTime.now().minusDays(1),
            TimeOfDay.EVERYDAY,
            WEEK,
            BOARD_COUNT,
            InvitationCode.generate()
    );

    @Test
    void 준비_상태의_미션을_조회한다() {
        Mission readyMission = missionRepository.save(READY_MISSION);
        Mission inProgressMission = missionRepository.save(IN_PROGRESS_MISSION);
        Mission completedMission = missionRepository.save(COMPLETED_MISSION);

        List<Mission> missions = missionRepository.getReadyMissions();

        assertAll(
                () -> assertThat(missions.size()).isEqualTo(1),
                () -> assertThat(missions.get(0).getId()).isEqualTo(readyMission.getId())
        );
    }

    @Test
    void 진행중인_미션을_조회한다() {
        Mission readyMission = missionRepository.save(READY_MISSION);
        Mission inProgressMission = missionRepository.save(IN_PROGRESS_MISSION);
        Mission completedMission = missionRepository.save(COMPLETED_MISSION);

        List<Mission> missions = missionRepository.getInProgressMissions();

        assertAll(
                () -> assertThat(missions.size()).isEqualTo(1),
                () -> assertThat(missions.get(0).getId()).isEqualTo(inProgressMission.getId())
        );
    }
}