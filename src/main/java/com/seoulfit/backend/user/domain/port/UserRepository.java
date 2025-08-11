package com.seoulfit.backend.user.domain.port;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 도메인 Repository 포트
 * 
 * DDD의 Repository 패턴을 적용한 도메인 포트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface UserRepository {
    
    /**
     * 사용자 저장
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    User save(User user);
    
    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 Optional
     */
    Optional<User> findById(Long id);
    
    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 Optional
     */
    Optional<User> findByEmail(String email);
    
    /**
     * OAuth 정보로 사용자 조회
     * @param provider OAuth 프로바이더
     * @param oauthUserId OAuth ID
     * @return 사용자 Optional
     */
    Optional<User> findByAuthProviderAndOAuthUserId(AuthProvider provider, String oauthUserId);
    

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
     * @param provider OAuth 프로바이더
     * @param oauthUserId OAuth ID
     * @return 존재 여부
     */
    boolean existsByAuthProviderAndOAuthUserId(AuthProvider provider, String oauthUserId);
    
    /**
     * 모든 활성 사용자 조회
     * @return 활성 사용자 목록
     */
    List<User> findAllActiveUsers();
    
    /**
     * 특정 관심사를 가진 사용자 조회
     * @param interestCategory 관심사 카테고리
     * @return 해당 관심사를 가진 사용자 목록
     */
    List<User> findUsersByInterest(InterestCategory interestCategory);
    
    /**
     * 사용자 삭제
     * @param user 삭제할 사용자
     */
    void delete(User user);
}
