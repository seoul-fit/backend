package com.seoulfit.backend.application.service;

import com.seoulfit.backend.application.dto.OAuthUserInfo;
import com.seoulfit.backend.application.oauth.OAuthUserInfoExtractorFactory;
import com.seoulfit.backend.application.security.CustomUserDetails;
import com.seoulfit.backend.domain.OAuthProvider;
import com.seoulfit.backend.domain.User;
import com.seoulfit.backend.domain.UserStatus;
import com.seoulfit.backend.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuthUserInfoExtractorFactory extractorFactory;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception e) {
            log.error("OAuth2 user processing failed", e);
            throw new OAuth2AuthenticationException("OAuth2 user processing failed");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthProvider provider = OAuthProvider.fromRegistrationId(registrationId);
        
        // OAuth 사용자 정보 추출
        OAuthUserInfo userInfo = extractorFactory.getExtractor(provider)
                .extractUserInfo(oAuth2User);

        // 기존 사용자 조회 또는 신규 생성
        User user = userRepository.findByOauthProviderAndOauthIdAndStatus(
                        provider, userInfo.getOauthId(), UserStatus.ACTIVE)
                .orElseGet(() -> createNewUser(userInfo));

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private User createNewUser(OAuthUserInfo userInfo) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(userInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email already exists: " + userInfo.getEmail());
        }

        // 닉네임 중복 체크 및 고유 닉네임 생성
        String uniqueNickname = generateUniqueNickname(userInfo.getNickname());

        User newUser = User.createOAuthUser(
                userInfo.getEmail(),
                uniqueNickname,
                userInfo.getProvider(),
                userInfo.getOauthId(),
                userInfo.getProfileImageUrl()
        );

        User savedUser = userRepository.save(newUser);
        log.info("New OAuth user created: {} with provider: {}", 
                savedUser.getEmail(), userInfo.getProvider());
        
        return savedUser;
    }

    private String generateUniqueNickname(String baseNickname) {
        String nickname = baseNickname;
        int counter = 1;
        
        while (userRepository.existsByNickname(nickname)) {
            nickname = baseNickname + counter++;
        }
        
        return nickname;
    }
}
