package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.ViewMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.ViewMissionVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RequiredArgsConstructor
@RequestMapping("/api/missions")
@RestController
public class MissionVerificationController implements MissionVerificationControllerDocs {

    private final MissionVerificationService missionVerificationService;

    @GetMapping("/{missionId}/verifications")
    public ResponseEntity<MissionVerificationsResponse> getVerifications(
            @LoginMemberId final Long memberId,
            @PathVariable(name = "missionId") final Long missionId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date,
            @RequestParam(name = "sortType", required = false, defaultValue = "VERIFIED_AT") final MissionVerificationQuery.SortType sortType,
            @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC") final Sort.Direction direction
    ) {
        MissionVerificationsResponse response = missionVerificationService.getVerifications(new MissionVerificationQuery(memberId, missionId, date, sortType, direction));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{missionId}/verifications/me/{number}")
    public ResponseEntity<MissionVerificationResponse> getMyVerification(
            @LoginMemberId final Long memberId,
            @PathVariable(name = "missionId") final Long missionId,
            @PathVariable(name = "number") final Integer number) {
        MissionVerificationResponse response = missionVerificationService.getMyVerification(
                new MyMissionVerificationQuery(memberId, missionId, number));

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{missionId}/verifications/me")
    public ResponseEntity<Void> createVerification(
            @LoginMemberId final Long memberId,
            @PathVariable(name = "missionId") final Long missionId,
            @RequestPart(name = "imageFile") final MultipartFile imageFile) {
        missionVerificationService.createVerification(new CreateMissionVerificationCommand(memberId, missionId, imageFile));

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/verifications/view")
    public ResponseEntity<MissionVerificationResponse> viewMissionVerification(
            @RequestBody final ViewMissionVerificationRequest request,
            @LoginMemberId final Long memberId
    ) {
        missionVerificationService.viewMissionVerification(
                new ViewMissionVerificationCommand(request.missionVerificationId(), memberId));

        return ResponseEntity.ok().build();
    }
}
