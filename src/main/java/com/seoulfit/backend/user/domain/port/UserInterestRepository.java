package com.seoulfit.backend.user.domain.port;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserInterest;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 관심사 도메인 Repository 포트
 * 
 * DDD의 Repository 패턴을 적용한 도메인 포트
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface UserInterestRepository {
    
    /**
     * 사용자 관심사 저장
     * @param userInterest 저장할 사용자 관심사
     * @return 저장된 사용자 관심사
     */
    UserInterest save(UserInterest userInterest);
    
    /**
     * 사용자 관심사 목록 저장
     * @param userInterests 저장할 사용자 관심사 목록
     * @return 저장된 사용자 관심사 목록
     */
    List<UserInterest> saveAll(List<UserInterest> userInterests);
    
    /**
     * 사용자의 관심사 목록 조회
     * @param user 사용자
     * @return 사용자 관심사 목록
     */
    List<UserInterest> findByUser(User user);
    
    /**
     * 사용자의 관심사 카테고리 목록 조회
     * @param user 사용자
     * @return 관심사 카테고리 목록
     */
    List<InterestCategory> findInterestCategoriesByUser(User user);
    
    /**
     * 사용자의 특정 관심사 조회
     * @param user 사용자
     * @param interestCategory 관심사 카테고리
     * @return 사용자 관심사 Optional
     */
    Optional<UserInterest> findByUserAndInterestCategory(User user, InterestCategory interestCategory);
    
    /**
     * 사용자의 모든 관심사 삭제
     * @param user 사용자
     */
    void deleteByUser(User user);
    
    /**
     * 사용자의 특정 관심사 삭제
     * @param user 사용자
     * @param interestCategory 관심사 카테고리
     */
    void deleteByUserAndInterestCategory(User user, InterestCategory interestCategory);
    
    /**
     * 사용자 관심사 존재 여부 확인
     * @param user 사용자
     * @param interestCategory 관심사 카테고리
     * @return 존재 여부
     */
    boolean existsByUserAndInterestCategory(User user, InterestCategory interestCategory);
}
