package com.nexters.goalpanzi.presentation.member;

import com.nexters.goalpanzi.application.member.MemberService;
import com.nexters.goalpanzi.application.member.dto.response.ProfileResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import com.nexters.goalpanzi.presentation.member.dto.UpdateDeviceTokenRequest;
import com.nexters.goalpanzi.presentation.member.dto.UpdateProfileRequest;
import com.nexters.goalpanzi.presentation.member.dto.UpdatePushActivationStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @Override
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(
            @LoginMemberId final Long memberId
    ) {
        ProfileResponse response = memberService.getMember(memberId);

        return ResponseEntity.ok(response);
    }

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

    @Override
    @PatchMapping("/device-token")
    public ResponseEntity<Void> updateDeviceToken(
            @LoginMemberId final Long memberId,
            @RequestBody @Valid final UpdateDeviceTokenRequest request
    ) {
        memberService.updateDeviceToken(request.toServiceDto(memberId));

        return ResponseEntity.ok().build();
    }

    @Override
    @PatchMapping("/push-activation-status")
    public ResponseEntity<Void> updatePushActivationStatus(
            @LoginMemberId final Long memberId,
            @RequestBody @Valid final UpdatePushActivationStatusRequest request
    ) {
        memberService.updatePushActivationStatus(request.toServiceDto(memberId));

        return ResponseEntity.ok().build();
    }
}
