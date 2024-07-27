package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.MissionVerificationUploadRequest;
import com.nexters.goalpanzi.common.argumentresolver.LoginUserId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/missions/{missionId}/verifications")
public class MissionVerificationController implements MissionVerificationControllerDocs {

    private MissionVerificationService missionVerificationService;

    @GetMapping("/{number}")
    public ResponseEntity<MissionVerificationResponse> getVerificationImage(
            @LoginUserId final Long userId,
            @PathVariable(name = "missionId") final Long missionId,
            @PathVariable(name = "number") final Integer number) {
        MissionVerificationResponse response = missionVerificationService.getVerificationImage(userId, missionId, number);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> uploadVerificationImage(
            @LoginUserId final Long userId,
            @PathVariable(name = "missionId") final Long missionId,
            @RequestBody @Valid MissionVerificationUploadRequest uploadRequest) {
        missionVerificationService.uploadVerificationImage(userId, missionId, uploadRequest);

        return ResponseEntity.ok().build();
    }
}
