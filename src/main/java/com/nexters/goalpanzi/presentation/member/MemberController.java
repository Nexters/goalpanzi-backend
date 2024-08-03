package com.nexters.goalpanzi.presentation.member;

import com.nexters.goalpanzi.application.member.MemberService;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @Override
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @LoginMemberId final Long memberId,
            @RequestBody @Valid final UpdateProfileRequest request
    ) {
        memberService.updateProfile(request.toServiceDto(memberId));

        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> deleteMember(
            @LoginMemberId final Long memberId
    ) {
        memberService.deleteMember(memberId);

        return ResponseEntity.noContent().build();
    }
}
