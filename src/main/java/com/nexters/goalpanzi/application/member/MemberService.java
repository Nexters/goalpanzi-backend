package com.nexters.goalpanzi.application.member;

import com.nexters.goalpanzi.application.member.dto.request.UpdateProfileCommand;
import com.nexters.goalpanzi.application.member.event.DeleteMemberEvent;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void updateProfile(final UpdateProfileCommand request) {
        validateNickname(request.nickname());
        Member member = memberRepository.getMember(request.memberId());

        member.updateProfile(request.nickname(), request.characterType());
    }

    private void validateNickname(final String nickname) {
        memberRepository.findByNickname(nickname)
                .ifPresent(member -> {
                    throw new AlreadyExistsException(ErrorCode.ALREADY_EXIST_NICKNAME, nickname);
                });
    }

    @Transactional
    public void deleteMember(final Long memberId) {
        eventPublisher.publishEvent(new DeleteMemberEvent(memberId));
        memberRepository.getMember(memberId).delete();
    }
}
