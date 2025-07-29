package com.seoulfit.backend.application.dto;

import com.seoulfit.backend.domain.OAuthProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfo {
    private String oauthId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private OAuthProvider provider;
}
