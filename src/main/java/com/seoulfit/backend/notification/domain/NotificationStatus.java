package com.seoulfit.backend.notification.domain;

import lombok.Getter;

/**
 * 알림 상태
 * 알림의 현재 상태를 나타냄
 */
@Getter
public enum NotificationStatus {
    SENT("발송됨", "알림이 성공적으로 발송됨"),
    READ("읽음", "사용자가 알림을 읽음"),
    FAILED("실패", "알림 발송에 실패함"),
    EXPIRED("만료", "알림이 만료됨");

    private final String displayName;
    private final String description;

    NotificationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
