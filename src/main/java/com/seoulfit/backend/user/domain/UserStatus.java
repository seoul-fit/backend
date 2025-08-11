package com.seoulfit.backend.user.domain;

import lombok.Getter;

/**
 * 사용자 상태
 * 
 * 사용자의 현재 상태를 나타내는 도메인 값 객체
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
public enum UserStatus {
    ACTIVE("활성", "정상적으로 서비스를 이용할 수 있는 상태"),
    INACTIVE("비활성", "일시적으로 서비스 이용이 중단된 상태"),
    DELETED("삭제", "사용자가 탈퇴하여 삭제된 상태"),
    SUSPENDED("정지", "관리자에 의해 서비스 이용이 정지된 상태");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 활성 상태 여부 확인
     * @return 활성 상태 여부
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 서비스 이용 가능 여부 확인
     * @return 서비스 이용 가능 여부
     */
    public boolean canUseService() {
        return this == ACTIVE;
    }
}
