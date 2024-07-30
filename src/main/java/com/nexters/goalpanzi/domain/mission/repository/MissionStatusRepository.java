package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.MissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MissionStatusRepository extends JpaRepository<MissionStatus, Long> {
    Optional<MissionStatus> findByMemberIdAndMissionId(final Long memberId, final Long missionId);
}
