package com.nexters.goalpanzi.domain.member;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    private static final String MEMBER_KEY_PREFIX = "ME_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "alt_key", nullable = false)
    private String altKey;

    @Column(name = "social_id", nullable = false)
    private String socialId;

    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "character_type")
    private String characterType;

    private Member(String socialId, String email, String altKey) {
        this.email = email;
    }

    public static Member init(String socialId, String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("사용자 정보가 올바르지 않습니다.");
        }
        String altKey = generateKey();

        return new Member(socialId, email, altKey);
    }

    private static String generateKey() {
        return MEMBER_KEY_PREFIX + UUID.randomUUID();
    }
}
