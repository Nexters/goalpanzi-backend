package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.MissionMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissionMemberRepository extends JpaRepository<MissionMember, Long> {
    Optional<MissionMember> findByMemberIdAndMissionId(final Long memberId, final Long missionId);

    List<MissionMember> findAllByMissionId(final Long MissionId);

    List<MissionMember> findAllByMemberId(final Long memberId);
}
