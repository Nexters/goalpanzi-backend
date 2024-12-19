package com.nexters.goalpanzi.domain.device.repository;

import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByMemberIdAndDeviceIdentifier(final Long memberId, final String deviceIdentifier);

    List<Device> findAllByMemberId(final Long memberId);

    List<Device> findAllByDeviceIdentifier(final String deviceIdentifier);

    boolean existsByDeviceIdentifier(final String deviceIdentifier);

    default Device getDevice(final Long deviceId) {
        return findById(deviceId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_DEVICE));
    }

    default Device getDevice(final Long memberId, final String deviceIdentifier) {
        return findByMemberIdAndDeviceIdentifier(memberId, deviceIdentifier)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_DEVICE));
    }
}
