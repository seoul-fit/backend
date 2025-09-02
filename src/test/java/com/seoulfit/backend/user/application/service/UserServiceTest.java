package com.seoulfit.backend.user.application.service;

import com.seoulfit.backend.user.application.port.in.dto.UpdateUserCommand;
import com.seoulfit.backend.user.application.port.in.dto.UserResult;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * UserService 단위 테스트
 * 
 * <p>UserService의 핵심 비즈니스 로직을 검증하는 단위 테스트입니다.
 * 모든 외부 의존성은 Mock 처리하여 격리된 환경에서 테스트를 수행합니다.</p>
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserPort userPort;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NICKNAME = "테스트유저";
    private static final String TEST_OAUTH_ID = "kakao123456";

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
    }

    @Nested
    @DisplayName("사용자 조회 테스트")
    class GetUserTest {

        @Test
        @DisplayName("ID로 사용자를 성공적으로 조회한다")
        void shouldGetUserById() {
            // given
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));

            // when
            UserResult result = userService.getUser(TEST_USER_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TEST_USER_ID);
            assertThat(result.getNickname()).isEqualTo(TEST_NICKNAME);
            
            then(userPort).should(times(1)).findById(TEST_USER_ID);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 조회 시 예외가 발생한다")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            Long nonExistentId = 999L;
            given(userPort.findById(nonExistentId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUser(nonExistentId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
            
            then(userPort).should(times(1)).findById(nonExistentId);
        }

        @Test
        @DisplayName("OAuth 정보로 사용자를 성공적으로 조회한다")
        void shouldGetUserByOAuthInfo() {
            // given
            AuthProvider provider = AuthProvider.KAKAO;
            given(userPort.findByProviderAndOauthUserId(provider, TEST_OAUTH_ID))
                    .willReturn(Optional.of(testUser));

            // when
            UserResult result = userService.getUserByOAuth(TEST_OAUTH_ID, provider);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getNickname()).isEqualTo(TEST_NICKNAME);
            
            then(userPort).should(times(1))
                    .findByProviderAndOauthUserId(provider, TEST_OAUTH_ID);
        }

        @Test
        @DisplayName("OAuth 사용자가 존재하지 않으면 예외가 발생한다")
        void shouldThrowExceptionWhenOAuthUserNotFound() {
            // given
            AuthProvider provider = AuthProvider.GOOGLE;
            String unknownOAuthId = "unknown123";
            given(userPort.findByProviderAndOauthUserId(provider, unknownOAuthId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUserByOAuth(unknownOAuthId, provider))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정 테스트")
    class UpdateUserTest {

        @Test
        @DisplayName("프로필 정보만 수정한다")
        void shouldUpdateProfileInfo() {
            // given
            String newNickname = "새닉네임";
            String newProfileUrl = "https://example.com/new.jpg";
            
            UpdateUserCommand command = UpdateUserCommand.builder()
                    .userId(TEST_USER_ID)
                    .nickname(newNickname)
                    .profileImageUrl(newProfileUrl)
                    .build();
            
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userPort.save(any(User.class))).willReturn(testUser);

            // when
            UserResult result = userService.updateUser(command);

            // then
            assertThat(result).isNotNull();
            then(userPort).should().findById(TEST_USER_ID);
            then(userPort).should().save(any(User.class));
        }

        @Test
        @DisplayName("위치 정보만 수정한다")
        void shouldUpdateLocationInfo() {
            // given
            UpdateUserCommand command = UpdateUserCommand.builder()
                    .userId(TEST_USER_ID)
                    .locationLatitude(37.5172)
                    .locationLongitude(127.0473)
                    .locationAddress("서울시 강남구 테헤란로")
                    .build();
            
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userPort.save(any(User.class))).willReturn(testUser);

            // when
            UserResult result = userService.updateUser(command);

            // then
            assertThat(result).isNotNull();
            then(userPort).should().save(argThat(user -> user != null));
        }

        @Test
        @DisplayName("관심사 정보만 수정한다")
        void shouldUpdateInterests() {
            // given
            List<InterestCategory> newInterests = List.of(
                    InterestCategory.SPORTS,
                    InterestCategory.CULTURE,
                    InterestCategory.WEATHER
            );
            
            UpdateUserCommand command = UpdateUserCommand.builder()
                    .userId(TEST_USER_ID)
                    .interests(newInterests)
                    .build();
            
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userPort.save(any(User.class))).willReturn(testUser);

            // when
            UserResult result = userService.updateUser(command);

            // then
            assertThat(result).isNotNull();
            then(userPort).should().save(any(User.class));
        }

        @Test
        @DisplayName("null 값은 업데이트하지 않는다")
        void shouldNotUpdateNullValues() {
            // given
            UpdateUserCommand command = UpdateUserCommand.builder()
                    .userId(TEST_USER_ID)
                    .nickname(null)  // null 값
                    .profileImageUrl(null)  // null 값
                    .build();
            
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            given(userPort.save(any(User.class))).willReturn(testUser);

            // when
            UserResult result = userService.updateUser(command);

            // then
            assertThat(result).isNotNull();
            // null 값이어도 save는 호출됨 (내부 로직에서 null 체크)
            then(userPort).should().save(any(User.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 수정 시 예외가 발생한다")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // given
            UpdateUserCommand command = UpdateUserCommand.builder()
                    .userId(999L)
                    .nickname("새닉네임")
                    .build();
            
            given(userPort.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.updateUser(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
            
            then(userPort).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("사용자 삭제 테스트")
    class DeleteUserTest {

        @Test
        @DisplayName("사용자를 성공적으로 삭제한다")
        void shouldDeleteUser() {
            // given
            given(userPort.findById(TEST_USER_ID)).willReturn(Optional.of(testUser));
            doNothing().when(userPort).deleteById(TEST_USER_ID);

            // when
            userService.deleteUser(TEST_USER_ID);

            // then
            then(userPort).should().findById(TEST_USER_ID);
            then(userPort).should().deleteById(TEST_USER_ID);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 삭제 시 예외가 발생한다")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            // given
            Long nonExistentId = 999L;
            given(userPort.findById(nonExistentId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(nonExistentId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
            
            then(userPort).should(never()).deleteById(any());
        }
    }

    /**
     * 테스트용 User 객체를 생성하는 헬퍼 메서드
     * Reflection을 사용하여 ID 값을 설정합니다.
     */
    private User createTestUser() {
        User user = User.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICKNAME)
                .profileImageUrl("https://example.com/profile.jpg")
                .locationLatitude(37.5665)
                .locationLongitude(126.9780)
                .locationAddress("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .oauthProvider(AuthProvider.KAKAO)
                .oauthUserId(TEST_OAUTH_ID)
                .build();
        
        // Reflection으로 ID 설정 (JPA Entity의 ID는 보통 setter가 없음)
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, TEST_USER_ID);
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 생성 실패", e);
        }
        
        return user;
    }
}