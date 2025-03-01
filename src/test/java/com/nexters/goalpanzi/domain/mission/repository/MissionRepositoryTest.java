package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.common.support.IntegrationTest;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.TimeOfDay;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.fixture.MemberFixture.MEMBER_ID;
import static com.nexters.goalpanzi.fixture.MissionFixture.*;
import static org.assertj.core.api.BDDAssertions.then;

class MissionRepositoryTest extends IntegrationTest {

    @Autowired
    private MissionRepository sut;

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

    @AfterEach
    void tearDown() {
        sut.deleteAllInBatch();
    }

    @Test
    void 준비_상태의_미션을_조회한다() {
        final Mission readyMission = sut.save(READY_MISSION);
        final Mission inProgressMission = sut.save(IN_PROGRESS_MISSION);
        final Mission completedMission = sut.save(COMPLETED_MISSION);

        final List<Mission> actual = sut.getReadyMissions();

        then(actual)
                .hasSize(1)
                .containsExactly(readyMission);
    }

    @Test
    void 진행중인_미션을_조회한다() {
        final Mission readyMission = sut.save(READY_MISSION);
        final Mission inProgressMission = sut.save(IN_PROGRESS_MISSION);
        final Mission completedMission = sut.save(COMPLETED_MISSION);

        final List<Mission> actual = sut.getInProgressMissions();

        then(actual)
                .hasSize(1)
                .containsExactly(inProgressMission);
    }
}