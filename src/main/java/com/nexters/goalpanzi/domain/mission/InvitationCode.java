package com.nexters.goalpanzi.domain.mission;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Random;

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
            throw new IllegalArgumentException("Invitation code is invalid");
        }
    }
}
