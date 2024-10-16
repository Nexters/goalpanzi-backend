package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionMemberService;
import com.nexters.goalpanzi.application.mission.dto.request.MissionFilter;
import com.nexters.goalpanzi.application.mission.dto.response.MemberRankResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.presentation.mission.dto.JoinMissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MissionMemberController implements MissionMemberControllerDocs {

    private final MissionMemberService missionMemberService;

    @Override
    @GetMapping("/mission-members/me")
    public ResponseEntity<MissionsResponse> getMissions(
            @LoginMemberId final Long memberId,
            @RequestParam(required = false, defaultValue = "PENDING,ONGOING") List<MissionFilter> filter
    ) {
        return ResponseEntity.ok(missionMemberService.findAllByMemberId(memberId, filter));
    }

    @PostMapping("/mission-members")
    public ResponseEntity<Void> joinMission(
            @LoginMemberId final Long memberId,
            @RequestBody final JoinMissionRequest request
    ) {
        missionMemberService.joinMission(memberId, new InvitationCode(request.invitationCode()));

        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/mission-members/rank")
    public ResponseEntity<MemberRankResponse> getMissionRank(
            @RequestParam final Long missionId,
            @LoginMemberId final Long memberId
    ) {
        MemberRankResponse response = missionMemberService.getMissionRank(missionId, memberId);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/mission:joinable")
    public ResponseEntity<MissionDetailResponse> getJoinableMission(
            @RequestParam final String invitationCode
    ) {
        MissionDetailResponse response = missionMemberService.getJoinableMission(new InvitationCode(invitationCode));

        return ResponseEntity.ok(response);
    }
}
