package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionService;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/missions")
@RestController
public class MissionController implements MissionControllerDocs {

    private final MissionService missionService;

    @Override
    @PostMapping
    public ResponseEntity<MissionDetailResponse> createMission(
            @LoginMemberId final Long memberId,
            @Valid @RequestBody final CreateMissionRequest request
    ) {
        MissionDetailResponse response = missionService.createMission(request.toServiceDto(memberId));

        return ResponseEntity.ok(response);
    }
}
