package com.seoulfit.backend.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * User 도메인 테스트 클래스입니다.
 * 
 * @author Seoul Fit
 * @since 2025-01-01
 */
@DisplayName("User 도메인 테스트")
class UserTest {

    @Nested
    @DisplayName("사용자 생성 테스트")
    class UserCreationTest {

        @Test
        @DisplayName("OAuth 사용자 생성 성공")
        void createOAuthUser_Success() {
            // when
            User user = User.createOAuthUser(
                AuthProvider.KAKAO,
                "12345",
                "테스트사용자",
                "test@example.com",
                "https://example.com/profile.jpg"
            );

            // then
            assertThat(user).isNotNull();
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getNickname()).isEqualTo("테스트사용자");
            assertThat(user.getProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("사용자 관심사 관리 테스트")
    class UserInterestTest {

        @Test
        @DisplayName("관심사 추가 성공")
        void addInterest_Success() {
            // given
            User user = User.createOAuthUser(
                AuthProvider.KAKAO,
                "12345",
                "테스트사용자",
                "test@example.com",
                "https://example.com/profile.jpg"
            );

            // when
            user.addInterest(InterestCategory.WEATHER);

            // then
            assertThat(user.getInterests()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("사용자 상태 관리 테스트")
    class UserStatusTest {

        @Test
        @DisplayName("사용자 비활성화 성공")
        void deactivateUser_Success() {
            // given
            User user = User.createOAuthUser(
                AuthProvider.KAKAO,
                "12345",
                "테스트사용자",
                "test@example.com",
                "https://example.com/profile.jpg"
            );

            // when
            user.deactivate();

            // then
            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }
    }
}
