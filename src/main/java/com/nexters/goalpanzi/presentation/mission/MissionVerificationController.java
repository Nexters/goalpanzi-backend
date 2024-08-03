package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationCommand;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionVerificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/missions")
@RestController
public class MissionVerificationController implements MissionVerificationControllerDocs {

    private final MissionVerificationService missionVerificationService;

    @GetMapping
    public ResponseEntity<List<MissionVerificationResponse>> getVerifications(
            @LoginMemberId final Long memberId,
            @PathVariable(name = "missionId") final Long missionId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date
    ) {
        List<MissionVerificationResponse> response = missionVerificationService.getVerifications(new MissionVerificationCommand(memberId, missionId, date));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{missionId}/verifications/me/{number}")
    public ResponseEntity<MissionVerificationResponse> getMyVerification(
            @LoginMemberId final Long memberId,
            @PathVariable(name = "missionId") final Long missionId,
            @PathVariable(name = "number") final Integer number) {
        MissionVerificationResponse response = missionVerificationService.getMyVerification(
                new MyMissionVerificationCommand(memberId, missionId, number));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{missionId}/verifications/me")
    public ResponseEntity<Void> createVerification(
            @LoginMemberId final Long memberId,
            @PathVariable(name = "missionId") final Long missionId,
            @RequestBody @Valid final CreateMissionVerificationRequest request) {
        missionVerificationService.createVerification(request.toServiceDto(memberId, missionId));

        return ResponseEntity.ok().build();
    }
}
