package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
