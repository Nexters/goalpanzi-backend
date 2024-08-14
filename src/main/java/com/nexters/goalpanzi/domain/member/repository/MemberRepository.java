package com.nexters.goalpanzi.domain.member.repository;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialIdAndDeletedAtIsNull(final String socialId);

    Optional<Member> findByNicknameAndDeletedAtIsNull(final String nickname);

    Optional<Member> findBySocialIdAndDeletedAtIsNotNull(final String socialId);

    default Member getMember(final Long memberId) {
        return findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER, memberId));
    }
}
