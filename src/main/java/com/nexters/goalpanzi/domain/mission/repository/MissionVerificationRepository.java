package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissionVerificationRepository extends JpaRepository<MissionVerification, Long> {

    List<MissionVerification> findByMemberId(final Long memberId);

    Optional<MissionVerification> findByMemberIdAndMissionIdAndBoardNumber(final Long memberId, final Long missionId, final Integer boardNumber);

    @Query("SELECT mv FROM MissionVerification mv WHERE mv.mission.id = :missionId AND Date(mv.createdAt) = :date")
    List<MissionVerification> findAllByMissionIdAndDate(@Param("missionId") final Long missionId, @Param("date") final LocalDate date);

    @Query("SELECT mv FROM MissionVerification mv WHERE mv.member.id = :memberId AND mv.mission.id = :missionId AND Date(mv.createdAt) = :date")
    Optional<MissionVerification> findByMemberIdAndMissionIdAndDate(@Param("memberId") Long memberId, @Param("missionId") Long missionId, @Param("date") LocalDate date);

    default MissionVerification getMyVerification(final Long memberId, final Long missionId, final Integer boardNumber) {
        return findByMemberIdAndMissionIdAndBoardNumber(memberId, missionId, boardNumber)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));
    }
}