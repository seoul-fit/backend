package com.seoulfit.backend.culture.domain.enums;

import lombok.Getter;

@Getter
public enum CongestionLevel {
    LOW("여유", "#4CAF50"),
    MODERATE("보통", "#FF9800"),
    HIGH("혼잡", "#FF5722"),
    VERY_HIGH("매우혼잡", "#F44336");

    private final String displayName;
    private final String colorCode;

    CongestionLevel(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }
}
