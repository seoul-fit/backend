package com.seoulfit.backend.config;

import com.seoulfit.backend.user.adapter.out.persistence.UserRepository;
import com.seoulfit.backend.user.application.service.CustomOAuth2UserService;
import com.seoulfit.backend.user.infrastructure.jwt.JwtTokenProvider;
import com.seoulfit.backend.user.infrastructure.security.OAuth2LoginSuccessHandler;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;

/**
 * 테스트용 Security 설정
 * WebMvcTest에서 Security 관련 빈들이 필요한 경우 사용
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    JwtTokenProvider jwtTokenProvider() {
        return Mockito.mock(JwtTokenProvider.class);
    }

    @Bean
    UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    CustomOAuth2UserService customOAuth2UserService() {
        return Mockito.mock(CustomOAuth2UserService.class);
    }

    @Bean
    OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
        return Mockito.mock(OAuth2LoginSuccessHandler.class);
    }
}
