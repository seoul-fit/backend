package com.seoulfit.backend.application.service;

import com.seoulfit.backend.domain.InterestCategory;
import com.seoulfit.backend.domain.User;
import com.seoulfit.backend.domain.UserStatus;
import com.seoulfit.backend.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createUser(String email, String password, String nickname, List<InterestCategory> interests) {
        validateDuplicateEmail(email);
        validateDuplicateNickname(nickname);

        User user = User.builder()
                .email(email)
                .password(password) // TODO: 암호화 처리 필요
                .nickname(nickname)
                .build();

        interests.forEach(user::addInterest);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public User findByIdWithInterests(Long userId) {
        return userRepository.findByIdWithInterests(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public User updateInterests(Long userId, List<InterestCategory> interests) {
        User user = findByIdWithInterests(userId);
        user.updateInterests(interests);
        return user;
    }

    @Transactional
    public User updateNickname(Long userId, String nickname) {
        validateDuplicateNickname(nickname);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        user.updateNickname(nickname);
        return user;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        user.delete();
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
    }
}
