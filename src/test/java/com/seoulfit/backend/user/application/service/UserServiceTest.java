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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserPort userPort;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .nickname("테스트유저")
                .profileImageUrl("https://example.com/profile.jpg")
                .locationLatitude(37.5665)
                .locationLongitude(126.9780)
                .locationAddress("서울시 강남구")
                .status(UserStatus.ACTIVE)
                .oauthProvider(AuthProvider.KAKAO)
                .oauthUserId("kakao123456")
                .build();
        
        // Reflection을 사용하여 ID 설정 (테스트 목적)
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(testUser, 1L);
        } catch (Exception e) {
            // 테스트에서만 사용
        }
    }

    @Test
    @DisplayName("사용자 ID로 조회 - 성공")
    void getUser_Success() {
        // given
        Long userId = 1L;
        given(userPort.findById(userId)).willReturn(Optional.of(testUser));

        // when
        UserResult result = userService.getUser(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getNickname()).isEqualTo("테스트유저");
        verify(userPort).findById(userId);
    }

    @Test
    @DisplayName("사용자 ID로 조회 - 사용자 없음")
    void getUser_NotFound() {
        // given
        Long userId = 999L;
        given(userPort.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        verify(userPort).findById(userId);
    }

    @Test
    @DisplayName("OAuth로 사용자 조회 - 성공")
    void getUserByOAuth_Success() {
        // given
        String oauthUserId = "kakao123456";
        AuthProvider provider = AuthProvider.KAKAO;
        given(userPort.findByProviderAndOauthUserId(provider, oauthUserId)).willReturn(Optional.of(testUser));

        // when
        UserResult result = userService.getUserByOAuth(oauthUserId, provider);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo("테스트유저");
        verify(userPort).findByProviderAndOauthUserId(provider, oauthUserId);
    }

    @Test
    @DisplayName("사용자 정보 수정 - 성공")
    void updateUser_Success() {
        // given
        Long userId = 1L;
        UpdateUserCommand command = UpdateUserCommand.of(
                userId,
                "새닉네임",
                "https://example.com/new.jpg",
                37.5172,
                127.0473,
                "서울시 강남구 테헤란로",
                List.of(InterestCategory.SPORTS, InterestCategory.CULTURE)
        );

        given(userPort.findById(userId)).willReturn(Optional.of(testUser));
        given(userPort.save(any(User.class))).willReturn(testUser);

        // when
        UserResult result = userService.updateUser(command);

        // then
        assertThat(result).isNotNull();
        verify(userPort).findById(userId);
        verify(userPort).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void deleteUser_Success() {
        // given
        Long userId = 1L;
        given(userPort.findById(userId)).willReturn(Optional.of(testUser));
        given(userPort.save(any(User.class))).willReturn(testUser);

        // when
        userService.deleteUser(userId);

        // then
        verify(userPort).findById(userId);
        verify(userPort).save(argThat(user -> 
            user.getStatus() == UserStatus.INACTIVE
        ));
    }

    @Test
    @DisplayName("사용자 삭제 - 사용자 없음")
    void deleteUser_NotFound() {
        // given
        Long userId = 999L;
        given(userPort.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        verify(userPort).findById(userId);
        verify(userPort, never()).save(any());
    }
}