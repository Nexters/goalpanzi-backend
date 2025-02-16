package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MissionVerificationRepository extends JpaRepository<MissionVerification, Long> {

    List<MissionVerification> findAllByMemberId(final Long memberId);

    List<MissionVerification> findAllByMissionId(final Long missionId);

    Optional<MissionVerification> findByMemberIdAndMissionIdAndBoardNumber(final Long memberId, final Long missionId, final Integer boardNumber);

    @Query("SELECT mv FROM MissionVerification mv"
            + " JOIN FETCH mv.mission ms"
            + " WHERE ms.id = :missionId AND DATE(mv.createdAt) = :date")
    List<MissionVerification> findAllByMissionIdAndDate(final Long missionId, final LocalDate date);

    @Query("SELECT mv FROM MissionVerification mv"
            + " JOIN FETCH mv.member mb JOIN FETCH mv.mission ms"
            + " WHERE mb.id = :memberId AND ms.id = :missionId AND DATE(mv.createdAt) = :date")
    Optional<MissionVerification> findByMemberIdAndMissionIdAndDate(Long memberId, Long missionId, LocalDate date);

    List<MissionVerification> findByMemberIdAndMissionIdIn(final Long memberId, final List<Long> missionIds);

    Slice<MissionVerification> findByMemberIdAndMissionId(final Long memberId, final Long missionId, final Pageable pageable);

    default MissionVerification getMyVerification(final Long memberId, final Long missionId, final Integer boardNumber) {
        return findByMemberIdAndMissionIdAndBoardNumber(memberId, missionId, boardNumber)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));
    }
}