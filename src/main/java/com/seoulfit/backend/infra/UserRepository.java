package com.seoulfit.backend.infra;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 Repository
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일과 상태로 사용자 조회
     * @param email 이메일
     * @param status 사용자 상태
     * @return 사용자 Optional
     */
    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    /**
     * OAuth 정보로 사용자 조회
     * @param oauthProvider OAuth 프로바이더
     * @param oauthUserId OAuth ID
     * @return 사용자 Optional
     */
    Optional<User> findByOauthProviderAndOauthUserId(AuthProvider oauthProvider, String oauthUserId);

    /**
     * OAuth 정보와 상태로 사용자 조회
     * @param oauthProvider OAuth 프로바이더
     * @param oauthUserId OAuth ID
     * @param status 사용자 상태
     * @return 사용자 Optional
     */
    Optional<User> findByOauthProviderAndOauthUserIdAndStatus(AuthProvider oauthProvider, String oauthUserId, UserStatus status);

    /**
     * 이메일 존재 여부 확인
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 존재 여부 확인
     * @param nickname 닉네임
     * @return 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * OAuth 사용자 존재 여부 확인
     * @param oauthProvider OAuth 프로바이더
     * @param oauthUserId OAuth ID
     * @return 존재 여부
     */
    boolean existsByOauthProviderAndOauthUserId(AuthProvider oauthProvider, String oauthUserId);

    /**
     * 사용자 ID로 관심사와 함께 조회
     * @param userId 사용자 ID
     * @return 사용자 Optional
     */
    @Query("SELECT u FROM User u JOIN FETCH u.interests WHERE u.id = :userId AND u.status = 'ACTIVE'")
    Optional<User> findByIdWithInterests(@Param("userId") Long userId);
    
    /**
     * 특정 관심사를 가진 사용자 조회
     * @param interestCategory 관심사 카테고리
     * @return 해당 관심사를 가진 사용자 목록
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.interests ui WHERE ui.interestCategory = :interestCategory AND u.status = 'ACTIVE'")
    List<User> findUsersByInterestCategory(@Param("interestCategory") InterestCategory interestCategory);
}
