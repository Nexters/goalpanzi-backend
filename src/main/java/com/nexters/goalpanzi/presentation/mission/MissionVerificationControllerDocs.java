package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.MissionVerificationResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionVerificationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name = "미션 인증",
        description = """
                미션 인증과 관련된 그룹입니다.
                                
                미션 인증을 위한 사진을 업로드하고 조회합니다.
                """
)
public interface MissionVerificationControllerDocs {

    @Operation(summary = "오늘 미션 인증 현황 조회", description = "오늘 미션 인증 현황을 조회합니다.")
    @ApiResponse(responseCode = "200")
    @GetMapping
    ResponseEntity<List<MissionVerificationResponse>> getTodayVerification(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Schema(description = "미션 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "missionId") final Long missionId
    );

    @Operation(summary = "나의 미션 인증 현황 조회", description = "보드판에 해당하는 미션 인증 현황을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found - 정보에 해당하는 이미지가 존재하지 않음", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/me/{number}")
    ResponseEntity<MissionVerificationResponse> getMyVerification(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Schema(description = "미션 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "missionId") final Long missionId,
            @Schema(description = "보드판 번호", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "number") final Integer number);

    @Operation(summary = "미션 인증", description = "미션 인증을 위해 이미지를 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request - 이미 완료한 미션이거나 오늘 인증을 마쳤음"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404", description = "Not Found - 정보에 해당하는 미션이 존재하지 않음"),
    })
    @PostMapping("/me")
    ResponseEntity<Void> createVerification(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Schema(description = "미션 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "missionId") final Long missionId,
            @RequestBody @Valid final CreateMissionVerificationRequest request);
}