package com.seoulfit.backend.shared.utils;

import java.math.BigDecimal;

/**
 * 지리적 계산을 위한 유틸리티 클래스
 * 
 * 데이터베이스에서 복잡한 Haversine 계산을 피하고, 
 * 애플리케이션 레벨에서 효율적인 지리적 계산을 제공합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public class GeoUtils {
    
    /**
     * 지구 반지름 (킬로미터)
     */
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * 1도당 거리 (킬로미터) - 대략적인 값 (위도 기준)
     * 서울 지역(위도 37도)에서의 근사값
     */
    private static final double KM_PER_DEGREE_LAT = 111.0;
    private static final double KM_PER_DEGREE_LNG = 88.8; // cos(37도) * 111km ≈ 88.8km
    
    /**
     * 바운딩 박스 계산
     * 
     * 중심점과 반지름을 기준으로 검색할 바운딩 박스의 경계를 계산합니다.
     * 이 방법은 Haversine 공식보다 훨씬 빠르며, 대부분의 경우 충분한 정확도를 제공합니다.
     * 
     * @param centerLat 중심점 위도
     * @param centerLng 중심점 경도
     * @param radiusKm 검색 반지름 (킬로미터)
     * @return BoundingBox 객체 [minLat, maxLat, minLng, maxLng]
     */
    public static BoundingBox calculateBoundingBox(double centerLat, double centerLng, double radiusKm) {
        double latDelta = radiusKm / KM_PER_DEGREE_LAT;
        double lngDelta = radiusKm / KM_PER_DEGREE_LNG;
        
        return new BoundingBox(
            BigDecimal.valueOf(centerLat - latDelta),
            BigDecimal.valueOf(centerLat + latDelta),
            BigDecimal.valueOf(centerLng - lngDelta),
            BigDecimal.valueOf(centerLng + lngDelta)
        );
    }
    
    /**
     * Haversine 공식을 사용한 정확한 거리 계산
     * 
     * 바운딩 박스로 1차 필터링된 결과에 대해 정확한 거리를 계산합니다.
     * 데이터베이스에서 계산하는 것보다 애플리케이션에서 계산하는 것이 효율적입니다.
     * 
     * @param lat1 첫 번째 지점의 위도
     * @param lng1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lng2 두 번째 지점의 경도
     * @return 두 지점 간의 거리 (킬로미터)
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
                   
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * BigDecimal 버전의 거리 계산
     */
    public static double calculateDistance(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        return calculateDistance(lat1.doubleValue(), lng1.doubleValue(), 
                               lat2.doubleValue(), lng2.doubleValue());
    }
    
    /**
     * 바운딩 박스 내부에 점이 포함되는지 확인
     */
    public static boolean isInBoundingBox(double lat, double lng, BoundingBox boundingBox) {
        return lat >= boundingBox.minLat().doubleValue() && 
               lat <= boundingBox.maxLat().doubleValue() &&
               lng >= boundingBox.minLng().doubleValue() && 
               lng <= boundingBox.maxLng().doubleValue();
    }
    
    /**
     * 바운딩 박스를 나타내는 레코드 클래스
     */
    public record BoundingBox(
        BigDecimal minLat,
        BigDecimal maxLat,
        BigDecimal minLng,
        BigDecimal maxLng
    ) {}
}