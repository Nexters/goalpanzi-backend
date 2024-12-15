package com.nexters.goalpanzi.domain.firebase;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device")
@NoArgsConstructor
@Getter
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    public Device(final Member member, final String deviceToken) {
        this.member = member;
        this.deviceToken = deviceToken;
    }

    public void updateDeviceToken(final String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
