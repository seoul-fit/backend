package com.seoulfit.backend.user.infrastructure.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.seoulfit.backend.user.domain.AuthProvider;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OAuthClientFactoryTest {

    private OAuthClient kakaoClient;
    private OAuthClientFactory factory;

    @BeforeEach
    void setUp() {
        kakaoClient = mock(OAuthClient.class);
        when(kakaoClient.supports(AuthProvider.KAKAO)).thenReturn(true);
        factory = new OAuthClientFactory(List.of(kakaoClient));
    }

    @Test
    void returnsKakaoClient() {
        assertThat(factory.getClient(AuthProvider.KAKAO)).isSameAs(kakaoClient);
    }

    @Test
    void rejectsUnsupportedProvider() {
        assertThatThrownBy(() -> factory.getClient(AuthProvider.GOOGLE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 OAuth 제공자입니다: GOOGLE");
    }
}
