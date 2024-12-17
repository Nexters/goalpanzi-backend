package com.nexters.goalpanzi.domain.device.repository;

import com.nexters.goalpanzi.domain.device.DeviceSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceSubscriptionRepository extends JpaRepository<DeviceSubscription, Long> {

    @Query("SELECT ds FROM DeviceSubscription ds"
            + " JOIN FETCH ds.mission JOIN FETCH ds.device"
            + " WHERE ds.device.id = :deviceId"
    )
    List<DeviceSubscription> findAllWithMissionAndDeviceByDeviceId(final Long deviceId);

    @Query("SELECT ds FROM DeviceSubscription ds"
            + " JOIN FETCH ds.device JOIN FETCH ds.mission"
            + " WHERE ds.mission.id = :missionId"
    )
    List<DeviceSubscription> findAllWithDeviceAndMissionByMissionId(final Long missionId);

    void deleteAllByMissionId(final Long missionId);
}
