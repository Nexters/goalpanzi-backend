package com.nexters.goalpanzi.domain.mission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MissionVerificationRepository extends JpaRepository<MissionVerification, Long> {
    Optional<MissionVerification> findByMemberIdAndMissionIdAndBoardNumber(final Long memberId, final Long missionId, final Integer boardNumber);

    @Query(value = "SELECT * FROM mission_verification mv WHERE mv.member_id = :memberId AND mv.mission_id = :missionId AND DATE(mv.created_at) = :date", nativeQuery = true)
    Optional<MissionVerification> findByMemberIdAndMissionIdAndDate(@Param("memberId") Long memberId,
                                                                    @Param("missionId") Long missionId,
                                                                    @Param("date") LocalDate date);
}