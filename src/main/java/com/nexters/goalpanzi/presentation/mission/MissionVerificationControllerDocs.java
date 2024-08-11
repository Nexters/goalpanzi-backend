package com.nexters.goalpanzi.presentation.mission;

import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;
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

    @Operation(
            summary = "미션 인증 현황 조회",
            description = """
                    해당 일자의 미션 인증 현황을 조회합니다.
                                        
                    0번째 항목은 나의 미션 인증 현황을 의미합니다.
                                        
                    orderBy 에 따라 인증 현황을 정렬하며, 미션을 인증하지 않은 멤버는 프로필 정보만 포함하여 마지막에 배치됩니다.
                    """
    )
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
            @Schema(description = "미션 인증 현황 정렬 기준", allowableValues = {"CREATED_AT_DESC", "CREATED_AT"}, requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam(name = "sortType", required = false) final MissionVerificationQuery.SortType sortType,
            @Schema(description = "미션 인증 현황 정렬 방향", allowableValues = {"ASC", "DESC"}, requiredMode = Schema.RequiredMode.REQUIRED)
            @RequestParam(name = "sortDirection", required = false) final Sort.Direction direction
    );

    @Operation(summary = "나의 미션 인증 현황 조회", description = "보드칸에 해당하는 나의 미션 인증 현황을 조회합니다.")
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
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Bad Request
                                                        
                            - 이미 완료한 미션인 경우 (= 보드 완주 성공)
                            - 오늘 인증을 마친 경우 (= 중복 인증)
                            - 인증 기간이 아닌 경우 (= 아직 미션이 시작되지 않았거나 끝난 경우)
                            - 인증 요일이 아닌 경우
                            - 인증 시간대가 아닌 경우
                            """
            ),
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