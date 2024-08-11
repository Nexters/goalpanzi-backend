package com.nexters.goalpanzi.domain.member;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;

@Entity
@SQLRestriction("deleted_at is NULL")
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "social_id", unique = true)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "character_type")
    private CharacterType characterType;

    private Member(final String socialId, final String email, final SocialType socialType) {
        this.socialId = socialId;
        this.email = email;
        this.socialType = socialType;
    }

    public static Member socialLogin(final String socialId, final String email, final SocialType socialType) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("사용자 정보가 올바르지 않습니다.");
        }

        return new Member(socialId, email, socialType);
    }

    public Boolean isProfileSet() {
        return (characterType != null) && (nickname != null);
    }

    public void updateProfile(final String nickname, final CharacterType characterType) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (characterType != null) {
            this.characterType = characterType;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member that = (Member) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, socialId, socialType, nickname, characterType);
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", socialId='" + socialId + '\'' +
                ", socialType=" + socialType +
                ", nickname='" + nickname + '\'' +
                ", characterType=" + characterType +
                '}';
    }
}
