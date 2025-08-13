package com.seoulfit.backend.user.application.port.in;

import com.seoulfit.backend.user.application.port.in.dto.*;
import com.seoulfit.backend.user.domain.AuthProvider;

/**
 * 사용자 인증 Use Case
 * <p>
 * 헥사고날 아키텍처의 입력 포트
 * 사용자 인증과 관련된 모든 비즈니스 로직을 정의
 * Authorization Code Flow를 지원하도록 업데이트
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface AuthenticateUserUseCase {

    /**
     * OAuth 로그인 (Authorization Code Flow)
     *
     * @param command OAuth 로그인 명령
     * @return 토큰 결과
     */
    TokenResult oauthLogin(OAuthLoginCommand command);

    /**
     * OAuth 권한부여 승인코드 기반 로그인
     *
     * @param command OAuth 권한부여 명령
     * @return 토큰 결과
     */
    TokenResult oauthLoginWithAuthorizationCode(OAuthAuthorizationCommand command);

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
     * OAuth 인가코드 검증
     * 인가코드로 OAuth 제공자에서 사용자 정보를 조회
     *
     * @param command OAuth 인가코드 검증 명령
     * @return OAuth 인가코드 검증 결과
     */
    OAuthAuthorizeCheckResult checkAuthorizationCode(OAuthAuthorizeCheckCommand command);

}
