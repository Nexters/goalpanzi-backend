package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionBoardService;
import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
            @RequestParam(name = "sortType", required = false) final MissionBoardQuery.SortType sortType,
            @Schema(description = "미션 보드칸 장기말 정렬 방향", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam(name = "sortDirection", required = false) final Sort.Direction direction
    ) {
        MissionBoardsResponse response = missionBoardService.getBoard(new MissionBoardQuery(memberId, missionId, sortType, direction));

        return ResponseEntity.ok(response);
    }
}
