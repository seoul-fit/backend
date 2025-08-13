package com.seoulfit.backend.user.application.service;

import com.seoulfit.backend.user.application.port.in.AuthenticateUserUseCase;
import com.seoulfit.backend.user.application.port.in.dto.OAuthAuthorizationCommand;
import com.seoulfit.backend.user.application.port.in.dto.OAuthAuthorizeCheckCommand;
import com.seoulfit.backend.user.application.port.in.dto.OAuthAuthorizeCheckResult;
import com.seoulfit.backend.user.application.port.in.dto.OAuthLoginCommand;
import com.seoulfit.backend.user.application.port.in.dto.OAuthSignUpCommand;
import com.seoulfit.backend.user.application.port.in.dto.OAuthUserCheckCommand;
import com.seoulfit.backend.user.application.port.in.dto.OAuthUserCheckResult;
import com.seoulfit.backend.user.application.port.in.dto.TokenResult;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import com.seoulfit.backend.user.domain.exception.OAuthUserAlreadyExistsException;
import com.seoulfit.backend.user.domain.exception.OAuthUserNotFoundException;
import com.seoulfit.backend.user.infrastructure.dto.OAuthTokenResponse;
import com.seoulfit.backend.user.infrastructure.dto.OAuthUserInfo;
import com.seoulfit.backend.user.infrastructure.jwt.JwtTokenProvider;
import com.seoulfit.backend.user.infrastructure.oauth.OAuthClient;
import com.seoulfit.backend.user.infrastructure.oauth.OAuthClientFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 통합 인증 서비스
 * <p>
 * 헥사고날 아키텍처의 애플리케이션 서비스 모든 인증 관련 비즈니스 로직을 통합 관리 Authorization Code Flow를 지원하도록 업데이트
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService implements AuthenticateUserUseCase {

    private final UserPort userPort;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthClientFactory oAuthClientFactory;

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
                .oauthProvider(command.getProvider())
                .oauthUserId(command.getOauthUserId())
                .profileImageUrl(command.getProfileImageUrl())
                .status(UserStatus.ACTIVE)
                .build();

        // OAuth 정보는 별도로 관리해야 할 수 있음 (AuthUser 엔티티 사용)
        User savedUser = userPort.save(user);

        // 사용자 선호도 저장
        if (command.getInterests() != null && !command.getInterests().isEmpty()) {
            userPort.saveUserInterests(savedUser.getId(), command.getInterests());
        }

        log.info("OAuth 회원가입 완료: userId={}, provider={}", savedUser.getId(), command.getProvider());

        return createTokenResult(savedUser);
    }

    @Override
    public TokenResult oauthLogin(OAuthLoginCommand command) {
        // 새로운 Authorization Code Flow 방식 우선 처리
        if (command.getAuthorizationCode() != null && command.getRedirectUri() != null) {
            return oauthLoginWithAuthorizationCode(
                    OAuthAuthorizationCommand.of(command.getProvider(), command.getAuthorizationCode(), command.getRedirectUri())
            );
        }

        // 기존 방식 (deprecated)
        return oauthLoginLegacy(command);
    }

    @Override
    @Transactional
    public TokenResult oauthLoginWithAuthorizationCode(OAuthAuthorizationCommand command) {
        log.info("OAuth Authorization Code 로그인 시작: provider={}", command.getProvider());

        try {
            // 1. OAuth 클라이언트 가져오기
            OAuthClient oAuthClient = oAuthClientFactory.getClient(command.getProvider());

            // 2. Authorization Code를 Access Token으로 교환
            OAuthTokenResponse tokenResponse = oAuthClient.exchangeCodeForToken(
                    command.getAuthorizationCode(),
                    command.getRedirectUri()
            );

            // 3. Access Token으로 사용자 정보 조회
            OAuthUserInfo userInfo = oAuthClient.getUserInfo(tokenResponse.getAccessToken());

            // 4. 사용자 조회 또는 생성 (OAuth 토큰 정보 포함)
            User user = findOrCreateUser(userInfo, tokenResponse);

            log.info("OAuth Authorization Code 로그인 성공: userId={}, provider={}",
                    user.getId(), command.getProvider());

            return createTokenResult(user);

        } catch (Exception e) {
            log.error("OAuth Authorization Code 로그인 실패: provider={}", command.getProvider(), e);
            throw new RuntimeException("OAuth 로그인에 실패했습니다.", e);
        }
    }

    /**
     * 기존 방식의 OAuth 로그인 (deprecated)
     */
    @Deprecated
    private TokenResult oauthLoginLegacy(OAuthLoginCommand command) {
        log.info("OAuth 로그인 시도 (기존 방식): provider={}, oauthUserId={}",
                command.getProvider(), command.getOauthUserId());

        User user = userPort.findByProviderAndOauthUserId(command.getProvider(), command.getOauthUserId())
                .orElseThrow(() -> new OAuthUserNotFoundException("OAuth 사용자를 찾을 수 없습니다."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }

        log.info("OAuth 로그인 성공 (기존 방식): userId={}, provider={}", user.getId(), command.getProvider());
        return createTokenResult(user);
    }

    /**
     * 사용자 조회 또는 생성 (OAuth 토큰 정보 포함)
     */
    @Transactional
    protected User findOrCreateUser(OAuthUserInfo userInfo, OAuthTokenResponse tokenResponse) {
        // 토큰 만료 시간 계산
        LocalDateTime expiresAt = calculateTokenExpiryTime(tokenResponse.getExpiresIn());

        // 기존 사용자 조회
        return userPort.findByProviderAndOauthUserId(userInfo.getProvider(), userInfo.getOAuthId())
                .map(existingUser -> {
                    // 기존 사용자의 OAuth 토큰 업데이트
                    existingUser.updateOAuthToken(tokenResponse.getAccessToken(), expiresAt);
                    return userPort.save(existingUser);
                })
                .orElseGet(() -> {
                    log.info("새로운 OAuth 사용자 생성: provider={}, oauthId={}",
                            userInfo.getProvider(), userInfo.getOAuthId());

                    // 새 사용자 생성 (토큰 정보 포함)
                    User newUser = User.createOAuthUserWithToken(
                            userInfo.getProvider(),
                            userInfo.getOAuthId(),
                            userInfo.getNickname(),
                            userInfo.getEmail(),
                            userInfo.getProfileImageUrl(),
                            tokenResponse.getAccessToken(),
                            expiresAt
                    );

                    return userPort.save(newUser);
                });
    }

    /**
     * 기존 방식의 사용자 조회 또는 생성 (deprecated)
     */
    @Deprecated
    protected User findOrCreateUser(OAuthUserInfo userInfo) {
        // 기존 사용자 조회
        return userPort.findByProviderAndOauthUserId(userInfo.getProvider(), userInfo.getOAuthId())
                .orElseGet(() -> {
                    log.info("새로운 OAuth 사용자 생성: provider={}, oauthId={}",
                            userInfo.getProvider(), userInfo.getOAuthId());

                    // 새 사용자 생성
                    User newUser = User.builder()
                            .email(userInfo.getEmail())
                            .nickname(userInfo.getNickname())
                            .oauthProvider(userInfo.getProvider())
                            .oauthUserId(userInfo.getOAuthId())
                            .profileImageUrl(userInfo.getProfileImageUrl())
                            .status(UserStatus.ACTIVE)
                            .build();

                    return userPort.save(newUser);
                });
    }

    /**
     * 토큰 만료 시간 계산
     *
     * @param expiresIn 만료까지 남은 초
     * @return 만료 시간
     */
    private LocalDateTime calculateTokenExpiryTime(Long expiresIn) {
        if (expiresIn == null || expiresIn <= 0) {
            // 기본값: 2시간 후 만료
            return LocalDateTime.now().plusHours(2);
        }
        return LocalDateTime.now().plusSeconds(expiresIn);
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

    @Override
    public OAuthAuthorizeCheckResult checkAuthorizationCode(OAuthAuthorizeCheckCommand command) {
        log.info("OAuth 인가코드 검증 시작: provider={}", command.getProvider());

        try {
            // OAuth 클라이언트 조회
            OAuthClient oAuthClient = oAuthClientFactory.getClient(command.getProvider());

            // 인가코드로 액세스 토큰 교환
            OAuthTokenResponse tokenResponse = oAuthClient.exchangeCodeForToken(
                    command.getAuthorizationCode(),
                    command.getRedirectUri()
            );

            // 액세스 토큰으로 사용자 정보 조회
            OAuthUserInfo userInfo = oAuthClient.getUserInfo(tokenResponse.getAccessToken());

            log.info("OAuth 사용자 정보 조회 성공: provider={}, oauthUserId={}",
                    command.getProvider(), userInfo.getOAuthId());

            return OAuthAuthorizeCheckResult.of(
                    command.getProvider(),
                    userInfo.getOAuthId(),
                    userInfo.getNickname(),
                    userInfo.getEmail(),
                    userInfo.getProfileImageUrl()
            );

        } catch (Exception e) {
            log.error("OAuth 인가코드 검증 실패: provider={}, error={}",
                    command.getProvider(), e.getMessage(), e);
            throw new RuntimeException("OAuth 인가코드 검증에 실패했습니다: " + e.getMessage(), e);
        }
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
