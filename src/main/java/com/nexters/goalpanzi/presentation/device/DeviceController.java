package com.nexters.goalpanzi.presentation.device;

import com.nexters.goalpanzi.application.device.DeviceService;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.device.dto.UpdateDeviceTokenRequest;
import com.nexters.goalpanzi.presentation.device.dto.UpdatePushActivationStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/device")
@RestController
public class DeviceController implements DeviceControllerDocs {

    private final DeviceService deviceService;

    @Override
    @PatchMapping("/device-token")
    public ResponseEntity<Void> updateDeviceToken(
            @LoginMemberId final Long memberId,
            @RequestBody @Valid final UpdateDeviceTokenRequest request
    ) {
        deviceService.updateDeviceToken(request.toServiceDto(memberId));

        return ResponseEntity.ok().build();
    }

    @Override
    @PatchMapping("/push-activation-status")
    public ResponseEntity<Void> updatePushActivationStatus(
            @LoginMemberId final Long memberId,
            @RequestBody @Valid final UpdatePushActivationStatusRequest request
    ) {
        deviceService.updatePushActivationStatus(request.toServiceDto(memberId));

        return ResponseEntity.ok().build();
    }
}
