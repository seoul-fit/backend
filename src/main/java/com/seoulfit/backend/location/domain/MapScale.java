package com.seoulfit.backend.location.domain;

import lombok.Getter;

/**
 * 지도 축적 정보와 검색 반경 매핑
 * 
 * 지도의 축적(Scale) 정보에 따라 적절한 검색 반경을 자동으로 결정하는 시스템
 * 사용자의 지도 확대/축소 레벨에 따라 최적화된 데이터 조회 범위를 제공
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
public enum MapScale {
    // 매우 상세한 레벨 (건물 단위)
    BUILDING_LEVEL(1, 50, 0.05, "건물 단위", "개별 건물이 구분되는 레벨"),
    STREET_LEVEL(2, 100, 0.1, "거리 단위", "거리와 상점이 구분되는 레벨"),
    
    // 근거리 레벨 (동네 단위)
    NEIGHBORHOOD_CLOSE(3, 200, 0.2, "근거리 동네", "인근 블록 단위"),
    NEIGHBORHOOD_MEDIUM(4, 500, 0.5, "중거리 동네", "동네 중심가 단위"),
    NEIGHBORHOOD_FAR(5, 1000, 1.0, "원거리 동네", "동네 전체 단위"),
    
    // 지역 레벨 (구 단위)
    DISTRICT_CLOSE(6, 2000, 2.0, "근거리 지역", "인근 동 단위"),
    DISTRICT_MEDIUM(7, 3000, 3.0, "중거리 지역", "구 일부 단위"),
    DISTRICT_FAR(8, 5000, 5.0, "원거리 지역", "구 전체 단위"),
    
    // 도시 레벨 (시 단위)
    CITY_CLOSE(9, 10000, 10.0, "근거리 도시", "인근 구 단위"),
    CITY_MEDIUM(10, 15000, 15.0, "중거리 도시", "도시 일부 단위"),
    CITY_FAR(11, 25000, 25.0, "원거리 도시", "도시 전체 단위"),
    
    // 광역 레벨 (수도권 단위)
    METROPOLITAN(12, 50000, 50.0, "광역권", "수도권 전체 단위");

    private final int level;                    // 축적 레벨 (1-12)
    private final int scaleValue;              // 축적 값 (1:50 ~ 1:50000)
    private final double radiusKm;             // 검색 반경 (km)
    private final String displayName;          // 표시명
    private final String description;          // 설명

    MapScale(int level, int scaleValue, double radiusKm, String displayName, String description) {
        this.level = level;
        this.scaleValue = scaleValue;
        this.radiusKm = radiusKm;
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 축적 레벨로 MapScale 찾기
     */
    public static MapScale fromLevel(int level) {
        for (MapScale scale : values()) {
            if (scale.level == level) {
                return scale;
            }
        }
        // 범위를 벗어나면 가장 가까운 값으로 매핑
        if (level <= 1) return BUILDING_LEVEL;
        if (level >= 12) return METROPOLITAN;
        
        // 중간값 찾기
        for (MapScale scale : values()) {
            if (scale.level >= level) {
                return scale;
            }
        }
        return DISTRICT_MEDIUM; // 기본값
    }

    /**
     * 축적 값으로 MapScale 찾기 (1:50000 형태)
     */
    public static MapScale fromScaleValue(int scaleValue) {
        MapScale closest = DISTRICT_MEDIUM;
        int minDiff = Integer.MAX_VALUE;
        
        for (MapScale scale : values()) {
            int diff = Math.abs(scale.scaleValue - scaleValue);
            if (diff < minDiff) {
                minDiff = diff;
                closest = scale;
            }
        }
        return closest;
    }

    /**
     * 줌 레벨로 MapScale 찾기 (Google Maps 스타일: 1-20)
     */
    public static MapScale fromZoomLevel(int zoomLevel) {
        // 줌 레벨을 축적 레벨로 변환 (역비례 관계)
        int mappedLevel = Math.max(1, Math.min(12, 13 - (zoomLevel / 2)));
        return fromLevel(mappedLevel);
    }

    /**
     * 픽셀 단위 해상도로 MapScale 찾기
     */
    public static MapScale fromPixelResolution(double metersPerPixel) {
        if (metersPerPixel <= 0.5) return BUILDING_LEVEL;
        if (metersPerPixel <= 1.0) return STREET_LEVEL;
        if (metersPerPixel <= 2.0) return NEIGHBORHOOD_CLOSE;
        if (metersPerPixel <= 5.0) return NEIGHBORHOOD_MEDIUM;
        if (metersPerPixel <= 10.0) return NEIGHBORHOOD_FAR;
        if (metersPerPixel <= 20.0) return DISTRICT_CLOSE;
        if (metersPerPixel <= 30.0) return DISTRICT_MEDIUM;
        if (metersPerPixel <= 50.0) return DISTRICT_FAR;
        if (metersPerPixel <= 100.0) return CITY_CLOSE;
        if (metersPerPixel <= 150.0) return CITY_MEDIUM;
        if (metersPerPixel <= 250.0) return CITY_FAR;
        return METROPOLITAN;
    }

    /**
     * 바운딩 박스 크기로 MapScale 찾기
     */
    public static MapScale fromBoundingBoxSize(double widthKm, double heightKm) {
        double maxSize = Math.max(widthKm, heightKm);
        double radius = maxSize / 2.0;
        
        for (MapScale scale : values()) {
            if (radius <= scale.radiusKm) {
                return scale;
            }
        }
        return METROPOLITAN;
    }

    /**
     * 데이터 밀도에 따른 최적 반경 계산
     * 
     * @param baseRadius 기본 반경
     * @param dataDensity 데이터 밀도 (개/km²)
     * @return 최적화된 반경
     */
    public double getOptimizedRadius(double baseRadius, double dataDensity) {
        // 데이터 밀도가 높으면 반경을 줄이고, 낮으면 늘림
        double densityFactor = Math.max(0.5, Math.min(2.0, 100.0 / dataDensity));
        return Math.min(this.radiusKm, baseRadius * densityFactor);
    }

    /**
     * 성능 최적화를 위한 사전 필터링 박스 크기 계산
     * 위경도 ± 값으로 사각형 영역을 만들어 DB 쿼리 최적화
     */
    public BoundingBox getBoundingBox(double centerLat, double centerLng) {
        // 1도당 거리 (대략적인 값, 위도에 따라 다름)
        double latDegreeKm = 111.0; // 위도 1도 ≈ 111km
        double lngDegreeKm = 111.0 * Math.cos(Math.toRadians(centerLat)); // 경도는 위도에 따라 변함
        
        double latDelta = radiusKm / latDegreeKm;
        double lngDelta = radiusKm / lngDegreeKm;
        
        return BoundingBox.builder()
                .minLat(centerLat - latDelta)
                .maxLat(centerLat + latDelta)
                .minLng(centerLng - lngDelta)
                .maxLng(centerLng + lngDelta)
                .build();
    }

    /**
     * 바운딩 박스 정보
     */
    @lombok.Builder
    @lombok.Getter
    public static class BoundingBox {
        private final double minLat;
        private final double maxLat;
        private final double minLng;
        private final double maxLng;
        
        /**
         * 점이 바운딩 박스 내부에 있는지 확인
         */
        public boolean contains(double lat, double lng) {
            return lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng;
        }
        
        /**
         * 바운딩 박스의 중심점 계산
         */
        public double[] getCenter() {
            return new double[]{(minLat + maxLat) / 2.0, (minLng + maxLng) / 2.0};
        }
        
        /**
         * 바운딩 박스의 크기 계산 (km)
         */
        public double[] getSizeKm() {
            double latDiff = (maxLat - minLat) * 111.0;
            double lngDiff = (maxLng - minLng) * 111.0 * Math.cos(Math.toRadians((minLat + maxLat) / 2.0));
            return new double[]{lngDiff, latDiff}; // [width, height]
        }
    }
}
