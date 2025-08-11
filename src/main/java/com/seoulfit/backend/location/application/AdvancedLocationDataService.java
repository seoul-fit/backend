package com.seoulfit.backend.location.application;

import com.seoulfit.backend.location.domain.*;
import com.seoulfit.backend.location.infrastructure.*;
import com.seoulfit.backend.location.util.GeoUtils;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.CoolingCenterRepository;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.LibraryRepository;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 고도화된 위치 기반 데이터 서비스
 * 
 * 지도 축적정보를 활용한 최적화된 데이터 조회 서비스
 * 2단계 필터링 (바운딩 박스 → 원형 반경)으로 성능 최적화
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvancedLocationDataService {

    private final RestaurantRepository restaurantRepository;
    private final LibraryRepository libraryRepository;
    private final ParkRepository parkRepository;
    private final SportsFacilityRepository sportsFacilityRepository;
    private final CoolingCenterRepository coolingCenterRepository;

    /**
     * 지도 축적정보 기반 고도화된 위치 데이터 조회
     */
    public AdvancedLocationData findAdvancedLocationData(Double latitude, Double longitude, 
                                                        MapScale mapScale, List<String> interests, 
                                                        Integer maxResults) {
        log.info("고도화된 위치 데이터 조회 시작: lat={}, lng={}, scale={}, interests={}, maxResults={}", 
                latitude, longitude, mapScale.getDisplayName(), interests, maxResults);

        long startTime = System.currentTimeMillis();

        // 바운딩 박스 계산
        MapScale.BoundingBox boundingBox = mapScale.getBoundingBox(latitude, longitude);
        double radiusKm = mapScale.getRadiusKm();

        AdvancedLocationData.AdvancedLocationDataBuilder builder = AdvancedLocationData.builder()
                .centerLatitude(latitude)
                .centerLongitude(longitude)
                .mapScale(mapScale)
                .boundingBox(boundingBox)
                .searchRadiusKm(radiusKm);

        // 각 카테고리별 데이터 조회
        if (interests.contains("RESTAURANTS")) {
            builder.restaurants(findNearbyRestaurantsAdvanced(latitude, longitude, boundingBox, radiusKm, maxResults));
        }
        if (interests.contains("LIBRARIES")) {
            builder.libraries(findNearbyLibrariesAdvanced(latitude, longitude, boundingBox, radiusKm, maxResults));
        }
        if (interests.contains("PARKS")) {
            builder.parks(findNearbyParksAdvanced(latitude, longitude, boundingBox, radiusKm, maxResults));
        }
        if (interests.contains("SPORTS_FACILITIES")) {
            builder.sportsFacilities(findNearbySportsFacilitiesAdvanced(latitude, longitude, boundingBox, radiusKm, maxResults));
        }
        if (interests.contains("COOLING_CENTERS")) {
            builder.coolingCenters(findNearbyCoolingCentersAdvanced(latitude, longitude, boundingBox, radiusKm, maxResults));
        }

        AdvancedLocationData result = builder.build();

        long endTime = System.currentTimeMillis();
        log.info("고도화된 위치 데이터 조회 완료: 총 {}개 항목, 처리시간 {}ms", 
                result.getTotalCount(), endTime - startTime);

        return result;
    }

    /**
     * 고도화된 맛집 조회 (2단계 필터링)
     */
    private List<GeoUtils.GeoPointWithDistance<Restaurant>> findNearbyRestaurantsAdvanced(
            Double centerLat, Double centerLng, MapScale.BoundingBox boundingBox, 
            double radiusKm, Integer maxResults) {
        
        // 1단계: 바운딩 박스로 DB 쿼리 최적화
        List<Restaurant> candidates = restaurantRepository.findByBoundingBox(
                boundingBox.getMinLat(), boundingBox.getMaxLat(),
                boundingBox.getMinLng(), boundingBox.getMaxLng());

        // 2단계: 정확한 원형 반경 필터링 및 거리 계산
        List<GeoUtils.GeoPointWithDistance<Restaurant>> filtered = candidates.stream()
                .map(restaurant -> {
                    double distance = GeoUtils.calculateOptimalDistance(
                            centerLat, centerLng, restaurant.getLatitude(), restaurant.getLongitude(), radiusKm);
                    return new GeoUtils.GeoPointWithDistance<>(restaurant, distance);
                })
                .filter(pointWithDistance -> pointWithDistance.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(maxResults != null ? maxResults : 50)
                .toList();

        log.debug("맛집 조회: 후보 {}개 → 필터링 후 {}개", candidates.size(), filtered.size());
        return filtered;
    }

    /**
     * 고도화된 도서관 조회 (2단계 필터링)
     */
    private List<GeoUtils.GeoPointWithDistance<Library>> findNearbyLibrariesAdvanced(
            Double centerLat, Double centerLng, MapScale.BoundingBox boundingBox, 
            double radiusKm, Integer maxResults) {
        
        List<Library> candidates = libraryRepository.findByBoundingBox(
                boundingBox.getMinLat(), boundingBox.getMaxLat(),
                boundingBox.getMinLng(), boundingBox.getMaxLng());

        List<GeoUtils.GeoPointWithDistance<Library>> filtered = candidates.stream()
                .map(library -> {
                    double distance = GeoUtils.calculateOptimalDistance(
                            centerLat, centerLng, library.getLatitude(), library.getLongitude(), radiusKm);
                    return new GeoUtils.GeoPointWithDistance<>(library, distance);
                })
                .filter(pointWithDistance -> pointWithDistance.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(maxResults != null ? maxResults : 30)
                .toList();

        log.debug("도서관 조회: 후보 {}개 → 필터링 후 {}개", candidates.size(), filtered.size());
        return filtered;
    }

    /**
     * 고도화된 공원 조회 (2단계 필터링)
     */
    private List<GeoUtils.GeoPointWithDistance<Park>> findNearbyParksAdvanced(
            Double centerLat, Double centerLng, MapScale.BoundingBox boundingBox, 
            double radiusKm, Integer maxResults) {
        
        List<Park> candidates = parkRepository.findByBoundingBox(
                boundingBox.getMinLat(), boundingBox.getMaxLat(),
                boundingBox.getMinLng(), boundingBox.getMaxLng());

        List<GeoUtils.GeoPointWithDistance<Park>> filtered = candidates.stream()
                .map(park -> {
                    double distance = GeoUtils.calculateOptimalDistance(
                            centerLat, centerLng, park.getLatitude(), park.getLongitude(), radiusKm);
                    return new GeoUtils.GeoPointWithDistance<>(park, distance);
                })
                .filter(pointWithDistance -> pointWithDistance.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(maxResults != null ? maxResults : 20)
                .toList();

        log.debug("공원 조회: 후보 {}개 → 필터링 후 {}개", candidates.size(), filtered.size());
        return filtered;
    }

    /**
     * 고도화된 체육시설 조회 (2단계 필터링)
     */
    private List<GeoUtils.GeoPointWithDistance<SportsFacility>> findNearbySportsFacilitiesAdvanced(
            Double centerLat, Double centerLng, MapScale.BoundingBox boundingBox, 
            double radiusKm, Integer maxResults) {
        
        List<SportsFacility> candidates = sportsFacilityRepository.findByBoundingBox(
                boundingBox.getMinLat(), boundingBox.getMaxLat(),
                boundingBox.getMinLng(), boundingBox.getMaxLng());

        List<GeoUtils.GeoPointWithDistance<SportsFacility>> filtered = candidates.stream()
                .map(facility -> {
                    double distance = GeoUtils.calculateOptimalDistance(
                            centerLat, centerLng, facility.getLatitude(), facility.getLongitude(), radiusKm);
                    return new GeoUtils.GeoPointWithDistance<>(facility, distance);
                })
                .filter(pointWithDistance -> pointWithDistance.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(maxResults != null ? maxResults : 30)
                .toList();

        log.debug("체육시설 조회: 후보 {}개 → 필터링 후 {}개", candidates.size(), filtered.size());
        return filtered;
    }

    /**
     * 고도화된 무더위쉼터 조회 (2단계 필터링)
     */
    private List<GeoUtils.GeoPointWithDistance<CoolingCenter>> findNearbyCoolingCentersAdvanced(
            Double centerLat, Double centerLng, MapScale.BoundingBox boundingBox, 
            double radiusKm, Integer maxResults) {
        
        List<CoolingCenter> candidates = coolingCenterRepository.findByBoundingBox(
                boundingBox.getMinLat(), boundingBox.getMaxLat(),
                boundingBox.getMinLng(), boundingBox.getMaxLng());

        List<GeoUtils.GeoPointWithDistance<CoolingCenter>> filtered = candidates.stream()
                .map(center -> {
                    double distance = GeoUtils.calculateOptimalDistance(
                            centerLat, centerLng, center.getLatitude(), center.getLongitude(), radiusKm);
                    return new GeoUtils.GeoPointWithDistance<>(center, distance);
                })
                .filter(pointWithDistance -> pointWithDistance.getDistance() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.getDistance(), b.getDistance()))
                .limit(maxResults != null ? maxResults : 40)
                .toList();

        log.debug("무더위쉼터 조회: 후보 {}개 → 필터링 후 {}개", candidates.size(), filtered.size());
        return filtered;
    }

    /**
     * 고도화된 위치 기반 데이터 결과
     */
    @lombok.Builder
    @lombok.Getter
    public static class AdvancedLocationData {
        private final Double centerLatitude;
        private final Double centerLongitude;
        private final MapScale mapScale;
        private final MapScale.BoundingBox boundingBox;
        private final Double searchRadiusKm;
        
        private final List<GeoUtils.GeoPointWithDistance<Restaurant>> restaurants;
        private final List<GeoUtils.GeoPointWithDistance<Library>> libraries;
        private final List<GeoUtils.GeoPointWithDistance<Park>> parks;
        private final List<GeoUtils.GeoPointWithDistance<SportsFacility>> sportsFacilities;
        private final List<GeoUtils.GeoPointWithDistance<CoolingCenter>> coolingCenters;

        public int getTotalCount() {
            int count = 0;
            if (restaurants != null) count += restaurants.size();
            if (libraries != null) count += libraries.size();
            if (parks != null) count += parks.size();
            if (sportsFacilities != null) count += sportsFacilities.size();
            if (coolingCenters != null) count += coolingCenters.size();
            return count;
        }

        public double getAverageDistance() {
            double totalDistance = 0;
            int totalCount = 0;

            if (restaurants != null) {
                totalDistance += restaurants.stream().mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).sum();
                totalCount += restaurants.size();
            }
            if (libraries != null) {
                totalDistance += libraries.stream().mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).sum();
                totalCount += libraries.size();
            }
            if (parks != null) {
                totalDistance += parks.stream().mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).sum();
                totalCount += parks.size();
            }
            if (sportsFacilities != null) {
                totalDistance += sportsFacilities.stream().mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).sum();
                totalCount += sportsFacilities.size();
            }
            if (coolingCenters != null) {
                totalDistance += coolingCenters.stream().mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).sum();
                totalCount += coolingCenters.size();
            }

            return totalCount > 0 ? totalDistance / totalCount : 0.0;
        }
    }
}
