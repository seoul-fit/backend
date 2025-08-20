package com.seoulfit.backend.user.application.service;

import com.seoulfit.backend.user.application.port.in.ManageUserUseCase;
import com.seoulfit.backend.user.application.port.in.dto.UpdateUserCommand;
import com.seoulfit.backend.user.application.port.in.dto.UserResult;
import com.seoulfit.backend.user.application.port.out.UserPort;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 * <p>
 * 헥사고날 아키텍처의 애플리케이션 서비스
 * 사용자 프로필 관리와 관련된 비즈니스 로직을 처리
 *
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements ManageUserUseCase {

    private final UserPort userPort;

    @Override
    public UserResult getUser(Long userId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResult.from(user);
    }

    @Override
    public UserResult getUserByOAuth(String oauthUserId, AuthProvider oauthProvider) {
        User user = userPort.findByProviderAndOauthUserId(oauthProvider, oauthUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserResult.from(user);
    }

    @Override
    @Transactional
    public UserResult updateUser(UpdateUserCommand command) {
        User user = userPort.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 프로필 정보 업데이트
        if (command.getNickname() != null || command.getProfileImageUrl() != null) {
            user.updateProfile(command.getNickname(), command.getProfileImageUrl());
        }

        // 위치 정보 업데이트
        if (command.getLocationLatitude() != null && command.getLocationLongitude() != null) {
            user.updateLocation(
                    command.getLocationLatitude(),
                    command.getLocationLongitude(),
                    command.getLocationAddress()
            );
        }

        // 관심사 업데이트
        if (command.getInterests() != null) {
            user.updateInterests(command.getInterests());
        }

        User savedUser = userPort.save(user);
        return UserResult.from(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 하드 삭제 - 데이터베이스에서 완전 삭제
        userPort.deleteById(userId);
        log.info("사용자 완전 삭제 완료: userId={}", userId);
    }
}
