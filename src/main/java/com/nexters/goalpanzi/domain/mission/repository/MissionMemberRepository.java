package com.nexters.goalpanzi.domain.mission.repository;

import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionStatus;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MissionMemberRepository extends JpaRepository<MissionMember, Long> {

    @Query("SELECT mm FROM MissionMember mm JOIN FETCH mm.member WHERE mm.mission.id IN :missionIds")
    List<MissionMember> findAllByMissionIdIn(final List<Long> missionIds);

    Optional<MissionMember> findByMemberIdAndMissionId(final Long memberId, final Long missionId);

    @Query("SELECT mm FROM MissionMember mm"
            + " JOIN FETCH mm.member JOIN FETCH mm.mission"
            + " WHERE mm.member.id = :memberId AND mm.mission.id = :missionId"
    )
    Optional<MissionMember> findWithMemberAndMissionByMemberIdAndMissionId(final Long memberId, final Long missionId);

    List<MissionMember> findAllByMissionId(final Long MissionId);

    List<MissionMember> findAllByMissionId(final Long missionId, Sort sort);

    @Query("SELECT mm FROM MissionMember mm JOIN FETCH mm.mission WHERE mm.member.id = :memberId")
    List<MissionMember> findAllWithMissionByMemberId(final Long memberId);

    @Query("SELECT mm FROM MissionMember mm JOIN FETCH mm.member WHERE mm.mission.id = :missionId")
    List<MissionMember> findAllWithMemberByMissionId(final Long missionId);

    Optional<MissionMember> findTop1ByMemberIdOrderByUpdatedAtDesc(final Long memberId);

    Slice<MissionMember> findByMemberIdAndMissionStatus(
            final Long memberId,
            final MissionStatus status,
            final Pageable pageable
    );

    long countByMemberIdAndMissionStatus(final Long memberId, final MissionStatus status);

    default MissionMember getMissionMember(final Long memberId, final Long missionId) {
        return findByMemberIdAndMissionId(memberId, missionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_JOINED_MISSION_MEMBER));
    }

    default MissionMember getMissionMemberWithMemberAndMission(final Long memberId, final Long missionId) {
        return findWithMemberAndMissionByMemberIdAndMissionId(memberId, missionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_JOINED_MISSION_MEMBER));
    }

    default long getDaysAfterMissionCompletion(final Long memberId) {
        Optional<MissionMember> missionMember = findTop1ByMemberIdOrderByUpdatedAtDesc(memberId);
        if (missionMember.isEmpty() || missionMember.get().getMissionStatus().equals(MissionStatus.COMPLETED)) {
            return 0L;
        }
        Duration duration = Duration.between(LocalDateTime.now(), missionMember.get().getUpdatedAt());
        return duration.toDays();
    }
}
