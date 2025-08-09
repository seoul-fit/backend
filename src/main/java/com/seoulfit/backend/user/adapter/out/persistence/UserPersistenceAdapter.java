package com.seoulfit.backend.user.adapter.out.persistence;

import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserInterest;
import com.seoulfit.backend.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 영속성 어댑터
 * <p>
 * 헥사고날 아키텍처의 출력 어댑터
 * 사용자 데이터 접근을 담당
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

    private final UserJpaRepository userJpaRepository;
    private final UserInterestRepository userInterestRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByProviderAndOauthUserId(AuthProvider provider, String oauthUserId) {
        return userJpaRepository.findByOauthProviderAndOauthUserId(provider, oauthUserId);
    }

    @Override
    public boolean existsByProviderAndOauthUserId(AuthProvider provider, String oauthUserId) {
        return userJpaRepository.existsByOauthProviderAndOauthUserId(provider, oauthUserId);
    }

    @Override
    public Optional<User> findByOauthUserId(Long authUserId) {
        // 새로운 User 모델에서는 authUserId 필드가 제거되었으므로 
        // 이 메서드는 더 이상 사용되지 않음
        throw new UnsupportedOperationException("authUserId 필드가 제거되었습니다. findById를 사용하세요.");
    }

    @Override
    public List<User> findUsersByInterest(InterestCategory interestCategory) {
        return userJpaRepository.findUsersByInterest(interestCategory);
    }

    @Override
    public List<User> findAllActiveUsers() {
        return userJpaRepository.findByStatus(UserStatus.ACTIVE);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public long countByStatus(UserStatus status) {
        return userJpaRepository.countByStatus(status);
    }

    @Override
    public void saveUserInterests(Long userId, List<InterestCategory> interests) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        List<UserInterest> userInterests = interests.stream()
                .map(interest -> UserInterest.of(user, interest))
                .toList();
        
        userInterestRepository.saveAll(userInterests);
    }
}
