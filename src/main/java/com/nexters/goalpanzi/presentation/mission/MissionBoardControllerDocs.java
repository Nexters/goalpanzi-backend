package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionBoardQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionBoardsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;
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
    @Operation(
            summary = "미션 보드판 조회",
            description = """
                    해당 미션의 보드판 현황을 조회합니다.
                                        
                    보드칸에 대한 정보를 포함하며, missionBoards 배열의 크기는 **[보드칸의 개수 + 1]**만큼 입니다.
                                        
                    0번째 보드칸은 시작점으로, 아직 미션을 시작하지 않은 상태를 나타냅니다.
                                        
                    sortType 과 sortDirection 에 따라 보드칸의 장기말을 정렬합니다.
                                        
                    sortType, sortDirection 생략 시, **[순위순]**으로 조회합니다.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{missionId}/board")
    ResponseEntity<MissionBoardsResponse> getBoard(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Schema(description = "미션 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "missionId") final Long missionId,
            @Schema(description = "미션 보드칸 장기말 정렬 기준 (생략 시, 보드칸 도착 순서로 간주)", allowableValues = {"RANK", "RANDOM"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            @RequestParam(name = "sortType", required = false, defaultValue = "RANK") final MissionBoardQuery.SortType sortType,
            @Schema(description = "미션 보드칸 장기말 정렬 방향 (생략 시, 오름차순으로 간주)", allowableValues = {"ASC", "DESC"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") final Sort.Direction direction
    );
}
