package com.seoulfit.backend.user.infrastructure.dto;

import com.seoulfit.backend.user.domain.AuthProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfo {

    private String oauthId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private AuthProvider provider;
}
