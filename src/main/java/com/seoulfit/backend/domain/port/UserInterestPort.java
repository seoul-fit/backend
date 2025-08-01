package com.seoulfit.backend.domain.port;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserInterest;
import java.util.List;

/**
 * 사용자 관심사 도메인 포트
 * Hexagonal Architecture의 아웃바운드 포트
 */
public interface UserInterestPort {
    
    /**
     * 사용자 관심사 저장
     * @param userInterest 저장할 사용자 관심사
     * @return 저장된 사용자 관심사
     */
    UserInterest save(UserInterest userInterest);
    
    /**
     * 사용자의 모든 관심사 조회
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
     * 사용자의 모든 관심사 삭제
     * @param user 사용자
     */
    void deleteByUser(User user);
    
    /**
     * 사용자 관심사 일괄 저장
     * @param userInterests 저장할 사용자 관심사 목록
     * @return 저장된 사용자 관심사 목록
     */
    List<UserInterest> saveAll(List<UserInterest> userInterests);
}
