package com.nexters.goalpanzi.presentation.member;

import com.nexters.goalpanzi.application.member.dto.response.ProfileResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "회원")
public interface MemberControllerDocs {

    @Operation(summary = "프로필 조회", description = "캐릭터, 닉네임을 조회합니다.")
    ResponseEntity<ProfileResponse> getProfile(
            @Parameter(in = ParameterIn.HEADER, hidden = true) @LoginMemberId final Long userId
    );

    @Operation(summary = "프로필 설정", description = "캐릭터, 닉네임을 설정합니다.")
    ResponseEntity<Void> updateProfile(
            @Parameter(in = ParameterIn.HEADER, hidden = true) @LoginMemberId final Long userId,
            @RequestBody @Valid final UpdateProfileRequest request
    );

    @Operation(summary = "회원 탈퇴")
    ResponseEntity<Void> deleteMember(
            @Parameter(in = ParameterIn.HEADER, hidden = true) @LoginMemberId final Long memberId
    );
}
