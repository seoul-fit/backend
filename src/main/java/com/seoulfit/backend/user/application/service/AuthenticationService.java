package com.seoulfit.backend.user.application.service;

import com.seoulfit.backend.user.application.port.in.AuthenticateUserUseCase;
import com.seoulfit.backend.user.application.port.in.dto.*;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import com.seoulfit.backend.user.domain.exception.OAuthUserAlreadyExistsException;
import com.seoulfit.backend.user.domain.exception.OAuthUserNotFoundException;
import com.seoulfit.backend.user.infrastructure.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 통합 인증 서비스
 * <p>
 * 헥사고날 아키텍처의 애플리케이션 서비스
 * 모든 인증 관련 비즈니스 로직을 통합 관리
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService implements AuthenticateUserUseCase {

    private final UserPort userPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public TokenResult signUp(SignUpCommand command) {
        log.info("일반 회원가입 시작: email={}", command.getEmail());

        // 이메일 중복 확인
        if (userPort.existsByEmail(command.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + command.getEmail());
        }

        // 사용자 생성
        User user = User.builder()
                .email(command.getEmail())
                .password(passwordEncoder.encode(command.getPassword()))
                .nickname(command.getNickname())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userPort.save(user);
        log.info("일반 회원가입 완료: userId={}", savedUser.getId());

        // 토큰 생성
        return createTokenResult(savedUser);
    }

    @Override
    public TokenResult login(LoginCommand command) {
        log.info("일반 로그인 시도: email={}", command.getEmail());

        User user = userPort.findByEmail(command.getEmail())
                .orElseThrow(() -> new BadCredentialsException("잘못된 이메일 또는 비밀번호입니다."));

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("잘못된 이메일 또는 비밀번호입니다.");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        log.info("일반 로그인 성공: userId={}", user.getId());
        return createTokenResult(user);
    }

    @Override
    @Transactional
    public TokenResult oauthSignUp(OAuthSignUpCommand command) {
        log.info("OAuth 회원가입 시작: provider={}, oauthUserId={}", 
                command.getProvider(), command.getOauthUserId());

        // OAuth 사용자 중복 확인
        if (userPort.existsByProviderAndOauthUserId(command.getProvider(), command.getOauthUserId())) {
            throw new OAuthUserAlreadyExistsException("이미 존재하는 OAuth 사용자입니다.");
        }

        // 이메일 중복 확인 (선택사항)
        if (command.getEmail() != null && userPort.existsByEmail(command.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + command.getEmail());
        }

        // OAuth 사용자 생성 - 새로운 방식 사용
        User user = User.builder()
                .email(command.getEmail())
                .nickname(command.getNickname())
                .profileImageUrl(command.getProfileImageUrl())
                .status(UserStatus.ACTIVE)
                .build();

        // OAuth 정보는 별도로 관리해야 할 수 있음 (AuthUser 엔티티 사용)
        User savedUser = userPort.save(user);
        log.info("OAuth 회원가입 완료: userId={}, provider={}", savedUser.getId(), command.getProvider());

        return createTokenResult(savedUser);
    }

    @Override
    public TokenResult oauthLogin(OAuthLoginCommand command) {
        log.info("OAuth 로그인 시도: provider={}, oauthUserId={}", 
                command.getProvider(), command.getOauthUserId());

        User user = userPort.findByProviderAndOauthUserId(command.getProvider(), command.getOauthUserId())
                .orElseThrow(() -> new OAuthUserNotFoundException("OAuth 사용자를 찾을 수 없습니다."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        log.info("OAuth 로그인 성공: userId={}, provider={}", user.getId(), command.getProvider());
        return createTokenResult(user);
    }

    @Override
    public TokenResult refreshToken(String refreshToken) {
        log.info("토큰 갱신 요청");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        log.info("토큰 갱신 성공: userId={}", userId);
        return createTokenResult(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userPort.existsByEmail(email);
    }

    @Override
    public OAuthUserCheckResult checkOAuthUser(OAuthUserCheckCommand command) {
        log.info("OAuth 사용자 확인: provider={}, oauthUserId={}", 
                command.getProvider(), command.getOauthUserId());

        return userPort.findByProviderAndOauthUserId(command.getProvider(), command.getOauthUserId())
                .map(user -> OAuthUserCheckResult.builder()
                        .exists(true)
                        .userId(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .profileImageUrl(user.getProfileImageUrl())
                        .interests(user.getInterestCategories())
                        .build())
                .orElse(OAuthUserCheckResult.builder()
                        .exists(false)
                        .build());
    }

    /**
     * OAuth 사용자 존재 여부 확인 (레거시 메서드)
     *
     * @param provider    OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @return 사용자 확인 결과
     */
    public OAuthUserCheckResult checkOAuthUser(AuthProvider provider, String oauthUserId) {
        log.info("OAuth 사용자 확인: provider={}, oauthUserId={}", provider, oauthUserId);

        return userPort.findByProviderAndOauthUserId(provider, oauthUserId)
                .map(user -> OAuthUserCheckResult.builder()
                        .exists(true)
                        .userId(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .profileImageUrl(user.getProfileImageUrl())
                        .interests(user.getInterestCategories())
                        .build())
                .orElse(OAuthUserCheckResult.builder()
                        .exists(false)
                        .build());
    }

    /**
     * 토큰 결과 생성
     *
     * @param user 사용자
     * @return 토큰 결과
     */
    private TokenResult createTokenResult(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return TokenResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }
}
