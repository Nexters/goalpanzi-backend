package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MissionMemberRepository extends JpaRepository<MissionMember, Long> {
    Optional<MissionMember> findByMemberIdAndMissionId(final Long memberId, final Long missionId);

    List<MissionMember> findAllByMissionId(final Long MissionId);

    List<MissionMember> findAllByMissionId(final Long missionId, Sort sort);

    @Query("SELECT mm FROM MissionMember mm JOIN FETCH mm.mission WHERE mm.member.id = :memberId")
    List<MissionMember> findAllWithMissionByMemberId(final Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default MissionMember getMissionMember(final Long memberId, final Long missionId) {
        return findByMemberIdAndMissionId(memberId, missionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_JOINED_MISSION_MEMBER));
    }
}
