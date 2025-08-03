package com.seoulfit.backend.user.application.port.in;

import com.seoulfit.backend.user.application.port.in.dto.*;
import com.seoulfit.backend.user.domain.AuthProvider;

/**
 * 사용자 인증 Use Case
 * <p>
 * 헥사고날 아키텍처의 입력 포트
 * 사용자 인증과 관련된 모든 비즈니스 로직을 정의
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface AuthenticateUserUseCase {

    /**
     * 일반 로그인
     *
     * @param command 로그인 명령
     * @return 토큰 결과
     */
    TokenResult login(LoginCommand command);

    /**
     * 일반 회원가입
     *
     * @param command 회원가입 명령
     * @return 토큰 결과
     */
    TokenResult signUp(SignUpCommand command);

    /**
     * OAuth 로그인
     *
     * @param command OAuth 로그인 명령
     * @return 토큰 결과
     */
    TokenResult oauthLogin(OAuthLoginCommand command);

    /**
     * OAuth 회원가입
     *
     * @param command OAuth 회원가입 명령
     * @return 토큰 결과
     */
    TokenResult oauthSignUp(OAuthSignUpCommand command);

    /**
     * OAuth 사용자 확인
     *
     * @param command OAuth 사용자 확인 명령
     * @return OAuth 사용자 확인 결과
     */
    OAuthUserCheckResult checkOAuthUser(OAuthUserCheckCommand command);

    /**
     * 토큰 갱신
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 토큰 결과
     */
    TokenResult refreshToken(String refreshToken);

    /**
     * 사용자 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
}
