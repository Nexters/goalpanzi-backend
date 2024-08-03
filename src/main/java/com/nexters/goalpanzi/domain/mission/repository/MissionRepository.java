package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    Optional<Mission> findByInvitationCode(final InvitationCode invitationCode);

    default Mission getMission(final Long missionId) {
        return findById(missionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MISSION, missionId));
    }
}
