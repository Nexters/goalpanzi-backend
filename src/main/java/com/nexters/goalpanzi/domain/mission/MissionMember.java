package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

import static com.nexters.goalpanzi.exception.ErrorCode.CAN_NOT_JOIN_MISSION;

@Entity
@SQLRestriction("deleted_at is NULL")
@Table(name = "mission_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MissionMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "verification_count")
    private Integer verificationCount;

    @Column(name = "check_completed")
    private Boolean checkCompleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_status")
    private MissionStatus missionStatus;

    public MissionMember(final Member member, final Mission mission, final Integer verificationCount) {
        this.member = member;
        this.mission = mission;
        this.verificationCount = verificationCount;
        this.checkCompleted = false;
        this.missionStatus = MissionStatus.CREATED;
    }

    public static MissionMember join(final Member member, final Mission mission) {
        if (mission.isMissionPeriod()) {
            throw new BadRequestException(CAN_NOT_JOIN_MISSION);
        }
        return new MissionMember(member, mission, 0);
    }

    public void verify() {
        this.verificationCount++;
    }

    public void updateMissionStatus(
            final Mission mission,
            final Integer currentMemberCount
    ) {
        missionStatus = MissionStatus.fromMission(mission, currentMemberCount, this);
    }

    public void checkCompleted() {
        this.checkCompleted = true;
        this.missionStatus = MissionStatus.COMPLETED;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissionMember that = (MissionMember) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member.getId(), mission.getId());
    }

    @Override
    public String toString() {
        return "MissionMember{" +
                "id=" + id +
                ", member=" + member.getId() +
                ", mission=" + mission.getId() +
                ", verificationCount=" + verificationCount +
                '}';
    }
}
