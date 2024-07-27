package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.MissionDto;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "미션")
public interface MissionControllerDocs {

    ResponseEntity<MissionDto.MissionResponse> createMission(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Valid @RequestBody final CreateMissionRequest request
    );
}
