package com.seoulfit.backend.user.adapter.out.persistence;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.User;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 도메인 포트 Hexagonal Architecture의 아웃바운드 포트
 */
public interface UserPort {

    /**
     * 사용자 저장
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User save(User user);

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 사용자 Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * OAuth 정보로 사용자 조회
     *
     * @param provider OAuth 프로바이더
     * @param oauthId  OAuth ID
     * @return 사용자 Optional
     */
    Optional<User> findByAuthProviderAndOAuthUserId(AuthProvider provider, String oauthId);

    /**
     * ID로 사용자 조회
     *
     * @param id 사용자 ID
     * @return 사용자 Optional
     */
    Optional<User> findById(Long id);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * OAuth 사용자 존재 여부 확인
     *
     * @param provider OAuth 프로바이더
     * @param oauthId  OAuth ID
     * @return 존재 여부
     */
    boolean existsByAuthProviderAndOAuthUserId(AuthProvider provider, String oauthId);

    /**
     * 모든 활성 사용자 조회
     *
     * @return 활성 사용자 목록
     */
    List<User> findAllActiveUsers();

    /**
     * 특정 관심사를 가진 사용자 조회
     *
     * @param interestCategory 관심사 카테고리
     * @return 해당 관심사를 가진 사용자 목록
     */
    List<User> findUsersByInterest(InterestCategory interestCategory);
}
