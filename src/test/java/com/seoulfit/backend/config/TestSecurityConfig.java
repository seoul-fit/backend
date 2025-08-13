package com.seoulfit.backend.config;

import com.seoulfit.backend.user.adapter.out.persistence.UserRepository;
import com.seoulfit.backend.user.infrastructure.jwt.JwtTokenProvider;
import com.seoulfit.backend.user.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * 테스트용 Security 설정
 * WebMvcTest에서 Security 관련 빈들이 필요한 경우 사용
 */
@TestConfiguration
public class TestSecurityConfig {

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    
    @MockitoBean
    private UserRepository userRepository;
    
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
}
