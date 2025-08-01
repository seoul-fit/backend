package com.seoulfit.backend.infra;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 사용자 관심사 Repository
 */
public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    
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
    @Query("SELECT ui.interestCategory FROM UserInterest ui WHERE ui.user = :user")
    List<InterestCategory> findInterestCategoriesByUser(@Param("user") User user);
    
    /**
     * 사용자의 모든 관심사 삭제
     * @param user 사용자
     */
    void deleteByUser(User user);
}
