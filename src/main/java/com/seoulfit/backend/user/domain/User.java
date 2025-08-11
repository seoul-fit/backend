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
 * 사용자 도메인 모델을 나타내는 엔티티 클래스입니다.
 * 
 * <p>사용자 프로필과 관심사 관리를 담당하는 Aggregate Root로서, 
 * 일반 사용자와 OAuth 사용자 모두를 지원합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 기본 정보 관리 (이메일, 닉네임, 프로필 이미지)</li>
 *   <li>OAuth 인증 정보 관리 (카카오, 구글 등)</li>
 *   <li>위치 정보 관리 (위도, 경도, 주소)</li>
 *   <li>관심사 카테고리 관리</li>
 *   <li>사용자 상태 관리 (활성, 비활성, 삭제)</li>
 * </ul>
 * 
 * <p>이 클래스는 JPA 엔티티로서 데이터베이스의 users 테이블과 매핑되며,
 * JPA Auditing을 통해 생성일시와 수정일시가 자동으로 관리됩니다.</p>
 * 
 * @author Seoul Fit
 * @version 1.0.0
 * @since 2025-01-01
 * @see UserInterest
 * @see AuthProvider
 * @see UserStatus
 * @see InterestCategory
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    /**
     * 사용자의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자의 이메일 주소입니다.
     * 일반 사용자의 경우 인증에 사용되며, 고유해야 합니다.
     */
    @Column(unique = true)
    private String email;

    /**
     * 사용자의 비밀번호입니다.
     * 일반 사용자의 경우에만 사용되며, 암호화된 상태로 저장됩니다.
     */
    @Column
    private String password;

    /**
     * 사용자의 닉네임입니다.
     * 필수 항목이며, 애플리케이션 내에서 사용자를 식별하는 데 사용됩니다.
     */
    @Column(nullable = false)
    private String nickname;

    /**
     * OAuth 인증 제공자입니다.
     * OAuth 사용자의 경우에만 설정됩니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider")
    private AuthProvider oauthProvider;

    /**
     * OAuth 제공자에서 발급한 사용자 고유 ID입니다.
     */
    @Column(name = "oauth_user_id")
    private String oauthUserId;

    /**
     * OAuth 제공자로부터 받은 액세스 토큰입니다.
     * 로그아웃이나 연결 해제 시 사용됩니다.
     */
    @Column(name = "oauth_access_token", length = 2000)
    private String oauthAccessToken;

    /**
     * OAuth 액세스 토큰의 만료 시간입니다.
     */
    @Column(name = "oauth_token_expires_at")
    private LocalDateTime oauthTokenExpiresAt;

    /**
     * 사용자의 프로필 이미지 URL입니다.
     */
    @Column(name = "profile_image_url")
    private String profileImageUrl;

    /**
     * 사용자 위치의 위도 정보입니다.
     */
    @Column(name = "location_latitude")
    private Double locationLatitude;

    /**
     * 사용자 위치의 경도 정보입니다.
     */
    @Column(name = "location_longitude")
    private Double locationLongitude;

    /**
     * 사용자 위치의 주소 정보입니다.
     */
    @Column(name = "location_address")
    private String locationAddress;

    /**
     * 사용자의 현재 상태입니다.
     * 기본값은 ACTIVE입니다.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 사용자의 관심사 목록입니다.
     * 지연 로딩으로 설정되어 있으며, 사용자가 삭제되면 함께 삭제됩니다.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserInterest> interests = new ArrayList<>();

    /**
     * 사용자 계정 생성 일시입니다.
     * JPA Auditing에 의해 자동으로 설정됩니다.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 사용자 정보 최종 수정 일시입니다.
     * JPA Auditing에 의해 자동으로 업데이트됩니다.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * User 엔티티의 빌더 생성자입니다.
     * 
     * @param email 이메일 주소
     * @param password 암호화된 비밀번호
     * @param nickname 닉네임
     * @param oauthProvider OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @param oauthAccessToken OAuth 액세스 토큰
     * @param oauthTokenExpiresAt OAuth 토큰 만료 시간
     * @param profileImageUrl 프로필 이미지 URL
     * @param locationLatitude 위치 위도
     * @param locationLongitude 위치 경도
     * @param locationAddress 위치 주소
     * @param status 사용자 상태
     */
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
     * 일반 사용자를 생성합니다.
     *
     * @param email 이메일 주소
     * @param password 암호화된 비밀번호
     * @param nickname 닉네임
     * @return 생성된 일반 사용자 인스턴스
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
     * OAuth 사용자를 생성합니다.
     *
     * @param provider OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @param nickname 닉네임
     * @param email 이메일 주소 (선택사항)
     * @param profileImageUrl 프로필 이미지 URL (선택사항)
     * @return 생성된 OAuth 사용자 인스턴스
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
     * 토큰 정보를 포함한 OAuth 사용자를 생성합니다.
     *
     * @param provider OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @param nickname 닉네임
     * @param email 이메일 주소 (선택사항)
     * @param profileImageUrl 프로필 이미지 URL (선택사항)
     * @param accessToken OAuth 액세스 토큰
     * @param expiresAt 토큰 만료 시간
     * @return 생성된 OAuth 사용자 인스턴스
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
     * 기본 정보로 새 사용자를 생성합니다.
     *
     * @param nickname 닉네임
     * @param profileImageUrl 프로필 이미지 URL
     * @return 생성된 사용자 인스턴스
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
     * 사용자의 관심사를 추가합니다.
     * 이미 존재하는 관심사인 경우 무시됩니다.
     *
     * @param interestCategory 추가할 관심사 카테고리
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
     * 사용자의 관심사를 제거합니다.
     *
     * @param interestCategory 제거할 관심사 카테고리
     */
    public void removeInterest(InterestCategory interestCategory) {
        this.interests.removeIf(interest ->
                interest.getInterestCategory().equals(interestCategory));
    }

    /**
     * 사용자의 모든 관심사를 제거합니다.
     */
    public void clearInterests() {
        this.interests.clear();
    }

    /**
     * 사용자의 관심사 목록을 업데이트합니다.
     * 기존 관심사는 모두 제거되고 새로운 관심사로 대체됩니다.
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
     * 특정 관심사를 보유하고 있는지 확인합니다.
     *
     * @param interestCategory 확인할 관심사 카테고리
     * @return 관심사 보유 여부
     */
    public boolean hasInterest(InterestCategory interestCategory) {
        return this.interests.stream()
                .anyMatch(interest -> interest.getInterestCategory().equals(interestCategory));
    }

    /**
     * 사용자의 관심사 카테고리 목록을 반환합니다.
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
     * 사용자를 활성 상태로 변경합니다.
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * 사용자를 비활성 상태로 변경합니다.
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * 사용자를 삭제 상태로 변경합니다.
     */
    public void delete() {
        this.status = UserStatus.DELETED;
    }

    /**
     * 사용자가 활성 상태인지 확인합니다.
     *
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // ===== 프로필 관리 메서드들 =====

    /**
     * 사용자의 프로필 정보를 업데이트합니다.
     *
     * @param nickname 새 닉네임 (null이 아닌 경우에만 업데이트)
     * @param profileImageUrl 새 프로필 이미지 URL (null이 아닌 경우에만 업데이트)
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
     * 사용자의 닉네임을 업데이트합니다.
     * 
     * @param nickname 새 닉네임
     */
    public void updateNickname(String nickname) {
        updateProfile(nickname, null);
    }

    /**
     * 사용자의 비밀번호를 업데이트합니다.
     * OAuth 사용자의 경우 무시됩니다.
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
     * 사용자의 위치 정보를 업데이트합니다.
     *
     * @param latitude 위도
     * @param longitude 경도
     * @param address 주소
     */
    public void updateLocation(Double latitude, Double longitude, String address) {
        this.locationLatitude = latitude;
        this.locationLongitude = longitude;
        this.locationAddress = address;
    }

    /**
     * 사용자가 위치 정보를 보유하고 있는지 확인합니다.
     *
     * @return 위치 정보 보유 여부
     */
    public boolean hasLocation() {
        return this.locationLatitude != null && this.locationLongitude != null;
    }

    // ===== OAuth 관련 메서드들 =====

    /**
     * OAuth 사용자인지 확인합니다.
     *
     * @return OAuth 사용자 여부
     */
    public boolean isOAuthUser() {
        return this.oauthProvider != null && this.oauthUserId != null;
    }

    /**
     * OAuth 제공자를 반환합니다.
     *
     * @return OAuth 제공자
     */
    public AuthProvider getOauthProvider() {
        return this.oauthProvider;
    }

    /**
     * 주어진 OAuth 정보와 일치하는지 확인합니다.
     *
     * @param provider OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @return 일치 여부
     */
    public boolean matchesOAuth(AuthProvider provider, String oauthUserId) {
        return this.oauthProvider == provider && 
               this.oauthUserId != null && 
               this.oauthUserId.equals(oauthUserId);
    }

    /**
     * OAuth 액세스 토큰을 업데이트합니다.
     *
     * @param accessToken OAuth 액세스 토큰
     * @param expiresAt 토큰 만료 시간
     * @throws IllegalStateException OAuth 사용자가 아닌 경우
     */
    public void updateOAuthToken(String accessToken, LocalDateTime expiresAt) {
        if (!isOAuthUser()) {
            throw new IllegalStateException("OAuth 사용자가 아닙니다.");
        }
        this.oauthAccessToken = accessToken;
        this.oauthTokenExpiresAt = expiresAt;
    }

    /**
     * OAuth 액세스 토큰을 제거합니다.
     * 로그아웃 시 사용됩니다.
     */
    public void clearOAuthToken() {
        this.oauthAccessToken = null;
        this.oauthTokenExpiresAt = null;
    }

    /**
     * OAuth 액세스 토큰을 보유하고 있는지 확인합니다.
     *
     * @return 토큰 보유 여부
     */
    public boolean hasOAuthToken() {
        return this.oauthAccessToken != null;
    }

    /**
     * OAuth 액세스 토큰이 만료되었는지 확인합니다.
     *
     * @return 토큰 만료 여부
     */
    public boolean isOAuthTokenExpired() {
        return this.oauthTokenExpiresAt != null && 
               this.oauthTokenExpiresAt.isBefore(LocalDateTime.now());
    }

    /**
     * 유효한 OAuth 액세스 토큰을 보유하고 있는지 확인합니다.
     *
     * @return 유효한 토큰 보유 여부
     */
    public boolean hasValidOAuthToken() {
        return hasOAuthToken() && !isOAuthTokenExpired();
    }

    // ===== 검증 메서드들 =====

    /**
     * 사용자 정보의 유효성을 검증합니다.
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
     * 사용자의 표시명을 반환합니다.
     * 닉네임이 있으면 닉네임을, 없으면 이메일을 반환합니다.
     *
     * @return 사용자 표시명
     */
    public String getDisplayName() {
        return nickname != null ? nickname : email;
    }

    /**
     * 사용자의 요약 정보를 문자열로 반환합니다.
     * 디버깅이나 로깅 목적으로 사용됩니다.
     *
     * @return 사용자 요약 정보
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
