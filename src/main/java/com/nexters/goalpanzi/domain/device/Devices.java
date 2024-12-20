package com.nexters.goalpanzi.domain.device;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Devices {

    private final List<Device> devices;

    public List<Device> getActivatedDevices() {
        return devices.stream()
                .filter(Device::getPushActivationStatus)
                .toList();
    }

    public List<String> getActivatedDeviceTokens() {
        return getActivatedDevices().stream()
                .map(Device::getDeviceToken)
                .toList();
    }

    // TODO: 추후 불필요하면 삭제
    public List<String> getDeactivatedDeviceTokens() {
        return devices.stream()
                .filter(it -> !it.getPushActivationStatus())
                .map(Device::getDeviceToken)
                .toList();
    }

    public List<Long> getFilteredMemberIds(final Long excludedMemberId) {
        return devices.stream()
                .filter(it -> it.getMember().getId() != excludedMemberId)
                .map(it -> it.getMember().getId())
                .toList();
    }
}
