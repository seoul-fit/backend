package com.seoulfit.backend.location.util;

import com.seoulfit.backend.location.domain.MapScale;
import lombok.experimental.UtilityClass;

/**
 * 지리적 계산 유틸리티
 * 
 * 고성능 거리 계산 및 지리적 연산을 위한 유틸리티 클래스
 * 다양한 거리 계산 알고리즘과 최적화 기법을 제공
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@UtilityClass
public class GeoUtils {

    // 지구 반지름 (km)
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    // 위도 1도당 거리 (km) - 상수
    private static final double LAT_DEGREE_KM = 111.0;
    
    // 거리 계산 캐시 (성능 최적화용)
    private static final int CACHE_SIZE = 1000;
    private static final java.util.Map<String, Double> distanceCache = 
            new java.util.concurrent.ConcurrentHashMap<>(CACHE_SIZE);

    /**
     * Haversine 공식을 사용한 정확한 거리 계산
     * 
     * @param lat1 첫 번째 점의 위도
     * @param lng1 첫 번째 점의 경도
     * @param lat2 두 번째 점의 위도
     * @param lng2 두 번째 점의 경도
     * @return 거리 (km)
     */
    public static double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2) {
        // 캐시 키 생성 (소수점 4자리까지만 사용하여 캐시 효율성 증대)
        String cacheKey = String.format("%.4f,%.4f,%.4f,%.4f", lat1, lng1, lat2, lng2);
        
        return distanceCache.computeIfAbsent(cacheKey, k -> {
            double latDistance = Math.toRadians(lat2 - lat1);
            double lngDistance = Math.toRadians(lng2 - lng1);
            
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
            
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            
            return EARTH_RADIUS_KM * c;
        });
    }

    /**
     * 빠른 근사 거리 계산 (Equirectangular approximation)
     * 정확도는 떨어지지만 성능이 뛰어남 (근거리용)
     * 
     * @param lat1 첫 번째 점의 위도
     * @param lng1 첫 번째 점의 경도
     * @param lat2 두 번째 점의 위도
     * @param lng2 두 번째 점의 경도
     * @return 거리 (km)
     */
    public static double calculateFastDistance(double lat1, double lng1, double lat2, double lng2) {
        double latDiff = Math.toRadians(lat2 - lat1);
        double lngDiff = Math.toRadians(lng2 - lng1);
        double avgLat = Math.toRadians((lat1 + lat2) / 2);
        
        double x = lngDiff * Math.cos(avgLat);
        double y = latDiff;
        
        return EARTH_RADIUS_KM * Math.sqrt(x * x + y * y);
    }

    /**
     * 맨하탄 거리 계산 (매우 빠름, 격자형 도로에서 유용)
     * 
     * @param lat1 첫 번째 점의 위도
     * @param lng1 첫 번째 점의 경도
     * @param lat2 두 번째 점의 위도
     * @param lng2 두 번째 점의 경도
     * @return 맨하탄 거리 (km)
     */
    public static double calculateManhattanDistance(double lat1, double lng1, double lat2, double lng2) {
        double latDistance = Math.abs(lat2 - lat1) * LAT_DEGREE_KM;
        double lngDistance = Math.abs(lng2 - lng1) * LAT_DEGREE_KM * Math.cos(Math.toRadians((lat1 + lat2) / 2));
        
        return latDistance + lngDistance;
    }

    /**
     * 거리 계산 방법을 자동 선택
     * 거리와 정확도 요구사항에 따라 최적의 알고리즘 선택
     * 
     * @param lat1 첫 번째 점의 위도
     * @param lng1 첫 번째 점의 경도
     * @param lat2 두 번째 점의 위도
     * @param lng2 두 번째 점의 경도
     * @param maxDistance 최대 거리 (km) - 이 값을 초과하면 빠른 계산 사용
     * @return 거리 (km)
     */
    public static double calculateOptimalDistance(double lat1, double lng1, double lat2, double lng2, double maxDistance) {
        // 매우 가까운 거리는 빠른 계산 사용
        if (maxDistance <= 1.0) {
            return calculateFastDistance(lat1, lng1, lat2, lng2);
        }
        // 중간 거리는 정확한 계산 사용
        else if (maxDistance <= 10.0) {
            return calculateHaversineDistance(lat1, lng1, lat2, lng2);
        }
        // 먼 거리는 빠른 계산으로도 충분
        else {
            return calculateFastDistance(lat1, lng1, lat2, lng2);
        }
    }

    /**
     * 바운딩 박스 내부 점 필터링
     * 
     * @param points 필터링할 점들
     * @param boundingBox 바운딩 박스
     * @return 필터링된 점들
     */
    public static <T extends GeoPoint> java.util.List<T> filterByBoundingBox(
            java.util.List<T> points, MapScale.BoundingBox boundingBox) {
        return points.stream()
                .filter(point -> boundingBox.contains(point.getLatitude(), point.getLongitude()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 원형 반경 내부 점 필터링 (정확한 거리 계산)
     * 
     * @param points 필터링할 점들
     * @param centerLat 중심점 위도
     * @param centerLng 중심점 경도
     * @param radiusKm 반경 (km)
     * @return 필터링된 점들과 거리 정보
     */
    public static <T extends GeoPoint> java.util.List<GeoPointWithDistance<T>> filterByCircularRadius(
            java.util.List<T> points, double centerLat, double centerLng, double radiusKm) {
        return points.stream()
                .map(point -> {
                    double distance = calculateOptimalDistance(
                            centerLat, centerLng, 
                            point.getLatitude(), point.getLongitude(), 
                            radiusKm);
                    return new GeoPointWithDistance<>(point, distance);
                })
                .filter(pointWithDistance -> pointWithDistance.getDistance() <= radiusKm)
                .sorted(java.util.Comparator.comparing(GeoPointWithDistance::getDistance))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 2단계 필터링: 바운딩 박스 → 원형 반경
     * 성능 최적화를 위한 단계적 필터링
     * 
     * @param points 필터링할 점들
     * @param centerLat 중심점 위도
     * @param centerLng 중심점 경도
     * @param radiusKm 반경 (km)
     * @param mapScale 지도 축적 정보
     * @return 필터링된 점들과 거리 정보
     */
    public static <T extends GeoPoint> java.util.List<GeoPointWithDistance<T>> filterByTwoStageRadius(
            java.util.List<T> points, double centerLat, double centerLng, double radiusKm, MapScale mapScale) {
        
        // 1단계: 바운딩 박스로 빠른 필터링
        MapScale.BoundingBox boundingBox = mapScale.getBoundingBox(centerLat, centerLng);
        java.util.List<T> boundingBoxFiltered = filterByBoundingBox(points, boundingBox);
        
        // 2단계: 원형 반경으로 정확한 필터링
        return filterByCircularRadius(boundingBoxFiltered, centerLat, centerLng, radiusKm);
    }

    /**
     * 베어링(방향) 계산
     * 
     * @param lat1 시작점 위도
     * @param lng1 시작점 경도
     * @param lat2 끝점 위도
     * @param lng2 끝점 경도
     * @return 베어링 (도, 0-360)
     */
    public static double calculateBearing(double lat1, double lng1, double lat2, double lng2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLng = Math.toRadians(lng2 - lng1);
        
        double y = Math.sin(deltaLng) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - 
                   Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLng);
        
        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    /**
     * 지리적 점 인터페이스
     */
    public interface GeoPoint {
        double getLatitude();
        double getLongitude();
    }

    /**
     * 거리 정보를 포함한 지리적 점
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class GeoPointWithDistance<T extends GeoPoint> {
        private final T point;
        private final double distance;
        
        public double getLatitude() {
            return point.getLatitude();
        }
        
        public double getLongitude() {
            return point.getLongitude();
        }
    }

    /**
     * 거리 계산 캐시 정리
     */
    public static void clearDistanceCache() {
        distanceCache.clear();
    }

    /**
     * 캐시 통계 정보
     */
    public static java.util.Map<String, Object> getCacheStats() {
        return java.util.Map.of(
                "cacheSize", distanceCache.size(),
                "maxCacheSize", CACHE_SIZE,
                "cacheHitRate", distanceCache.size() > 0 ? "N/A" : "0%"
        );
    }
}
