package com.seoulfit.backend.user.application.port.in;

import com.seoulfit.backend.user.application.port.in.dto.UpdateUserInterestCommand;
import com.seoulfit.backend.user.application.port.in.dto.UserInterestResult;

/**
 * 사용자 관심사 관리 Use Case
 */
public interface ManageUserInterestUseCase {

    /**
     * 사용자의 관심사를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 관심사 결과
     */
    UserInterestResult getUserInterests(Long userId);

    /**
     * 사용자의 관심사를 업데이트합니다.
     *
     * @param command 관심사 업데이트 명령
     * @return 사용자 관심사 결과
     */
    UserInterestResult updateUserInterests(UpdateUserInterestCommand command);
}
