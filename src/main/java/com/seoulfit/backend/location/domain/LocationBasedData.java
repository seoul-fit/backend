package com.seoulfit.backend.location.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 위치 기반 데이터 집합
 */
@Data
@Builder
public class LocationBasedData {
    
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private String district;
    
    // 주변 시설 정보
    private List<SportsFacility> sportsFacilities;
    private List<ParkInfo> parks;
    private List<RestaurantInfo> restaurants;
    private List<CulturalEventInfo> culturalEvents;
    
    // 대기질 정보
    private AirQualityInfo airQuality;
    
    // 통계 정보
    private Map<String, Integer> facilityCounts;
    private Integer totalCount;
    
    @Data
    @Builder
    public static class ParkInfo {
        private Long id;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private Double distance;
    }
    
    @Data
    @Builder
    public static class RestaurantInfo {
        private Long id;
        private String name;
        private String category;
        private String address;
        private Double latitude;
        private Double longitude;
        private Double distance;
    }
    
    @Data
    @Builder
    public static class CulturalEventInfo {
        private Long id;
        private String title;
        private String venue;
        private String startDate;
        private String endDate;
        private Double distance;
    }
    
    @Data
    @Builder
    public static class AirQualityInfo {
        private Integer pm10;
        private Integer pm25;
        private String status;
        private String district;
    }
}