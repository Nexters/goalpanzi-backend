package com.nexters.goalpanzi.domain.device;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "os_type", nullable = false)
    private OsType osType;

    @Column(name = "push_activation_status", nullable = false)
    private Boolean pushActivationStatus;

    public Device(final Member member, final String deviceIdentifier, final String deviceToken, final OsType osType) {
        this.member = member;
        this.deviceIdentifier = deviceIdentifier;
        this.deviceToken = deviceToken;
        this.osType = osType;
        this.pushActivationStatus = true;
    }

    public void updateDeviceToken(final String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void updatePushActivationStatus(final boolean pushActivationStatus) {
        this.pushActivationStatus = pushActivationStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Device that = (Device) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }
}
