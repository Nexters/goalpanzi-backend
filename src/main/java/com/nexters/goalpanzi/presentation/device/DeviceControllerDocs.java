package com.nexters.goalpanzi.presentation.device;

import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.device.dto.UpdateDeviceTokenRequest;
import com.nexters.goalpanzi.presentation.device.dto.UpdatePushActivationStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "device")
public interface DeviceControllerDocs {

    @Operation(summary = "디바이스 토큰 갱신")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404", description = "Not Found - 정보에 해당하는 디바이스가 존재하지 않음"),
    })
    ResponseEntity<Void> updateDeviceToken(
            @Parameter(in = ParameterIn.HEADER, hidden = true) @LoginMemberId final Long memberId,
            @RequestBody @Valid final UpdateDeviceTokenRequest request
    );

    @Operation(summary = "푸시 알림 활성화 상태 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404", description = "Not Found - 정보에 해당하는 디바이스가 존재하지 않음"),
    })
    ResponseEntity<Void> updatePushActivationStatus(
            @Parameter(in = ParameterIn.HEADER, hidden = true) @LoginMemberId final Long memberId,
            @RequestBody @Valid final UpdatePushActivationStatusRequest request
    );
}
