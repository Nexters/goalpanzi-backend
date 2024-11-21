package com.nexters.goalpanzi.application.member;

import com.nexters.goalpanzi.application.member.dto.request.UpdateDeviceTokenCommand;
import com.nexters.goalpanzi.application.member.dto.request.UpdateProfileCommand;
import com.nexters.goalpanzi.application.member.dto.request.UpdatePushActivationStatusCommand;
import com.nexters.goalpanzi.application.member.dto.response.ProfileResponse;
import com.nexters.goalpanzi.application.member.event.DeleteMemberEvent;
import com.nexters.goalpanzi.application.member.event.UpdateDeviceTokenEvent;
import com.nexters.goalpanzi.application.member.event.UpdatePushActivationStatusEvent;
import com.nexters.goalpanzi.domain.auth.repository.RefreshTokenRepository;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.exception.AlreadyExistsException;
import com.nexters.goalpanzi.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public ProfileResponse getMember(final Long memberId) {
        Member member = memberRepository.getMember(memberId);

        return new ProfileResponse(member.getNickname(), member.getCharacterType());
    }

    @Transactional
    public void updateProfile(final UpdateProfileCommand request) {
        request.nickname().ifPresent(this::validateNickname);

        Member member = memberRepository.getMember(request.memberId());

        request.nickname().ifPresent(member::updateNickname);
        request.characterType().ifPresent(member::updateCharacterType);
    }

    private void validateNickname(final String nickname) {
        memberRepository.findByNicknameAndDeletedAtIsNull(nickname)
                .ifPresent(member -> {
                    throw new AlreadyExistsException(ErrorCode.ALREADY_EXIST_NICKNAME, nickname);
                });
    }

    @Transactional
    public void deleteMember(final Long memberId) {
        eventPublisher.publishEvent(new DeleteMemberEvent(memberId));
        refreshTokenRepository.delete(memberId.toString());
        memberRepository.getMember(memberId).delete();
    }

    @Transactional
    public void updateDeviceToken(final UpdateDeviceTokenCommand command) {
        Member member = memberRepository.getMember(command.memberId());
        String deprecatedDeviceToken = member.getDeviceToken();

        member.updateDeviceToken(command.deviceToken());
        member.updatePushActivationStatus(true);
        eventPublisher.publishEvent(
                new UpdateDeviceTokenEvent(command.memberId(), deprecatedDeviceToken, command.deviceToken())
        );
    }

    @Transactional
    public void updatePushActivationStatus(final UpdatePushActivationStatusCommand command) {
        Member member = memberRepository.getMember(command.memberId());

        member.updatePushActivationStatus(command.pushActivationStatus());
        eventPublisher.publishEvent(
                new UpdatePushActivationStatusEvent(command.memberId(), member.getDeviceToken(), command.pushActivationStatus())
        );
    }
}
