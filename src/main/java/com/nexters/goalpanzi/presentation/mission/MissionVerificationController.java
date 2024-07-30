package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.MyMissionVerificationCommand;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionVerificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/missions/{missionId}/verifications")
@RestController
public class MissionVerificationController implements MissionVerificationControllerDocs {

    private final MissionVerificationService missionVerificationService;

    @GetMapping
    public ResponseEntity<List<MissionVerificationResponse>> getTodayVerification(
            @LoginMemberId final Long memberId,
            @PathVariable final Long missionId
    ) {
        List<MissionVerificationResponse> response = missionVerificationService.getTodayVerification(new MissionVerificationCommand(memberId, missionId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/{number}")
    public ResponseEntity<MissionVerificationResponse> getMyVerification(
            @LoginMemberId final Long memberId,
            @PathVariable final Long missionId,
            @PathVariable final Integer number) {
        MissionVerificationResponse response = missionVerificationService.getMyVerification(
                new MyMissionVerificationCommand(memberId, missionId, number));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/me")
    public ResponseEntity<Void> createVerification(
            @LoginMemberId final Long memberId,
            @PathVariable final Long missionId,
            @RequestBody @Valid final CreateMissionVerificationRequest request) {
        missionVerificationService.createVerification(request.toServiceDto(memberId, missionId));

        return ResponseEntity.ok().build();
    }
}
