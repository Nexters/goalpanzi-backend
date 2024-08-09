package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "미션")
public interface MissionControllerDocs {

    @Operation(summary = "미션 생성", description = "미션을 생성합니다.")
    ResponseEntity<MissionDetailResponse> createMission(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Valid @RequestBody final CreateMissionRequest request
    );

    @Operation(summary = "미션 조회", description = "미션정보를 조회합니다.")
    ResponseEntity<MissionDetailResponse> getMission(
            @PathVariable("/{missionId}") Long missionId
    );

    @Operation(summary = "미션 삭제", description = "미션을 삭제합니다.")
    ResponseEntity<MissionDetailResponse> deleteMission(
            @PathVariable("/{missionId}") Long missionId,
            @Parameter(in = ParameterIn.HEADER, hidden = true) @LoginMemberId final Long memberId
    );

    @Operation(summary = "초대코드로 미션 조회", description = "초대코드로 미션정보를 조회합니다.")
    ResponseEntity<MissionDetailResponse> getMissionByInvitationCode(
            @RequestParam String invitationCode
    );

}
