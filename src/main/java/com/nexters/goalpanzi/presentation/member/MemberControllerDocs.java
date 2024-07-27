package com.nexters.goalpanzi.presentation.member;

import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "회원")
public interface MemberControllerDocs {

    @Operation(summary = "프로필 생성", description = "캐릭터, 닉네임을 설정합니다.")
    @PatchMapping("/profile")
    ResponseEntity<Void> updateProfile(
            @Parameter(in = ParameterIn.HEADER, hidden = true) @LoginMemberId final Long userId,
            @RequestBody @Valid final UpdateProfileRequest request
    );
}
