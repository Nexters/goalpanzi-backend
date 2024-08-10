package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.domain.mission.VerificationOrderBy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Tag(
        name = "미션 인증",
        description = """
                미션 인증과 관련된 그룹입니다.
                                
                미션 인증을 위한 사진을 업로드하고 조회합니다.
                """
)
public interface MissionVerificationControllerDocs {

    @Operation(summary = "미션 인증 현황 조회", description = "해당 일자의 미션 인증 현황을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{missionId}/verifications")
    ResponseEntity<MissionVerificationsResponse> getVerifications(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Schema(description = "미션 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "missionId") final Long missionId,
            @Schema(description = "미션 인증 일자", type = "string", format = "date", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date,
            @Schema(description = "미션 인증 현황 정렬 기준", allowableValues = {"CREATED_AT_DESC"}, requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam(name = "orderBy", required = false) final VerificationOrderBy orderBy
    );

    @Operation(summary = "나의 미션 인증 현황 조회", description = "보드판에 해당하는 미션 인증 현황을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Not Found - 정보에 해당하는 이미지가 존재하지 않음", content = @Content(schema = @Schema(hidden = true))),
    })
    @GetMapping("/{missionId}/verifications/me/{number}")
    ResponseEntity<MissionVerificationResponse> getMyVerification(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Schema(description = "미션 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "missionId") final Long missionId,
            @Schema(description = "보드칸 번호", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "number") final Integer number);

    @Operation(summary = "미션 인증", description = "미션 인증을 위해 이미지를 업로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request - 이미 완료한 미션이거나 오늘 인증을 마쳤음"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "404", description = "Not Found - 정보에 해당하는 미션이 존재하지 않음"),
    })
    @PostMapping(value = "/{missionId}/verifications/me")
    ResponseEntity<Void> createVerification(
            @Parameter(hidden = true) @LoginMemberId final Long memberId,
            @Schema(description = "미션 아이디", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
            @PathVariable(name = "missionId") final Long missionId,
            @Schema(description = "인증 이미지 파일", requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestPart(name = "imageFile") final MultipartFile imageFile);
}