package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionMemberService;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.JoinMissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mission-members")
public class MissionMemberController implements MissionMemberControllerDocs {

    private final MissionMemberService missionMemberService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<MissionsResponse> getMissions(@LoginMemberId final Long memberId) {
        return ResponseEntity.ok(missionMemberService.findAllByMemberId(memberId));
    }

    @PostMapping
    public ResponseEntity<Void> joinMission(
            @LoginMemberId final Long memberId,
            @RequestBody JoinMissionRequest request
    ) {
        missionMemberService.joinMission(memberId, request.missionCode());

        return ResponseEntity.ok().build();
    }
}
