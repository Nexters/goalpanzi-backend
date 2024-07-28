package com.nexters.goalpanzi.application.member;

import com.nexters.goalpanzi.application.member.dto.UpdateProfileCommand;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.exception.AlreadyExistsException;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void updateProfile(final UpdateProfileCommand request) {
        validateNickname(request.nickname());
        Member member = getMember(request.memberId());

        member.updateProfile(request.nickname(), request.characterType());
    }

    private Member getMember(final Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }

    private void validateNickname(final String nickname) {
        memberRepository.findByNickname(nickname)
                .ifPresent(member -> {
                    throw new AlreadyExistsException(ErrorCode.ALREADY_EXIST_NICKNAME);
                });
    }
}
