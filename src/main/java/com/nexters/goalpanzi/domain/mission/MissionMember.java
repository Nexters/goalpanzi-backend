package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
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

    public MissionMember(final Member member, final Mission mission, final Integer verificationCount) {
        this.member = member;
        this.mission = mission;
        this.verificationCount = verificationCount;
    }

    public static MissionMember join(final Member member, final Mission mission) {
        if (mission.isMissionPeriod()) {
            throw new IllegalArgumentException(CAN_NOT_JOIN_MISSION.getMessage());
        }
        return new MissionMember(member, mission, 0);
    }

    public void verify() {
        this.verificationCount++;
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
