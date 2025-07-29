package com.seoulfit.backend.application.service;

import com.seoulfit.backend.application.exception.OAuthUserAlreadyExistsException;
import com.seoulfit.backend.application.exception.OAuthUserNotFoundException;
import com.seoulfit.backend.application.jwt.JwtTokenProvider;
import com.seoulfit.backend.domain.OAuthProvider;
import com.seoulfit.backend.domain.User;
import com.seoulfit.backend.domain.UserStatus;
import com.seoulfit.backend.infra.UserRepository;
import com.seoulfit.backend.presentation.dto.OAuthLoginRequest;
import com.seoulfit.backend.presentation.dto.OAuthSignUpRequest;
import com.seoulfit.backend.presentation.dto.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuthAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private OAuthSignUpRequest oauthSignUpRequest;
    private OAuthLoginRequest oauthLoginRequest;
    private User oauthUser;

    @BeforeEach
    void setUp() {
        oauthSignUpRequest = OAuthSignUpRequest.builder()
                .provider(OAuthProvider.KAKAO)
                .oauthUserId("kakao123")
                .nickname("테스트유저")
                .email("test@example.com")
                .profileImageUrl("https://example.com/profile.jpg")
                .build();

        oauthLoginRequest = OAuthLoginRequest.builder()
                .provider(OAuthProvider.KAKAO)
                .oauthUserId("kakao123")
                .build();

        oauthUser = User.createOAuthUser(
                "test@example.com",
                "테스트유저",
                OAuthProvider.KAKAO,
                "kakao123",
                "https://example.com/profile.jpg"
        );
    }

    @Test
    @DisplayName("OAuth 회원가입 성공")
    void oauthSignUp_Success() {
        // given
        given(userRepository.findByOauthProviderAndOauthId(OAuthProvider.KAKAO, "kakao123"))
                .willReturn(Optional.empty());
        given(userRepository.existsByEmail("test@example.com")).willReturn(false);
        given(userRepository.existsByNickname("테스트유저")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(oauthUser);
        given(jwtTokenProvider.createAccessToken(any(), any())).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("refresh-token");

        // when
        TokenResponse response = authService.oauthSignUp(oauthSignUpRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getNickname()).isEqualTo("테스트유저");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("OAuth 회원가입 실패 - 이미 존재하는 사용자")
    void oauthSignUp_Fail_UserAlreadyExists() {
        // given
        given(userRepository.findByOauthProviderAndOauthId(OAuthProvider.KAKAO, "kakao123"))
                .willReturn(Optional.of(oauthUser));

        // when & then
        assertThatThrownBy(() -> authService.oauthSignUp(oauthSignUpRequest))
                .isInstanceOf(OAuthUserAlreadyExistsException.class)
                .hasMessage("이미 가입된 OAuth 사용자입니다.");
    }

    @Test
    @DisplayName("OAuth 로그인 성공")
    void oauthLogin_Success() {
        // given
        given(userRepository.findByOauthProviderAndOauthIdAndStatus(
                OAuthProvider.KAKAO, "kakao123", UserStatus.ACTIVE))
                .willReturn(Optional.of(oauthUser));
        given(jwtTokenProvider.createAccessToken(any(), any())).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("refresh-token");

        // when
        TokenResponse response = authService.oauthLogin(oauthLoginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("OAuth 로그인 실패 - 등록되지 않은 사용자")
    void oauthLogin_Fail_UserNotFound() {
        // given
        given(userRepository.findByOauthProviderAndOauthIdAndStatus(
                OAuthProvider.KAKAO, "kakao123", UserStatus.ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.oauthLogin(oauthLoginRequest))
                .isInstanceOf(OAuthUserNotFoundException.class)
                .hasMessage("등록되지 않은 OAuth 사용자입니다.");
    }

    @Test
    @DisplayName("OAuth 사용자 존재 여부 확인 - 존재함")
    void isOAuthUserExists_True() {
        // given
        given(userRepository.findByOauthProviderAndOauthId(OAuthProvider.KAKAO, "kakao123"))
                .willReturn(Optional.of(oauthUser));

        // when
        boolean exists = authService.isOAuthUserExists(OAuthProvider.KAKAO, "kakao123");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("OAuth 사용자 존재 여부 확인 - 존재하지 않음")
    void isOAuthUserExists_False() {
        // given
        given(userRepository.findByOauthProviderAndOauthId(OAuthProvider.KAKAO, "kakao123"))
                .willReturn(Optional.empty());

        // when
        boolean exists = authService.isOAuthUserExists(OAuthProvider.KAKAO, "kakao123");

        // then
        assertThat(exists).isFalse();
    }
}
