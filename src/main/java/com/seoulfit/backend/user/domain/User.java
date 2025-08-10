package com.seoulfit.backend.user.domain;

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

/**
 * 사용자 도메인 모델
 * <p>
 * 사용자 프로필과 관심사 관리를 담당하는 Aggregate Root
 * 인증 정보는 AuthUser에서 분리하여 관리하거나, 단순한 경우 직접 포함
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이메일 (인증용)
     */
    @Column(unique = true)
    private String email;

    /**
     * 비밀번호 (암호화된 상태)
     */
    @Column
    private String password;

    /**
     * 닉네임
     */
    @Column(nullable = false)
    private String nickname;

    /**
     * OAuth 프로바이더
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider")
    private AuthProvider oauthProvider;

    /**
     * OAuth 사용자 ID
     */
    @Column(name = "oauth_user_id")
    private String oauthUserId;

    /**
     * OAuth Provider AccessToken (로그아웃, 연결 해제용)
     */
    @Column(name = "oauth_access_token", length = 2000)
    private String oauthAccessToken;

    /**
     * OAuth AccessToken 만료 시간
     */
    @Column(name = "oauth_token_expires_at")
    private LocalDateTime oauthTokenExpiresAt;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    /**
     * 위치 정보 - 위도
     */
    @Column(name = "location_latitude")
    private Double locationLatitude;

    /**
     * 위치 정보 - 경도
     */
    @Column(name = "location_longitude")
    private Double locationLongitude;

    /**
     * 위치 정보 - 주소
     */
    @Column(name = "location_address")
    private String locationAddress;

    /**
     * 사용자 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 사용자 관심사 목록
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserInterest> interests = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(String email, String password, String nickname, AuthProvider oauthProvider, 
                String oauthUserId, String oauthAccessToken, LocalDateTime oauthTokenExpiresAt, 
                String profileImageUrl, Double locationLatitude, 
                Double locationLongitude, String locationAddress, UserStatus status) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.oauthProvider = oauthProvider;
        this.oauthUserId = oauthUserId;
        this.oauthAccessToken = oauthAccessToken;
        this.oauthTokenExpiresAt = oauthTokenExpiresAt;
        this.profileImageUrl = profileImageUrl;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationAddress = locationAddress;
        this.status = status != null ? status : UserStatus.ACTIVE;
    }

    // ===== 정적 팩토리 메서드들 =====

    /**
     * 일반 사용자 생성
     *
     * @param email    이메일
     * @param password 암호화된 비밀번호
     * @param nickname 닉네임
     * @return 일반 사용자
     */
    public static User createLocalUser(String email, String password, String nickname) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .status(UserStatus.ACTIVE)
                .build();
    }

    /**
     * OAuth 사용자 생성
     *
     * @param provider        OAuth 프로바이더
     * @param oauthUserId     OAuth 사용자 ID
     * @param nickname        닉네임
     * @param email           이메일 (선택사항)
     * @param profileImageUrl 프로필 이미지 URL (선택사항)
     * @return OAuth 사용자
     */
    public static User createOAuthUser(AuthProvider provider, String oauthUserId, String nickname,
                                      String email, String profileImageUrl) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .oauthProvider(provider)
                .oauthUserId(oauthUserId)
                .profileImageUrl(profileImageUrl)
                .status(UserStatus.ACTIVE)
                .build();
    }

    /**
     * OAuth 사용자 생성 (토큰 정보 포함)
     *
     * @param provider        OAuth 프로바이더
     * @param oauthUserId     OAuth 사용자 ID
     * @param nickname        닉네임
     * @param email           이메일 (선택사항)
     * @param profileImageUrl 프로필 이미지 URL (선택사항)
     * @param accessToken     OAuth Provider AccessToken
     * @param expiresAt       토큰 만료 시간
     * @return OAuth 사용자
     */
    public static User createOAuthUserWithToken(AuthProvider provider, String oauthUserId, String nickname,
                                               String email, String profileImageUrl, String accessToken, LocalDateTime expiresAt) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .oauthProvider(provider)
                .oauthUserId(oauthUserId)
                .oauthAccessToken(accessToken)
                .oauthTokenExpiresAt(expiresAt)
                .profileImageUrl(profileImageUrl)
                .status(UserStatus.ACTIVE)
                .build();
    }

    /**
     * 새 사용자 생성 (기본)
     *
     * @param nickname        닉네임
     * @param profileImageUrl 프로필 이미지 URL
     * @return 새 사용자
     */
    public static User create(String nickname, String profileImageUrl) {
        return User.builder()
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .status(UserStatus.ACTIVE)
                .build();
    }

    // ===== 관심사 관리 메서드들 =====

    /**
     * 사용자 관심사 추가
     *
     * @param interestCategory 관심사 카테고리
     */
    public void addInterest(InterestCategory interestCategory) {
        // 이미 존재하는 관심사인지 확인
        if (hasInterest(interestCategory)) {
            return;
        }
        
        UserInterest userInterest = UserInterest.builder()
                .user(this)
                .interestCategory(interestCategory)
                .build();
        this.interests.add(userInterest);
    }

    /**
     * 사용자 관심사 제거
     *
     * @param interestCategory 관심사 카테고리
     */
    public void removeInterest(InterestCategory interestCategory) {
        this.interests.removeIf(interest ->
                interest.getInterestCategory().equals(interestCategory));
    }

    /**
     * 모든 관심사 제거
     */
    public void clearInterests() {
        this.interests.clear();
    }

    /**
     * 관심사 목록 업데이트
     *
     * @param newInterests 새로운 관심사 목록
     */
    public void updateInterests(List<InterestCategory> newInterests) {
        clearInterests();
        if (newInterests != null) {
            newInterests.forEach(this::addInterest);
        }
    }

    /**
     * 특정 관심사 보유 여부 확인
     *
     * @param interestCategory 관심사 카테고리
     * @return 보유 여부
     */
    public boolean hasInterest(InterestCategory interestCategory) {
        return this.interests.stream()
                .anyMatch(interest -> interest.getInterestCategory().equals(interestCategory));
    }

    /**
     * 관심사 카테고리 목록 반환
     *
     * @return 관심사 카테고리 목록
     */
    public List<InterestCategory> getInterestCategories() {
        return this.interests.stream()
                .map(UserInterest::getInterestCategory)
                .toList();
    }

    // ===== 상태 관리 메서드들 =====

    /**
     * 사용자 활성화
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * 사용자 비활성화
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * 사용자 삭제 처리
     */
    public void delete() {
        this.status = UserStatus.DELETED;
    }

    /**
     * 활성 사용자 여부 확인
     *
     * @return 활성 여부
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // ===== 프로필 관리 메서드들 =====

    /**
     * 프로필 정보 업데이트
     *
     * @param nickname        새 닉네임
     * @param profileImageUrl 새 프로필 이미지 URL
     */
    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null && !nickname.trim().isEmpty()) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    /**
     * 닉네임 업데이트
     * 
     * @param nickname 새 닉네임
     */
    public void updateNickname(String nickname) {
        updateProfile(nickname, null);
    }

    /**
     * 비밀번호 업데이트 (일반 사용자만)
     *
     * @param newPassword 새 암호화된 비밀번호
     */
    public void updatePassword(String newPassword) {
        if (!isOAuthUser()) {
            this.password = newPassword;
        }
    }

    // ===== 위치 관리 메서드들 =====

    /**
     * 위치 정보 업데이트
     *
     * @param latitude  위도
     * @param longitude 경도
     * @param address   주소
     */
    public void updateLocation(Double latitude, Double longitude, String address) {
        this.locationLatitude = latitude;
        this.locationLongitude = longitude;
        this.locationAddress = address;
    }

    /**
     * 위치 정보 보유 여부 확인
     *
     * @return 위치 정보 보유 여부
     */
    public boolean hasLocation() {
        return this.locationLatitude != null && this.locationLongitude != null;
    }

    // ===== OAuth 관련 메서드들 =====

    /**
     * OAuth 사용자 여부 확인
     *
     * @return OAuth 사용자 여부
     */
    public boolean isOAuthUser() {
        return this.oauthProvider != null && this.oauthUserId != null;
    }

    /**
     * OAuth 프로바이더 반환
     *
     * @return OAuth 프로바이더
     */
    public AuthProvider getOauthProvider() {
        return this.oauthProvider;
    }

    /**
     * OAuth 정보로 사용자 매칭 확인
     *
     * @param provider    OAuth 프로바이더
     * @param oauthUserId OAuth 사용자 ID
     * @return 매칭 여부
     */
    public boolean matchesOAuth(AuthProvider provider, String oauthUserId) {
        return this.oauthProvider == provider && 
               this.oauthUserId != null && 
               this.oauthUserId.equals(oauthUserId);
    }

    /**
     * OAuth AccessToken 업데이트
     *
     * @param accessToken OAuth Provider AccessToken
     * @param expiresAt   토큰 만료 시간
     */
    public void updateOAuthToken(String accessToken, LocalDateTime expiresAt) {
        if (!isOAuthUser()) {
            throw new IllegalStateException("OAuth 사용자가 아닙니다.");
        }
        this.oauthAccessToken = accessToken;
        this.oauthTokenExpiresAt = expiresAt;
    }

    /**
     * OAuth AccessToken 제거 (로그아웃 시)
     */
    public void clearOAuthToken() {
        this.oauthAccessToken = null;
        this.oauthTokenExpiresAt = null;
    }

    /**
     * OAuth AccessToken 보유 여부 확인
     *
     * @return 토큰 보유 여부
     */
    public boolean hasOAuthToken() {
        return this.oauthAccessToken != null;
    }

    /**
     * OAuth AccessToken 만료 여부 확인
     *
     * @return 만료 여부
     */
    public boolean isOAuthTokenExpired() {
        return this.oauthTokenExpiresAt != null && 
               this.oauthTokenExpiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * 유효한 OAuth AccessToken 보유 여부 확인
     *
     * @return 유효한 토큰 보유 여부
     */
    public boolean hasValidOAuthToken() {
        return hasOAuthToken() && !isOAuthTokenExpired();
    }

    // ===== 검증 메서드들 =====

    /**
     * 사용자 정보 유효성 검증
     *
     * @throws IllegalStateException 유효하지 않은 상태인 경우
     */
    public void validate() {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalStateException("닉네임은 필수입니다.");
        }
        
        if (isOAuthUser()) {
            if (oauthProvider == null || oauthUserId == null) {
                throw new IllegalStateException("OAuth 사용자는 프로바이더와 사용자 ID가 필요합니다.");
            }
        } else {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalStateException("일반 사용자는 이메일이 필요합니다.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalStateException("일반 사용자는 비밀번호가 필요합니다.");
            }
        }
    }

    // ===== 편의 메서드들 =====

    /**
     * 사용자 표시명 반환 (닉네임 우선, 없으면 이메일)
     *
     * @return 표시명
     */
    public String getDisplayName() {
        return nickname != null ? nickname : email;
    }

    /**
     * 사용자 요약 정보 반환
     *
     * @return 요약 정보
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("User{id=").append(id);
        sb.append(", nickname='").append(nickname).append('\'');
        if (email != null) {
            sb.append(", email='").append(email).append('\'');
        }
        if (isOAuthUser()) {
            sb.append(", oauth=").append(oauthProvider);
        }
        sb.append(", status=").append(status);
        sb.append(", interests=").append(getInterestCategories().size());
        sb.append('}');
        return sb.toString();
    }
}
