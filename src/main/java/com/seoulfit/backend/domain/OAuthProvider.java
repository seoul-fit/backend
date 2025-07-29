package com.seoulfit.backend.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {
    KAKAO("kakao", "카카오"),
    GOOGLE("google", "구글"),
    NAVER("naver", "네이버"),
    APPLE("apple", "애플");

    private final String key;
    private final String value;

    public static OAuthProvider fromRegistrationId(String registrationId) {
        for (OAuthProvider provider : values()) {
            if (provider.getKey().equals(registrationId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown OAuth provider: " + registrationId);
    }

    public static OAuthProvider findByKey(String key) {
        for (OAuthProvider provider : values()) {
            if (provider.getKey().equals(key)) {
                return provider;
            }
        }
        return null;
    }

    public static OAuthProvider findByValue(String value) {
        for (OAuthProvider provider : values()) {
            if (provider.getValue().equals(value)) {
                return provider;
            }
        }
        return null;
    }

    // 기존 메서드와의 호환성을 위해 유지
    public String getRegistrationId() {
        return this.key;
    }
}
