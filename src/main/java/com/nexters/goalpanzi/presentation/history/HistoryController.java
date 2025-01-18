package com.nexters.goalpanzi.presentation.history;

import com.nexters.goalpanzi.application.history.HistoryService;
import com.nexters.goalpanzi.application.history.dto.response.HistoryResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HistoryController implements HistoryControllerDocs {

    private final HistoryService historyService;

    @Override
    @GetMapping("/api/missions/history")
    public ResponseEntity<HistoryResponse.CompletedMissionWrapper> getMyMissionHistories(
            @LoginMemberId final Long memberId,
            @RequestParam(defaultValue = "0") final Integer page,
            @RequestParam(defaultValue = "30") final Integer pageSize
    ) {
        var result = historyService.getMissionHistories(memberId, PageRequest.of(page, pageSize));

        return ResponseEntity.ok(result);
    }
}
