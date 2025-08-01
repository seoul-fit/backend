package com.seoulfit.backend.user.application.port.in.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 명령
 * <p>
 * 헥사고날 아키텍처의 Command 객체
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Getter
@Builder
public class LoginCommand {

    private final String email;
    private final String password;

    /**
     * 로그인 명령 생성
     *
     * @param email    이메일
     * @param password 비밀번호
     * @return 로그인 명령
     */
    public static LoginCommand of(String email, String password) {
        return LoginCommand.builder()
                .email(email)
                .password(password)
                .build();
    }
}
