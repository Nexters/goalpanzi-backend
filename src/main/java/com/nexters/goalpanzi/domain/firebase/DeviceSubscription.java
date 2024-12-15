package com.nexters.goalpanzi.domain.firebase;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.mission.Mission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "device_subscription")
@SQLRestriction("deleted_at is NULL")
@NoArgsConstructor
@Getter
public class DeviceSubscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_subscription_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    public DeviceSubscription(final Device device, final Mission mission) {
        this.device = device;
        this.mission = mission;
    }
}
