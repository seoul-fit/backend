package com.seoulfit.backend.user.adapter.out.persistence;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 JPA Repository
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface UserJpaRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 사용자
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * OAuth 프로바이더와 사용자 ID로 사용자 조회
     *
     * @param oauthProvider OAuth 프로바이더
     * @param oauthUserId   OAuth 사용자 ID
     * @return 사용자
     */
    Optional<User> findByOauthProviderAndOauthUserId(AuthProvider oauthProvider, String oauthUserId);

    /**
     * OAuth 사용자 존재 여부 확인
     *
     * @param oauthProvider OAuth 프로바이더
     * @param oauthUserId   OAuth 사용자 ID
     * @return 존재 여부
     */
    boolean existsByOauthProviderAndOauthUserId(AuthProvider oauthProvider, String oauthUserId);

    /**
     * 상태별 사용자 목록 조회
     *
     * @param status 사용자 상태
     * @return 사용자 목록
     */
    List<User> findByStatus(UserStatus status);

    /**
     * 관심사별 사용자 목록 조회
     *
     * @param interestCategory 관심사 카테고리
     * @return 사용자 목록
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.interests ui WHERE ui.interestCategory = :interestCategory AND u.status = 'ACTIVE'")
    List<User> findUsersByInterest(@Param("interestCategory") InterestCategory interestCategory);

    /**
     * 상태별 사용자 수 조회
     *
     * @param status 사용자 상태
     * @return 사용자 수
     */
    long countByStatus(UserStatus status);

    /**
     * 활성 사용자 중 관심사별 사용자 수 조회
     *
     * @param interestCategory 관심사 카테고리
     * @return 사용자 수
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.interests ui WHERE ui.interestCategory = :interestCategory AND u.status = 'ACTIVE'")
    long countActiveUsersByInterest(@Param("interestCategory") InterestCategory interestCategory);

    /**
     * OAuth 프로바이더별 사용자 목록 조회
     *
     * @param oauthProvider OAuth 프로바이더
     * @return 사용자 목록
     */
    List<User> findByOauthProvider(AuthProvider oauthProvider);

    /**
     * 활성 OAuth 사용자 목록 조회
     *
     * @return OAuth 사용자 목록
     */
    @Query("SELECT u FROM User u WHERE u.oauthProvider IS NOT NULL AND u.status = 'ACTIVE'")
    List<User> findActiveOAuthUsers();

    /**
     * 활성 일반 사용자 목록 조회
     *
     * @return 일반 사용자 목록
     */
    @Query("SELECT u FROM User u WHERE u.oauthProvider IS NULL AND u.status = 'ACTIVE'")
    List<User> findActiveLocalUsers();
}
