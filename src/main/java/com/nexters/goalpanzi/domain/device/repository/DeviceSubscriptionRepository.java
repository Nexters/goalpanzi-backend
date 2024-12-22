package com.nexters.goalpanzi.domain.device.repository;

import com.nexters.goalpanzi.domain.device.DeviceSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceSubscriptionRepository extends JpaRepository<DeviceSubscription, Long> {

    @Query("SELECT ds FROM DeviceSubscription ds"
            + " JOIN FETCH ds.mission JOIN FETCH ds.device dd"
            + " WHERE dd.id = :deviceId"
    )
    List<DeviceSubscription> findAllWithMissionAndDeviceByDeviceId(final Long deviceId);

    @Query("SELECT ds FROM DeviceSubscription ds"
            + " JOIN FETCH ds.device JOIN FETCH ds.mission dm"
            + " WHERE dm.id = :missionId"
    )
    List<DeviceSubscription> findAllWithDeviceAndMissionByMissionId(final Long missionId);

    @Query("SELECT ds FROM DeviceSubscription ds"
            + " JOIN FETCH ds.device dd"
            + " WHERE ds.mission.id = :missionId AND dd.id IN :deviceIds")
    List<DeviceSubscription> findAllWithDeviceByMissionIdAndDeviceIds(final Long missionId, final List<Long> deviceIds);

    void deleteAllByMissionId(final Long missionId);
}
