package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionBoardService;
import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.domain.mission.BoardOrderBy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/missions")
@RestController
public class MissionBoardController implements MissionBoardControllerDocs {

    private final MissionBoardService missionBoardService;

    @GetMapping("/{missionId}/board")
    public ResponseEntity<MissionBoardsResponse> getBoard(
            @LoginMemberId final Long memberId,
            @PathVariable(name = "missionId") final Long missionId,
            @RequestParam(name = "orderBy") final BoardOrderBy orderBy
    ) {
        MissionBoardsResponse response = missionBoardService.getBoard(new MissionBoardQuery(memberId, missionId, orderBy));

        return ResponseEntity.ok(response);
    }
}
