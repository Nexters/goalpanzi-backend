package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.infrastructure.jpa.DaysOfWeekConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.joda.time.LocalTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@SQLRestriction("deleted_at is NULL")
@Table(name = "mission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Mission extends BaseEntity {

    public static final Integer MAX_MISSION_MEMBER = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @Column(name = "host_member_id", nullable = false)
    private Long hostMemberId;

    @Column(name = "description", nullable = false)
    private String description;

    @Embedded
    @Column(name = "invitation_code", nullable = false)
    private InvitationCode invitationCode;

    @Column(name = "mission_start_date", nullable = false)
    private LocalDateTime missionStartDate;

    @Column(name = "mission_end_date", nullable = false)
    private LocalDateTime missionEndDate;

    @Column(name = "upload_start_time", nullable = false)
    private String uploadStartTime;

    @Column(name = "upload_end_time", nullable = false)
    private String uploadEndTime;

    @Column(name = "board_count", nullable = false)
    private Integer boardCount;

    @Convert(converter = DaysOfWeekConverter.class)
    @Column(name = "mission_day", nullable = false)
    private List<DayOfWeek> missionDays;

    public Mission(final Long hostMemberId, final String description, final InvitationCode invitationCode,
                   final LocalDateTime missionStartDate, final LocalDateTime missionEndDate,
                   final String uploadStartTime, final String uploadEndTime,
                   final List<DayOfWeek> missionDays, final Integer boardCount) {

        this.hostMemberId = hostMemberId;
        this.description = description;
        this.invitationCode = invitationCode;
        this.missionStartDate = missionStartDate;
        this.missionEndDate = missionEndDate;
        this.uploadStartTime = uploadStartTime;
        this.uploadEndTime = uploadEndTime;
        this.missionDays = missionDays;
        this.boardCount = boardCount;

        validateMission();
    }

    public static Mission create(
            final Long hostMemberId,
            final String description,
            final LocalDateTime missionStartDate,
            final LocalDateTime missionEndDate,
            final TimeOfDay timeOfDay,
            final List<DayOfWeek> missionDays,
            final Integer boardCount,
            final InvitationCode invitationCode
    ) {

        return new Mission(
                hostMemberId,
                description,
                invitationCode,
                missionStartDate,
                missionEndDate,
                timeOfDay.getStartTime(),
                timeOfDay.getEndTime(),
                missionDays,
                boardCount
        );
    }

    private void validateMission() {
        if (missionStartDate.isAfter(missionEndDate)) {
            throw new IllegalArgumentException("미션 시작일과 종료일이 올바르지 않습니다.");
        }
        if (missionDays.isEmpty()) {
            throw new IllegalArgumentException("미션 요일은 빈 값일 수 없습니다.");
        }

        if (boardCount <= 0) {
            throw new IllegalArgumentException("보드 칸 개수는 최소 1이어야 합니다.");
        }
    }

    public boolean isMissionPeriod() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(this.missionStartDate.toLocalDate()) && !today.isAfter(missionEndDate.toLocalDate());
    }

    public boolean isMissionDay() {
        return this.missionDays.contains(DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name()));
    }

    public boolean isMissionTime() {
        String now = LocalTime.now().toString().substring(0, 5);
        return now.compareTo(uploadStartTime) >= 0 && now.compareTo(uploadEndTime) <= 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mission that = (Mission) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, invitationCode);
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", hostMemberId=" + hostMemberId +
                ", description='" + description + '\'' +
                ", invitationCode=" + invitationCode +
                ", missionStartDate=" + missionStartDate +
                ", missionEndDate=" + missionEndDate +
                ", uploadStartTime='" + uploadStartTime + '\'' +
                ", uploadEndTime='" + uploadEndTime + '\'' +
                ", boardCount=" + boardCount +
                ", missionDays=" + missionDays +
                '}';
    }
}
