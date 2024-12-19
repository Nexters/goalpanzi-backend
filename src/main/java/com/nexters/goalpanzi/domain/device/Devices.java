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

    public List<String> getDeactivatedDeviceTokens() {
        return devices.stream()
                .filter(it -> !it.getPushActivationStatus())
                .map(Device::getDeviceToken)
                .toList();
    }
}
