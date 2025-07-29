package com.seoulfit.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password; // OAuth 사용자는 null 가능

    @Column(nullable = false, length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    // OAuth 관련 필드
    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInterest> interests = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Builder
    public User(String email, String password, String nickname, OAuthProvider oauthProvider, 
                String oauthId, String profileImageUrl) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.profileImageUrl = profileImageUrl;
    }

    // 일반 회원가입용 생성자
    public static User createLocalUser(String email, String password, String nickname) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    // OAuth 회원가입용 생성자
    public static User createOAuthUser(String email, String nickname, OAuthProvider provider, 
                                     String oauthId, String profileImageUrl) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .oauthProvider(provider)
                .oauthId(oauthId)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    // OAuth 사용자 조회를 위한 메서드
    public static User findByOAuthProviderAndOAuthId(OAuthProvider provider, String oauthId) {
        // 이 메서드는 실제로는 Repository에서 구현되어야 하지만, 
        // 비즈니스 로직의 명확성을 위해 여기에 정의
        return null;
    }

    // OAuth 사용자 여부 확인 (provider와 oauthId로)
    public boolean matchesOAuthUser(OAuthProvider provider, String oauthId) {
        return this.oauthProvider == provider && 
               this.oauthId != null && 
               this.oauthId.equals(oauthId);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void addInterest(InterestCategory category) {
        UserInterest interest = UserInterest.builder()
                .user(this)
                .interestCategory(category)
                .build();
        this.interests.add(interest);
    }

    public void updateInterests(List<InterestCategory> categories) {
        this.interests.clear();
        categories.forEach(this::addInterest);
    }

    public void delete() {
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isOAuthUser() {
        return this.oauthProvider != null;
    }

    public boolean isLocalUser() {
        return this.oauthProvider == null;
    }
}
