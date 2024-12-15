package com.nexters.goalpanzi.domain.firebase.repository;

import com.nexters.goalpanzi.domain.firebase.Device;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByMemberIdAndDeviceToken(final Long memberId, final String deviceToken);

    default Device getDevice(final Long memberId, final String deviceToken) {
        return findByMemberIdAndDeviceToken(memberId, deviceToken)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_DEVICE));
    }
}
