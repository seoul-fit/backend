package com.seoulfit.backend.user.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 회원가입 명령
 * <p>
 * 헥사고날 아키텍처의 Command 객체
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
public class SignUpCommand {

    private final String email;
    private final String password;
    private final String nickname;

    /**
     * 회원가입 명령 생성
     *
     * @param email    이메일
     * @param password 비밀번호
     * @param nickname 닉네임
     * @return 회원가입 명령
     */
    public static SignUpCommand of(String email, String password, String nickname) {
        return SignUpCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
