package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.MissionDetailResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "미션")
public interface MissionControllerDocs {

    @Operation(summary = "미션 생성", description = "미션을 생성합니다.")
    ResponseEntity<MissionDetailResponse> createMission(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Valid @RequestBody final CreateMissionRequest request
    );
}
