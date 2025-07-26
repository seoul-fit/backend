package com.seoulfit.backend.application.service;

import com.seoulfit.backend.domain.InterestCategory;
import com.seoulfit.backend.domain.User;
import com.seoulfit.backend.domain.UserStatus;
import com.seoulfit.backend.infra.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("사용자 생성 - 성공")
    void createUser_Success() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String nickname = "테스트유저";
        List<InterestCategory> interests = List.of(InterestCategory.SPORTS, InterestCategory.CULTURE);

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByNickname(nickname)).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return User.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .nickname(user.getNickname())
                    .build();
        });

        // when
        User result = userService.createUser(email, password, nickname, interests);

        // then
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getNickname()).isEqualTo(nickname);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 이메일 중복 실패")
    void createUser_DuplicateEmail_ThrowsException() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String nickname = "테스트유저";
        List<InterestCategory> interests = List.of(InterestCategory.SPORTS);

        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(email, password, nickname, interests))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 성공")
    void findByEmail_Success() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .password("password")
                .nickname("테스트유저")
                .build();

        given(userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE))
                .willReturn(Optional.of(user));

        // when
        User result = userService.findByEmail(email);

        // then
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 사용자 없음")
    void findByEmail_UserNotFound_ThrowsException() {
        // given
        String email = "notfound@example.com";
        given(userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findByEmail(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }
}
