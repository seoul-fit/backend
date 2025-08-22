package com.seoulfit.backend.user.application.service;

import com.seoulfit.backend.user.application.port.in.dto.*;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import com.seoulfit.backend.user.domain.exception.OAuthUserAlreadyExistsException;
import com.seoulfit.backend.user.domain.exception.OAuthUserNotFoundException;
import com.seoulfit.backend.user.infrastructure.dto.OAuthTokenResponse;
import com.seoulfit.backend.user.infrastructure.dto.OAuthUserInfo;
import com.seoulfit.backend.user.infrastructure.jwt.JwtTokenProvider;
import com.seoulfit.backend.user.infrastructure.oauth.OAuthClient;
import com.seoulfit.backend.user.infrastructure.oauth.OAuthClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * AuthenticationService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserPort userPort;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private OAuthClientFactory oAuthClientFactory;

    @Mock
    private OAuthClient oAuthClient;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private OAuthSignUpCommand signUpCommand;
    private OAuthLoginCommand loginCommand;
    private OAuthAuthorizationCommand authCommand;
    private OAuthTokenResponse tokenResponse;
    private OAuthUserInfo userInfo;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .email("test@example.com")
                .nickname("testuser")
                .oauthProvider(AuthProvider.KAKAO)
                .oauthUserId("kakao123")
                .profileImageUrl("http://profile.jpg")
                .status(UserStatus.ACTIVE)
                .build();
        // ID는 reflection으로 설정 (테스트용)
        setUserId(testUser, 1L);

        // OAuth 회원가입 명령
        signUpCommand = OAuthSignUpCommand.builder()
                .provider(AuthProvider.KAKAO)
                .oauthUserId("kakao123")
                .email("test@example.com")
                .nickname("testuser")
                .profileImageUrl("http://profile.jpg")
                .interests(List.of(InterestCategory.CULTURE, InterestCategory.SPORTS))
                .build();

        // OAuth 로그인 명령
        loginCommand = OAuthLoginCommand.builder()
                .provider(AuthProvider.KAKAO)
                .oauthUserId("kakao123")
                .build();

        // OAuth Authorization Code 명령
        authCommand = OAuthAuthorizationCommand.of(
                AuthProvider.KAKAO,
                "auth_code_123",
                "http://localhost:8080/callback"
        );

        // OAuth 토큰 응답
        tokenResponse = new OAuthTokenResponse();
        setFieldValue(tokenResponse, "accessToken", "access_token_123");
        setFieldValue(tokenResponse, "refreshToken", "refresh_token_123");
        setFieldValue(tokenResponse, "expiresIn", 3600L);
        setFieldValue(tokenResponse, "tokenType", "Bearer");

        // OAuth 사용자 정보
        userInfo = OAuthUserInfo.builder()
                .provider(AuthProvider.KAKAO)
                .id("kakao123")
                .email("test@example.com")
                .nickname("testuser")
                .profileImageUrl("http://profile.jpg")
                .build();
    }
    
    // 테스트용 헬퍼 메서드 - reflection으로 ID 설정
    private void setUserId(User user, Long id) {
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user ID", e);
        }
    }
    
    // 테스트용 헬퍼 메서드 - reflection으로 필드 설정
    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field value", e);
        }
    }

    @Test
    @DisplayName("OAuth 회원가입 - 성공")
    void oauthSignUp_Success() {
        // given
        when(userPort.existsByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(false);
        when(userPort.existsByEmail("test@example.com"))
                .thenReturn(false);
        when(userPort.save(any(User.class)))
                .thenReturn(testUser);
        when(jwtTokenProvider.createAccessToken(1L, "test@example.com"))
                .thenReturn("access_token");
        when(jwtTokenProvider.createRefreshToken(1L))
                .thenReturn("refresh_token");

        // when
        TokenResult result = authenticationService.oauthSignUp(signUpCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access_token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getNickname()).isEqualTo("testuser");

        verify(userPort).save(any(User.class));
        verify(userPort).saveUserInterests(1L, signUpCommand.getInterests());
    }

    @Test
    @DisplayName("OAuth 회원가입 - 이미 존재하는 사용자")
    void oauthSignUp_AlreadyExists() {
        // given
        when(userPort.existsByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authenticationService.oauthSignUp(signUpCommand))
                .isInstanceOf(OAuthUserAlreadyExistsException.class)
                .hasMessage("이미 존재하는 OAuth 사용자입니다.");

        verify(userPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("OAuth 회원가입 - 이메일 중복")
    void oauthSignUp_EmailDuplicate() {
        // given
        when(userPort.existsByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(false);
        when(userPort.existsByEmail("test@example.com"))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authenticationService.oauthSignUp(signUpCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다: test@example.com");

        verify(userPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("OAuth Authorization Code 로그인 - 성공")
    void oauthLoginWithAuthorizationCode_Success() {
        // given
        when(oAuthClientFactory.getClient(AuthProvider.KAKAO))
                .thenReturn(oAuthClient);
        when(oAuthClient.exchangeCodeForToken("auth_code_123", "http://localhost:8080/callback"))
                .thenReturn(tokenResponse);
        when(oAuthClient.getUserInfo("access_token_123"))
                .thenReturn(userInfo);
        when(userPort.findByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(Optional.of(testUser));
        when(userPort.save(any(User.class)))
                .thenReturn(testUser);
        when(jwtTokenProvider.createAccessToken(1L, "test@example.com"))
                .thenReturn("jwt_access_token");
        when(jwtTokenProvider.createRefreshToken(1L))
                .thenReturn("jwt_refresh_token");

        // when
        TokenResult result = authenticationService.oauthLoginWithAuthorizationCode(authCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("jwt_access_token");
        assertThat(result.getRefreshToken()).isEqualTo("jwt_refresh_token");
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(oAuthClient).exchangeCodeForToken("auth_code_123", "http://localhost:8080/callback");
        verify(oAuthClient).getUserInfo("access_token_123");
    }

    @Test
    @DisplayName("OAuth Authorization Code 로그인 - 새 사용자 생성")
    void oauthLoginWithAuthorizationCode_CreateNewUser() {
        // given
        User newUser = User.createOAuthUserWithToken(
                AuthProvider.KAKAO,
                "kakao123",
                "testuser",
                "test@example.com",
                "http://profile.jpg",
                "access_token_123",
                LocalDateTime.now().plusHours(1)
        );
        setUserId(newUser, 2L);

        when(oAuthClientFactory.getClient(AuthProvider.KAKAO))
                .thenReturn(oAuthClient);
        when(oAuthClient.exchangeCodeForToken("auth_code_123", "http://localhost:8080/callback"))
                .thenReturn(tokenResponse);
        when(oAuthClient.getUserInfo("access_token_123"))
                .thenReturn(userInfo);
        when(userPort.findByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(Optional.empty());
        when(userPort.save(any(User.class)))
                .thenReturn(newUser);
        when(jwtTokenProvider.createAccessToken(2L, "test@example.com"))
                .thenReturn("jwt_access_token");
        when(jwtTokenProvider.createRefreshToken(2L))
                .thenReturn("jwt_refresh_token");

        // when
        TokenResult result = authenticationService.oauthLoginWithAuthorizationCode(authCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(2L);
        verify(userPort).save(any(User.class));
    }

    @Test
    @DisplayName("OAuth 로그인 (기존 방식) - 성공")
    void oauthLogin_Legacy_Success() {
        // given
        when(userPort.findByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createAccessToken(1L, "test@example.com"))
                .thenReturn("access_token");
        when(jwtTokenProvider.createRefreshToken(1L))
                .thenReturn("refresh_token");

        // when
        TokenResult result = authenticationService.oauthLogin(loginCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access_token");
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("OAuth 로그인 (기존 방식) - 사용자 없음")
    void oauthLogin_Legacy_UserNotFound() {
        // given
        when(userPort.findByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authenticationService.oauthLogin(loginCommand))
                .isInstanceOf(OAuthUserNotFoundException.class)
                .hasMessage("OAuth 사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("OAuth 로그인 (기존 방식) - 비활성화된 계정")
    void oauthLogin_Legacy_InactiveUser() {
        // given
        User inactiveUser = User.builder()
                .email("test@example.com")
                .nickname("testuser")
                .oauthProvider(AuthProvider.KAKAO)
                .oauthUserId("kakao123")
                .status(UserStatus.INACTIVE)
                .build();
        setUserId(inactiveUser, 1L);

        when(userPort.findByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(Optional.of(inactiveUser));

        // when & then
        assertThatThrownBy(() -> authenticationService.oauthLogin(loginCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("비활성화된 계정입니다.");
    }

    @Test
    @DisplayName("토큰 갱신 - 성공")
    void refreshToken_Success() {
        // given
        String refreshToken = "valid_refresh_token";
        when(jwtTokenProvider.validateToken(refreshToken))
                .thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken))
                .thenReturn(1L);
        when(userPort.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createAccessToken(1L, "test@example.com"))
                .thenReturn("new_access_token");
        when(jwtTokenProvider.createRefreshToken(1L))
                .thenReturn("new_refresh_token");

        // when
        TokenResult result = authenticationService.refreshToken(refreshToken);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new_access_token");
        assertThat(result.getRefreshToken()).isEqualTo("new_refresh_token");
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("토큰 갱신 - 유효하지 않은 토큰")
    void refreshToken_InvalidToken() {
        // given
        String invalidToken = "invalid_refresh_token";
        when(jwtTokenProvider.validateToken(invalidToken))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authenticationService.refreshToken(invalidToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 리프레시 토큰입니다.");
    }

    @Test
    @DisplayName("토큰 갱신 - 사용자 없음")
    void refreshToken_UserNotFound() {
        // given
        String refreshToken = "valid_refresh_token";
        when(jwtTokenProvider.validateToken(refreshToken))
                .thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(refreshToken))
                .thenReturn(999L);
        when(userPort.findById(999L))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authenticationService.refreshToken(refreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("OAuth 사용자 확인 - 존재하는 사용자")
    void checkOAuthUser_Exists() {
        // given
        OAuthUserCheckCommand command = OAuthUserCheckCommand.builder()
                .provider(AuthProvider.KAKAO)
                .oauthUserId("kakao123")
                .build();

        when(userPort.findByProviderAndOauthUserId(AuthProvider.KAKAO, "kakao123"))
                .thenReturn(Optional.of(testUser));

        // when
        OAuthUserCheckResult result = authenticationService.checkOAuthUser(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isExists()).isTrue();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getNickname()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("OAuth 사용자 확인 - 존재하지 않는 사용자")
    void checkOAuthUser_NotExists() {
        // given
        OAuthUserCheckCommand command = OAuthUserCheckCommand.builder()
                .provider(AuthProvider.KAKAO)
                .oauthUserId("unknown123")
                .build();

        when(userPort.findByProviderAndOauthUserId(AuthProvider.KAKAO, "unknown123"))
                .thenReturn(Optional.empty());

        // when
        OAuthUserCheckResult result = authenticationService.checkOAuthUser(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.isExists()).isFalse();
        assertThat(result.getUserId()).isNull();
    }

    @Test
    @DisplayName("OAuth 인가코드 검증 - 성공")
    void checkAuthorizationCode_Success() {
        // given
        OAuthAuthorizeCheckCommand command = OAuthAuthorizeCheckCommand.builder()
                .provider(AuthProvider.KAKAO)
                .authorizationCode("auth_code_123")
                .redirectUri("http://localhost:8080/callback")
                .build();

        when(oAuthClientFactory.getClient(AuthProvider.KAKAO))
                .thenReturn(oAuthClient);
        when(oAuthClient.exchangeCodeForToken("auth_code_123", "http://localhost:8080/callback"))
                .thenReturn(tokenResponse);
        when(oAuthClient.getUserInfo("access_token_123"))
                .thenReturn(userInfo);

        // when
        OAuthAuthorizeCheckResult result = authenticationService.checkAuthorizationCode(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProvider()).isEqualTo(AuthProvider.KAKAO);
        assertThat(result.getOauthUserId()).isEqualTo("kakao123");
        assertThat(result.getNickname()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("OAuth 인가코드 검증 - 실패")
    void checkAuthorizationCode_Failure() {
        // given
        OAuthAuthorizeCheckCommand command = OAuthAuthorizeCheckCommand.builder()
                .provider(AuthProvider.KAKAO)
                .authorizationCode("invalid_code")
                .redirectUri("http://localhost:8080/callback")
                .build();

        when(oAuthClientFactory.getClient(AuthProvider.KAKAO))
                .thenReturn(oAuthClient);
        when(oAuthClient.exchangeCodeForToken("invalid_code", "http://localhost:8080/callback"))
                .thenThrow(new RuntimeException("Invalid authorization code"));

        // when & then
        assertThatThrownBy(() -> authenticationService.checkAuthorizationCode(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("OAuth 인가코드 검증에 실패했습니다");
    }
}