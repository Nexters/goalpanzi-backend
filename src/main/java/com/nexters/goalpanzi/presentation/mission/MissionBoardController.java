package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionBoardService;
import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/missions")
@RestController
public class MissionBoardController implements MissionBoardControllerDocs {

    private final MissionBoardService missionBoardService;

    @GetMapping("/{missionId}/board")
    public ResponseEntity<MissionBoardsResponse> getBoard(
            @PathVariable(name = "missionId") final Long missionId
    ) {
        MissionBoardsResponse response = missionBoardService.getBoard(new MissionBoardQuery(missionId));

        return ResponseEntity.ok(response);
    }
}
