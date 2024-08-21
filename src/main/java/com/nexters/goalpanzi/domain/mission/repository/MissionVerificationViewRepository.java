package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.MissionVerificationView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionVerificationViewRepository extends JpaRepository<MissionVerificationView, Long> {

    Optional<MissionVerificationView> findByMissionVerificationIdAndMemberId(final Long missionVerificationId, final Long memberId);

    default MissionVerificationView getMissionVerificationView(final Long missionVerificationId, final Long memberId) {
        return findByMissionVerificationIdAndMemberId(missionVerificationId, memberId).orElse(null);
    }
}
