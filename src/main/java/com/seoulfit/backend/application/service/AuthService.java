package com.seoulfit.backend.application.service;

import com.seoulfit.backend.application.exception.OAuthUserAlreadyExistsException;
import com.seoulfit.backend.application.exception.OAuthUserNotFoundException;
import com.seoulfit.backend.application.jwt.JwtTokenProvider;
import com.seoulfit.backend.domain.OAuthProvider;
import com.seoulfit.backend.domain.User;
import com.seoulfit.backend.domain.UserStatus;
import com.seoulfit.backend.infra.UserRepository;
import com.seoulfit.backend.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 사용자 생성
        User user = User.createLocalUser(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        );

        User savedUser = userRepository.save(user);

        // JWT 토큰 생성
        return createTokenResponse(savedUser);
    }

    @Transactional
    public TokenResponse oauthSignUp(OAuthSignUpRequest request) {
        // OAuth 사용자 중복 체크
        if (userRepository.findByOauthProviderAndOauthId(request.getProvider(), request.getOauthUserId()).isPresent()) {
            throw new OAuthUserAlreadyExistsException("이미 가입된 OAuth 사용자입니다.");
        }

        // 이메일 중복 체크 (OAuth 사용자도 이메일 중복 불가)
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // OAuth 사용자 생성
        User user = User.createOAuthUser(
                request.getEmail(),
                request.getNickname(),
                request.getProvider(),
                request.getOauthUserId(),
                request.getProfileImageUrl()
        );

        User savedUser = userRepository.save(user);

        // JWT 토큰 생성
        return createTokenResponse(savedUser);
    }

    public TokenResponse oauthLogin(OAuthLoginRequest request) {
        // OAuth 사용자 조회
        User user = userRepository.findByOauthProviderAndOauthIdAndStatus(
                request.getProvider(), 
                request.getOauthUserId(), 
                UserStatus.ACTIVE
        ).orElseThrow(() -> new OAuthUserNotFoundException("등록되지 않은 OAuth 사용자입니다."));

        // JWT 토큰 생성
        return createTokenResponse(user);
    }

    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmailAndStatus(request.getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // OAuth 사용자인 경우 일반 로그인 불가
        if (user.isOAuthUser()) {
            throw new BadCredentialsException("소셜 로그인 사용자입니다. 소셜 로그인을 이용해주세요.");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 토큰 생성
        return createTokenResponse(user);
    }

    public TokenResponse refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 사용자 조회
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("비활성화된 사용자입니다.");
        }

        // 새로운 액세스 토큰 생성 (기존 리프레시 토큰 유지)
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .user(UserResponse.from(user))
                .build();
    }

    /**
     * OAuth 사용자 존재 여부 확인
     */
    public boolean isOAuthUserExists(OAuthProvider provider, String oauthUserId) {
        return userRepository.findByOauthProviderAndOauthId(provider, oauthUserId).isPresent();
    }

    /**
     * OAuth 사용자 정보 조회 (로그인 전 사용자 정보 확인용)
     */
    public User getOAuthUser(OAuthProvider provider, String oauthUserId) {
        return userRepository.findByOauthProviderAndOauthIdAndStatus(provider, oauthUserId, UserStatus.ACTIVE)
                .orElse(null);
    }

    /**
     * 공통 토큰 생성 메서드
     */
    private TokenResponse createTokenResponse(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserResponse.from(user))
                .build();
    }
}
