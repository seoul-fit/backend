package com.seoulfit.backend.user.application.port.out;

import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserStatus;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 출력 포트
 * <p>
 * 헥사고날 아키텍처의 출력 포트 사용자 데이터 접근을 위한 인터페이스
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface UserPort {

    /**
     * 사용자 저장
     *
     * @param user 사용자
     * @return 저장된 사용자
     */
    User save(User user);

    /**
     * ID로 사용자 조회
     *
     * @param id 사용자 ID
     * @return 사용자
     */
    Optional<User> findById(Long id);

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
     * OAuth 제공자와 사용자 ID로 사용자 조회
     *
     * @param provider OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @return 사용자
     */
    Optional<User> findByProviderAndOauthUserId(AuthProvider provider, String oauthUserId);

    /**
     * OAuth 사용자 존재 여부 확인
     *
     * @param provider OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @return 존재 여부
     */
    boolean existsByProviderAndOauthUserId(AuthProvider provider, String oauthUserId);

    /**
     * 인증 사용자 ID로 사용자 조회
     *
     * @param authUserId 인증 사용자 ID
     * @return 사용자
     */
    Optional<User> findByOauthUserId(Long authUserId);

    /**
     * 관심사별 사용자 목록 조회
     *
     * @param interestCategory 관심사 카테고리
     * @return 사용자 목록
     */
    List<User> findUsersByInterest(InterestCategory interestCategory);

    /**
     * 활성 사용자 목록 조회
     *
     * @return 활성 사용자 목록
     */
    List<User> findAllActiveUsers();

    /**
     * 사용자 삭제
     *
     * @param id 사용자 ID
     */
    void deleteById(Long id);

    /**
     * 상태별 사용자 수 조회
     *
     * @param status 사용자 상태
     * @return 사용자 수
     */
    long countByStatus(UserStatus status);

    /**
     * 사용자 관심사 저장
     *
     * @param userId 사용자 ID
     * @param interests 관심사 목록
     */
    void saveUserInterests(Long userId, List<InterestCategory> interests);
}
