package com.seoulfit.backend.user.adapter.in.web.dto;

import com.seoulfit.backend.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String provider;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl() != null ? user.getProfileImageUrl() : "")
                .provider(user.isOAuthUser() ? user.getOauthProvider().name() : "LOCAL")
                .build();
    }
}
