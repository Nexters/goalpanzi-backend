package com.nexters.goalpanzi.domain.member.repository;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialId(final String socialId);

    Optional<Member> findByNickname(final String nickname);

    @Query(value = "select m.* from member m where m.social_id = :socialId and m.deleted_at is not null", nativeQuery = true)
    Optional<Member> findBySocialIdAndDeletedAtIsNotNull(final String socialId);

    default Member getMember(final Long memberId) {
        return findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER, memberId));
    }
}
