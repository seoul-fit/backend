package com.seoulfit.backend.location.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("GeoUtils 테스트")
class GeoUtilsTest {

    @Test
    @DisplayName("Haversine 거리 계산 - 서울 시청과 남산타워")
    void calculateHaversineDistance_SeoulCityHallToNamsan() {
        // given
        double lat1 = 37.5663; // 서울 시청
        double lon1 = 126.9779;
        double lat2 = 37.5511; // 남산타워
        double lon2 = 126.9882;

        // when
        double distance = GeoUtils.calculateHaversineDistance(lat1, lon1, lat2, lon2);

        // then
        // 실제 거리는 약 1.8km 정도
        assertThat(distance).isCloseTo(1.8, within(0.5));
    }

    @Test
    @DisplayName("Haversine 거리 계산 - 동일한 지점")
    void calculateHaversineDistance_SameLocation() {
        // given
        double lat = 37.5665;
        double lon = 126.9780;

        // when
        double distance = GeoUtils.calculateHaversineDistance(lat, lon, lat, lon);

        // then
        assertThat(distance).isEqualTo(0.0);
    }

    @Test
    @DisplayName("빠른 거리 계산 - 서울과 부산")
    void calculateFastDistance_SeoulToBusan() {
        // given
        double seoulLat = 37.5665;
        double seoulLon = 126.9780;
        double busanLat = 35.1796;
        double busanLon = 129.0756;

        // when
        double distance = GeoUtils.calculateFastDistance(seoulLat, seoulLon, busanLat, busanLon);

        // then
        // 서울-부산 직선거리는 약 325km
        assertThat(distance).isCloseTo(325.0, within(50.0));
    }

    @Test
    @DisplayName("맨하탄 거리 계산")
    void calculateManhattanDistance() {
        // given
        double lat1 = 37.5665;
        double lon1 = 126.9780;
        double lat2 = 37.5511;
        double lon2 = 126.9882;

        // when
        double distance = GeoUtils.calculateManhattanDistance(lat1, lon1, lat2, lon2);

        // then
        assertThat(distance).isGreaterThan(0);
        // 맨하탄 거리는 직선거리보다 길어야 함
        double haversineDistance = GeoUtils.calculateHaversineDistance(lat1, lon1, lat2, lon2);
        assertThat(distance).isGreaterThan(haversineDistance);
    }

    @Test
    @DisplayName("최적 거리 계산 - 근거리")
    void calculateOptimalDistance_ShortDistance() {
        // given
        double lat1 = 37.5665;
        double lon1 = 126.9780;
        double lat2 = 37.5511;
        double lon2 = 126.9882;
        double maxDistance = 0.5; // 500m

        // when
        double distance = GeoUtils.calculateOptimalDistance(lat1, lon1, lat2, lon2, maxDistance);

        // then
        assertThat(distance).isGreaterThan(0);
        assertThat(distance).isCloseTo(1.8, within(0.5));
    }

    @Test
    @DisplayName("베어링 계산")
    void calculateBearing() {
        // given
        double lat1 = 37.5665; // 서울
        double lon1 = 126.9780;
        double lat2 = 35.1796; // 부산 (남동쪽)
        double lon2 = 129.0756;

        // when
        double bearing = GeoUtils.calculateBearing(lat1, lon1, lat2, lon2);

        // then
        assertThat(bearing).isBetween(0.0, 360.0);
        // 부산은 서울의 남동쪽에 위치 (대략 120-150도 방향)
        assertThat(bearing).isBetween(100.0, 180.0);
    }

    @Test
    @DisplayName("원형 반경 필터링")
    void filterByCircularRadius() {
        // given
        double centerLat = 37.5665;
        double centerLon = 126.9780;
        double radiusKm = 2.0;

        TestGeoPoint center = new TestGeoPoint(centerLat, centerLon);
        TestGeoPoint nearby = new TestGeoPoint(37.5511, 126.9882); // 남산타워 (약 1.8km)
        TestGeoPoint faraway = new TestGeoPoint(37.4782, 127.0122); // 강남 (약 10km)

        List<TestGeoPoint> points = Arrays.asList(center, nearby, faraway);

        // when
        List<GeoUtils.GeoPointWithDistance<TestGeoPoint>> result = 
                GeoUtils.filterByCircularRadius(points, centerLat, centerLon, radiusKm);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPoint()).isEqualTo(center); // 가장 가까운 점이 첫 번째
        assertThat(result.get(1).getPoint()).isEqualTo(nearby);
        
        // 거리 순으로 정렬되어 있는지 확인
        assertThat(result.get(0).getDistance()).isLessThanOrEqualTo(result.get(1).getDistance());
    }

    @Test
    @DisplayName("원형 반경 필터링 - 빈 리스트")
    void filterByCircularRadius_EmptyList() {
        // given
        List<TestGeoPoint> points = Collections.emptyList();

        // when
        List<GeoUtils.GeoPointWithDistance<TestGeoPoint>> result = 
                GeoUtils.filterByCircularRadius(points, 37.5665, 126.9780, 2.0);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("거리 계산 캐시 테스트")
    void distanceCalculationCache() {
        // given
        double lat1 = 37.5665;
        double lon1 = 126.9780;
        double lat2 = 37.5511;
        double lon2 = 126.9882;

        // when - 같은 계산을 여러 번 수행
        double distance1 = GeoUtils.calculateHaversineDistance(lat1, lon1, lat2, lon2);
        double distance2 = GeoUtils.calculateHaversineDistance(lat1, lon1, lat2, lon2);
        double distance3 = GeoUtils.calculateHaversineDistance(lat1, lon1, lat2, lon2);

        // then - 모든 결과가 동일해야 함 (캐시 동작 확인)
        assertThat(distance1).isEqualTo(distance2);
        assertThat(distance2).isEqualTo(distance3);
    }

    @Test
    @DisplayName("캐시 통계 확인")
    void getCacheStats() {
        // given
        GeoUtils.clearDistanceCache();

        // when
        var stats = GeoUtils.getCacheStats();

        // then
        assertThat(stats).containsKey("cacheSize");
        assertThat(stats).containsKey("maxCacheSize");
        assertThat(stats).containsKey("cacheHitRate");
        assertThat(stats.get("cacheSize")).isEqualTo(0);
    }

    @Test
    @DisplayName("캐시 정리 기능")
    void clearDistanceCache() {
        // given
        GeoUtils.calculateHaversineDistance(37.5665, 126.9780, 37.5511, 126.9882);
        
        // when
        GeoUtils.clearDistanceCache();
        var stats = GeoUtils.getCacheStats();

        // then
        assertThat(stats.get("cacheSize")).isEqualTo(0);
    }

    // 테스트용 GeoPoint 구현체
    private static class TestGeoPoint implements GeoUtils.GeoPoint {
        private final double latitude;
        private final double longitude;

        public TestGeoPoint(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public double getLatitude() {
            return latitude;
        }

        @Override
        public double getLongitude() {
            return longitude;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestGeoPoint that = (TestGeoPoint) obj;
            return Double.compare(that.latitude, latitude) == 0 &&
                   Double.compare(that.longitude, longitude) == 0;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(latitude, longitude);
        }
    }
}
