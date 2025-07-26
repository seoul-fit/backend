package com.seoulfit.backend.domain;

import lombok.Getter;

@Getter
public enum InterestCategory {
    SPORTS("체육시설"),
    CULTURE("문화시설"),
    RESTAURANT("맛집"),
    LIBRARY("도서관"),
    PARK("공원");

    private final String displayName;

    InterestCategory(String displayName) {
        this.displayName = displayName;
    }
}
