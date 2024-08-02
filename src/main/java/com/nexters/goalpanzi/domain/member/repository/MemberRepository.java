package com.nexters.goalpanzi.domain.member.repository;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.exception.BaseException;
import com.nexters.goalpanzi.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialId(String socialId);

    Optional<Member> findByNickname(String nickname);

    default Member getMember(Long memberId) {
        return findById(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
