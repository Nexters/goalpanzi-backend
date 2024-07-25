package com.nexters.goalpanzi.presentation.member;

import com.nexters.goalpanzi.application.member.dto.ProfileRequest;
import com.nexters.goalpanzi.common.argumentresolver.LoginUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberControllerDocs {

    @Operation(summary = "프로필 생성", description = "캐릭터, 닉네임을 생성합니다.")
    @PatchMapping("/profile")
    ResponseEntity<Void> updateProfile(
            @Parameter(in = ParameterIn.HEADER, hidden = true) @LoginUserId final Long userId,
            @RequestBody @Valid final ProfileRequest request
    );
}
