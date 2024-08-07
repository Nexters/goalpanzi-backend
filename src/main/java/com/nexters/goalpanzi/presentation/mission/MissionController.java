package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionService;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Override
    @GetMapping("/{missionId}")
    public ResponseEntity<MissionDetailResponse> getMission(@PathVariable final Long missionId) {
        MissionDetailResponse response = missionService.getMission(missionId);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{missionId}")
    public ResponseEntity<MissionDetailResponse> deleteMission(
            @PathVariable final Long missionId,
            @LoginMemberId final Long memberId
    ) {
        missionService.deleteMission(memberId, missionId);

        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping
    public ResponseEntity<MissionDetailResponse> getMissionByInvitationCode(
            @RequestParam final String invitationCode) {
        MissionDetailResponse response = missionService.getMissionByInvitationCode(new InvitationCode(invitationCode));

        return ResponseEntity.ok(response);
    }
}
