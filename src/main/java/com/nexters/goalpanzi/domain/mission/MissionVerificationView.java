package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mission_verification_view")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MissionVerificationView extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_verification_view_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_verification_id", nullable = false)
    private MissionVerification missionVerification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public MissionVerificationView(final Long missionVerificationId, final Long memberId) {
        new MissionVerificationView(missionVerificationId, memberId);
    }
}
