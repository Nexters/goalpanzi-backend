package com.nexters.goalpanzi.domain.device;

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

    @Column(name = "device_identifier", nullable = false)
    private String deviceIdentifier;

    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    @Column(name = "push_activation_status", nullable = false)
    private Boolean pushActivationStatus = true;

    public Device(final Member member, final String deviceIdentifier, final String deviceToken) {
        this.member = member;
        this.deviceIdentifier = deviceIdentifier;
        this.deviceToken = deviceToken;
    }

    public void updateDeviceToken(final String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void updatePushActivationStatus(final boolean pushActivationStatus) {
        this.pushActivationStatus = pushActivationStatus;
    }
}
