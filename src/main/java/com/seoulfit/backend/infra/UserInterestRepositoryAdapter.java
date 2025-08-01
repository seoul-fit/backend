package com.seoulfit.backend.infra;

import com.seoulfit.backend.domain.port.UserInterestPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserInterest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사용자 관심사 Repository 어댑터
 * Hexagonal Architecture의 아웃바운드 어댑터
 */
@Repository
@RequiredArgsConstructor
public class UserInterestRepositoryAdapter implements UserInterestPort {
    
    private final UserInterestRepository userInterestRepository;
    
    @Override
    public UserInterest save(UserInterest userInterest) {
        return userInterestRepository.save(userInterest);
    }
    
    @Override
    public List<UserInterest> findByUser(User user) {
        return userInterestRepository.findByUser(user);
    }
    
    @Override
    public List<InterestCategory> findInterestCategoriesByUser(User user) {
        return userInterestRepository.findInterestCategoriesByUser(user);
    }
    
    @Override
    public void deleteByUser(User user) {
        userInterestRepository.deleteByUser(user);
    }
    
    @Override
    public List<UserInterest> saveAll(List<UserInterest> userInterests) {
        return userInterestRepository.saveAll(userInterests);
    }
}
