package com.seoulfit.backend.user.application.port.in;

import com.seoulfit.backend.user.application.port.in.dto.UpdateUserCommand;
import com.seoulfit.backend.user.application.port.in.dto.UserResult;

/**
 * 사용자 관리 Use Case
 * <p>
 * 헥사고날 아키텍처의 입력 포트 사용자 프로필 관리와 관련된 비즈니스 로직을 정의
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface ManageUserUseCase {

    /**
     * 사용자 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 결과
     */
    UserResult getUser(Long userId);

    /**
     * 인증 사용자 ID로 사용자 조회
     *
     * @param authUserId 인증 사용자 ID
     * @return 사용자 결과
     */
    UserResult getUserByAuthUserId(Long authUserId);

    /**
     * 사용자 정보 수정
     *
     * @param command 사용자 수정 명령
     * @return 수정된 사용자 결과
     */
    UserResult updateUser(UpdateUserCommand command);

    /**
     * 사용자 삭제
     *
     * @param userId 사용자 ID
     */
    void deleteUser(Long userId);
}
