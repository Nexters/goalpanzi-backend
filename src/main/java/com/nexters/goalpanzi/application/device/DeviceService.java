package com.nexters.goalpanzi.application.device;

import com.nexters.goalpanzi.application.device.dto.request.UpdateDeviceTokenCommand;
import com.nexters.goalpanzi.application.device.dto.request.UpdatePushActivationStatusCommand;
import com.nexters.goalpanzi.application.device.event.UpdateDeviceTokenEvent;
import com.nexters.goalpanzi.application.device.event.UpdatePushActivationStatusEvent;
import com.nexters.goalpanzi.domain.device.Device;
import com.nexters.goalpanzi.domain.device.OsType;
import com.nexters.goalpanzi.domain.device.repository.DeviceRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class DeviceService {

    private final MemberRepository memberRepository;
    private final DeviceRepository deviceRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void updateDeviceToken(final UpdateDeviceTokenCommand command) {
        if (existsMemberDevice(command.memberId(), command.deviceIdentifier())) {
            updateDevice(command.memberId(), command.deviceIdentifier(), command.deviceToken());
        } else {
            createDevice(command.memberId(), command.deviceIdentifier(), command.deviceToken(), command.osType());
        }
    }

    private boolean existsMemberDevice(final Long memberId, final String deviceIdentifier) {
        return !deviceIdentifier.isBlank() && deviceRepository.existsByMemberIdAndDeviceIdentifier(memberId, deviceIdentifier);
    }

    private void createDevice(final Long memberId, final String deviceIdentifier, final String deviceToken, final OsType osType) {
        Member member = memberRepository.getMember(memberId);
        Device device = deviceRepository.save(new Device(member, deviceIdentifier, deviceToken, osType));

        eventPublisher.publishEvent(
                new UpdateDeviceTokenEvent(memberId, device.getId(), null)
        );
    }

    private void updateDevice(final Long memberId, final String deviceIdentifier, final String deviceToken) {
        Device device = deviceRepository.getDevice(memberId, deviceIdentifier);

        String deprecatedToken = device.getDeviceToken();
        device.updateDeviceToken(deviceToken);
        device.updatePushActivationStatus(true);

        eventPublisher.publishEvent(
                new UpdateDeviceTokenEvent(memberId, device.getId(), deprecatedToken)
        );
    }

    @Transactional
    public void updatePushActivationStatus(final UpdatePushActivationStatusCommand command) {
        Device device = deviceRepository.getDevice(command.memberId(), command.deviceIdentifier());

        device.updatePushActivationStatus(command.pushActivationStatus());

        eventPublisher.publishEvent(
                new UpdatePushActivationStatusEvent(command.memberId(), device.getId(), command.pushActivationStatus(), device.getDeviceToken())
        );
    }
}
