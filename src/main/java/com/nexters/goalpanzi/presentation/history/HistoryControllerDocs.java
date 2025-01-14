package com.nexters.goalpanzi.presentation.history;

import com.nexters.goalpanzi.application.history.dto.response.HistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "내 기록 (아카이빙)")
public interface HistoryControllerDocs {

    @Operation(summary = "내 완료 미션 목록 조회")
    ResponseEntity<HistoryResponse.CompletedMissionWrapper> getMyMissionHistories(
            @Parameter(in = ParameterIn.HEADER, hidden = true)
            final Long memberId,
            @Schema(description = "페이지 번호 (default:0)")
            final Integer page,
            @Schema(description = "페이지 사이즈 (default:10)")
            final Integer pageSize
    );
}
