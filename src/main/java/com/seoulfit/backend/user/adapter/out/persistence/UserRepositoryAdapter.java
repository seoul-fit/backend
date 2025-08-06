package com.seoulfit.backend.user.adapter.out.persistence;

import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 Repository 어댑터
 * Hexagonal Architecture의 아웃바운드 어댑터
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserPort {
    
    private final UserRepository userRepository;
    
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findByAuthProviderAndOAuthUserId(AuthProvider provider, String oauthUserId) {
        return userRepository.findByOauthProviderAndOauthUserId(provider, oauthUserId);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByAuthProviderAndOAuthUserId(AuthProvider provider, String oauthUserId) {
        return userRepository.existsByOauthProviderAndOauthUserId(provider, oauthUserId);
    }
    
    @Override
    public List<User> findAllActiveUsers() {
        // 현재는 모든 사용자를 활성 사용자로 간주
        // 추후 User 엔티티에 active 필드 추가 시 수정 필요
        return userRepository.findAll();
    }
    
    @Override
    public List<User> findUsersByInterest(InterestCategory interestCategory) {
        // UserInterest 조인을 통해 특정 관심사를 가진 사용자 조회
        return userRepository.findUsersByInterestCategory(interestCategory);
    }
}
