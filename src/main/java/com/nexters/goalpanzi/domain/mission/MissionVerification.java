package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public MissionVerification(final Member member, final Mission mission, final String imageUrl) {
        this.member = member;
        this.mission = mission;
        this.imageUrl = imageUrl;
    }
}
