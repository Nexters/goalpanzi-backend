package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "mission_verification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MissionVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_verification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "board_number")
    private Integer boardNumber;

    public MissionVerification(final Member member, final Mission mission, final String imageUrl, final Integer boardNumber) {
        this.member = member;
        this.mission = mission;
        this.imageUrl = imageUrl;
        this.boardNumber = boardNumber;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissionVerification that = (MissionVerification) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member.getId(), mission.getId());
    }

    @Override
    public String toString() {
        return "MissionVerification{" +
                "id=" + id +
                ", member=" + member.getId() +
                ", mission=" + mission.getId() +
                ", imageUrl='" + imageUrl + '\'' +
                ", boardNumber=" + boardNumber +
                '}';
    }
}
