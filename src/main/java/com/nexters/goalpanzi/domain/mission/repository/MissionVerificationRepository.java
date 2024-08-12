package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MissionVerificationRepository extends JpaRepository<MissionVerification, Long> {

    List<MissionVerification> findAllByMemberId(final Long memberId);

    List<MissionVerification> findAllByMissionId(final Long missionId);

    Optional<MissionVerification> findByMemberIdAndMissionIdAndBoardNumber(final Long memberId, final Long missionId, final Integer boardNumber);

    @Query(value = "select mv.* from mission_verification mv where mv.mission_id = :missionId and date(mv.created_at) = :date", nativeQuery = true)
    List<MissionVerification> findAllByMissionIdAndDate(final Long missionId, final LocalDate date);

    @Query(value = "select mv.* from mission_verification mv where mv.member_id = :memberId AND mv.mission_id = :missionId and date(mv.created_at) = :date", nativeQuery = true)
    Optional<MissionVerification> findByMemberIdAndMissionIdAndDate(Long memberId, Long missionId, LocalDate date);

    default MissionVerification getMyVerification(final Long memberId, final Long missionId, final Integer boardNumber) {
        return findByMemberIdAndMissionIdAndBoardNumber(memberId, missionId, boardNumber)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));
    }
}