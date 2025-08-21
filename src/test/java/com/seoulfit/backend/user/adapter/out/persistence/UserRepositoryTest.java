package com.seoulfit.backend.user.adapter.out.persistence;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

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
                .build();
    }

    @Test
    @DisplayName("사용자 저장 - 성공")
    void saveUser_Success() {
        // when
        User savedUser = userJpaRepository.save(testUser);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 성공")
    void findByEmail_Success() {
        // given
        userJpaRepository.save(testUser);

        // when
        Optional<User> foundUser = userJpaRepository.findByEmail("test@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("OAuth 정보로 사용자 조회 - 성공")
    void findByOAuthInfo_Success() {
        // given
        User oauthUser = User.builder()
                .email("oauth@kakao.com")
                .nickname("카카오유저")
                .oauthProvider(AuthProvider.KAKAO)
                .oauthUserId("kakao123456")
                .status(UserStatus.ACTIVE)
                .build();
        userJpaRepository.save(oauthUser);

        // when
        Optional<User> foundUser = userJpaRepository.findByOauthProviderAndOauthUserId(
                AuthProvider.KAKAO, "kakao123456");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getOauthProvider()).isEqualTo(AuthProvider.KAKAO);
        assertThat(foundUser.get().getOauthUserId()).isEqualTo("kakao123456");
    }

    @Test
    @DisplayName("이메일 중복 확인 - 존재하는 경우")
    void existsByEmail_Exists() {
        // given
        userJpaRepository.save(testUser);

        // when
        boolean exists = userJpaRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 중복 확인 - 존재하지 않는 경우")
    void existsByEmail_NotExists() {
        // when
        boolean exists = userJpaRepository.existsByEmail("notexist@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자 업데이트 - 성공")
    void updateUser_Success() {
        // given
        User savedUser = userJpaRepository.save(testUser);
        
        // when
        savedUser.updateProfile("새닉네임", "https://new.com/profile.jpg");
        User updatedUser = userJpaRepository.save(savedUser);

        // then
        assertThat(updatedUser.getNickname()).isEqualTo("새닉네임");
        assertThat(updatedUser.getProfileImageUrl()).isEqualTo("https://new.com/profile.jpg");
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void deleteUser_Success() {
        // given
        User savedUser = userJpaRepository.save(testUser);
        Long userId = savedUser.getId();

        // when
        userJpaRepository.deleteById(userId);

        // then
        Optional<User> deletedUser = userJpaRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("상태별 사용자 수 조회 - 성공")
    void countByStatus_Success() {
        // given
        User activeUser1 = User.builder()
                .email("active1@example.com")
                .nickname("활성유저1")
                .status(UserStatus.ACTIVE)
                .build();
        User activeUser2 = User.builder()
                .email("active2@example.com")
                .nickname("활성유저2")
                .status(UserStatus.ACTIVE)
                .build();
        User inactiveUser = User.builder()
                .email("inactive@example.com")
                .nickname("비활성유저")
                .status(UserStatus.INACTIVE)
                .build();

        userJpaRepository.save(activeUser1);
        userJpaRepository.save(activeUser2);
        userJpaRepository.save(inactiveUser);

        // when
        long activeCount = userJpaRepository.countByStatus(UserStatus.ACTIVE);
        long inactiveCount = userJpaRepository.countByStatus(UserStatus.INACTIVE);

        // then
        assertThat(activeCount).isEqualTo(2);
        assertThat(inactiveCount).isEqualTo(1);
    }
}