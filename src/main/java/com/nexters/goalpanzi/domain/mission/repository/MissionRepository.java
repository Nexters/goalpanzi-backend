package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findAllByIdInAndDeletedAtIsNull(List<Long> missionIds);

    Optional<Mission> findByInvitationCode(final InvitationCode invitationCode);

    List<Mission> findByMissionStartDateGreaterThanEqual(final LocalDateTime todayStart);

    List<Mission> findByMissionStartDateLessThanEqualAndMissionEndDateGreaterThanEqual(final LocalDateTime startDate, final LocalDateTime endDate);

    default Mission getMission(final Long missionId) {
        return findById(missionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION, missionId));
    }

    default List<Mission> getReadyMissions() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return findByMissionStartDateGreaterThanEqual(todayStart);
    }

    default List<Mission> getInProgressMissions() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return findByMissionStartDateLessThanEqualAndMissionEndDateGreaterThanEqual(todayStart, todayStart);
    }
}
