package com.nexters.goalpanzi.domain.mission;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Random;

import static com.nexters.goalpanzi.exception.ErrorCode.INVALID_INVITATION_CODE;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvitationCode {

    private static final String CODE_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 4;
    private static final Random RANDOM = new Random();

    @Column(name = "invitation_code", nullable = false)
    private String code;

    public InvitationCode(final String code) {
        this.code = code;
        validate();
    }

    public static InvitationCode generate() {
        return new InvitationCode(generateRandomCode());
    }

    private static String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CODE_CHARACTERS.length());
            sb.append(CODE_CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    private void validate() {
        if (!StringUtils.hasText(code) || this.code.length() != CODE_LENGTH) {
            throw new IllegalArgumentException(INVALID_INVITATION_CODE.getMessage());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvitationCode code1 = (InvitationCode) o;
        return Objects.equals(code, code1.code);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(code);
    }

    @Override
    public String toString() {
        return "InvitationCode{" +
                "code='" + code + '\'' +
                '}';
    }
}
