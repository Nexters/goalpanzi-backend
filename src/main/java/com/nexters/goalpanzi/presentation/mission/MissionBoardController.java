package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.MissionBoardService;
import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/missions")
@RestController
public class MissionBoardController implements MissionBoardControllerDocs {

    private final MissionBoardService missionBoardService;

    @GetMapping("/{missionId}/board")
    public ResponseEntity<List<MissionBoardResponse>> getBoard(
            @PathVariable(name = "missionId") final Long missionId
    ) {
        List<MissionBoardResponse> response = missionBoardService.getBoard(new MissionBoardQuery(missionId));

        return ResponseEntity.ok(response);
    }
}
