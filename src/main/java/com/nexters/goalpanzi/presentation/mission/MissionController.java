package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionService;
import com.nexters.goalpanzi.application.mission.dto.CreateMissionRequest;
import com.nexters.goalpanzi.application.mission.dto.MissionResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
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

    @PostMapping
    @Override
    public ResponseEntity<MissionResponse> createMission(
            @LoginMemberId final Long memberId,
            @Valid @RequestBody final CreateMissionRequest request
    ) {
        MissionResponse response = missionService.createMission(memberId, request);

        return ResponseEntity.ok(response);
    }
}
