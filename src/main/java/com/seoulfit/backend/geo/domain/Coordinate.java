package com.seoulfit.backend.geo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 위경도 좌표를 나타내는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {
    
    /**
     * 위도 (Latitude)
     */
    private double latitude;
    
    /**
     * 경도 (Longitude)
     */
    private double longitude;
    
    /**
     * 좌표 유효성 검증
     */
    public boolean isValid() {
        return latitude >= -90 && latitude <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    /**
     * 한국 영역 내 좌표인지 확인
     */
    public boolean isInKorea() {
        // 한국의 대략적인 경계
        return latitude >= 33.0 && latitude <= 38.6 &&
               longitude >= 124.0 && longitude <= 132.0;
    }
    
    @Override
    public String toString() {
        return String.format("(%.6f, %.6f)", latitude, longitude);
    }
}
