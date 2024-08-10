package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.domain.mission.BoardOrderBy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(
        name = "미션 보드",
        description = """
                미션 보드와 관련된 그룹입니다.
                                
                미션 보드판 정보을 제공합니다.
                """
)
public interface MissionBoardControllerDocs {
    @Operation(summary = "미션 보드판 조회", description = "해당 미션의 보드판 현황을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{missionId}/board")
    ResponseEntity<MissionBoardsResponse> getBoard(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Schema(description = "미션 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "missionId") final Long missionId,
            @Schema(description = "미션 보드칸 장기말 정렬 기준", allowableValues = {"CREATED_AT"}, requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam(name = "orderBy", required = false) final BoardOrderBy orderBy
    );
}
