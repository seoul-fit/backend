package com.seoulfit.backend.location.application;

import com.seoulfit.backend.location.domain.*;
import com.seoulfit.backend.location.infrastructure.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 위치 기반 데이터 조회 서비스
 * 
 * 사용자 위치를 기반으로 주변의 다양한 시설 정보를 조회하는 서비스
 * 맛집, 문화시설, 도서관, 공원, 체육시설, 무더위쉼터 등을 통합 제공
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationBasedDataService {

    private final RestaurantRepository restaurantRepository;
    private final LibraryRepository libraryRepository;
    private final ParkRepository parkRepository;
    private final SportsFacilityRepository sportsFacilityRepository;
    private final CoolingCenterRepository coolingCenterRepository;

    /**
     * 위치 기반 맛집 조회
     */
    public List<Restaurant> findNearbyRestaurants(Double latitude, Double longitude, Double radiusKm) {
        log.debug("위치 기반 맛집 조회: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);
        
        if (latitude == null || longitude == null) {
            log.warn("위치 정보가 없어 빈 목록 반환");
            return List.of();
        }

        List<Restaurant> restaurants = restaurantRepository.findByLocationWithinRadius(
                latitude, longitude, radiusKm);
        
        log.info("맛집 조회 결과: {}개", restaurants.size());
        return restaurants;
    }

    /**
     * 위치 기반 도서관 조회
     */
    public List<Library> findNearbyLibraries(Double latitude, Double longitude, Double radiusKm) {
        log.debug("위치 기반 도서관 조회: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);
        
        if (latitude == null || longitude == null) {
            log.warn("위치 정보가 없어 빈 목록 반환");
            return List.of();
        }

        List<Library> libraries = libraryRepository.findByLocationWithinRadius(
                latitude, longitude, radiusKm);
        
        log.info("도서관 조회 결과: {}개", libraries.size());
        return libraries;
    }

    /**
     * 위치 기반 공원 조회
     */
    public List<Park> findNearbyParks(Double latitude, Double longitude, Double radiusKm) {
        log.debug("위치 기반 공원 조회: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);
        
        if (latitude == null || longitude == null) {
            log.warn("위치 정보가 없어 빈 목록 반환");
            return List.of();
        }

        List<Park> parks = parkRepository.findByLocationWithinRadius(
                latitude, longitude, radiusKm);
        
        log.info("공원 조회 결과: {}개", parks.size());
        return parks;
    }

    /**
     * 위치 기반 체육시설 조회
     */
    public List<SportsFacility> findNearbySportsFacilities(Double latitude, Double longitude, Double radiusKm) {
        log.debug("위치 기반 체육시설 조회: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);
        
        if (latitude == null || longitude == null) {
            log.warn("위치 정보가 없어 빈 목록 반환");
            return List.of();
        }

        List<SportsFacility> facilities = sportsFacilityRepository.findByLocationWithinRadius(
                latitude, longitude, radiusKm);
        
        log.info("체육시설 조회 결과: {}개", facilities.size());
        return facilities;
    }

    /**
     * 위치 기반 무더위쉼터 조회
     */
    public List<CoolingCenter> findNearbyCoolingCenters(Double latitude, Double longitude, Double radiusKm) {
        log.debug("위치 기반 무더위쉼터 조회: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);
        
        if (latitude == null || longitude == null) {
            log.warn("위치 정보가 없어 빈 목록 반환");
            return List.of();
        }

        List<CoolingCenter> centers = coolingCenterRepository.findByLocationWithinRadius(
                latitude, longitude, radiusKm);
        
        log.info("무더위쉼터 조회 결과: {}개", centers.size());
        return centers;
    }

    /**
     * 위치 기반 통합 데이터 조회
     */
    public LocationBasedData findNearbyData(Double latitude, Double longitude, Double radiusKm) {
        log.debug("위치 기반 통합 데이터 조회: lat={}, lng={}, radius={}km", latitude, longitude, radiusKm);

        return LocationBasedData.builder()
                .restaurants(findNearbyRestaurants(latitude, longitude, radiusKm))
                .libraries(findNearbyLibraries(latitude, longitude, radiusKm))
                .parks(findNearbyParks(latitude, longitude, radiusKm))
                .sportsFacilities(findNearbySportsFacilities(latitude, longitude, radiusKm))
                .coolingCenters(findNearbyCoolingCenters(latitude, longitude, radiusKm))
                .latitude(latitude)
                .longitude(longitude)
                .radiusKm(radiusKm)
                .build();
    }

    /**
     * 관심사별 위치 기반 데이터 조회
     */
    public LocationBasedData findNearbyDataByInterests(Double latitude, Double longitude, 
                                                      Double radiusKm, List<String> interests) {
        log.debug("관심사별 위치 기반 데이터 조회: lat={}, lng={}, radius={}km, interests={}", 
                latitude, longitude, radiusKm, interests);

        LocationBasedData.LocationBasedDataBuilder builder = LocationBasedData.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radiusKm(radiusKm);

        // 관심사에 따라 선택적으로 데이터 조회
        if (interests.contains("RESTAURANTS")) {
            builder.restaurants(findNearbyRestaurants(latitude, longitude, radiusKm));
        }
        if (interests.contains("LIBRARIES")) {
            builder.libraries(findNearbyLibraries(latitude, longitude, radiusKm));
        }
        if (interests.contains("PARKS")) {
            builder.parks(findNearbyParks(latitude, longitude, radiusKm));
        }
        if (interests.contains("SPORTS_FACILITIES")) {
            builder.sportsFacilities(findNearbySportsFacilities(latitude, longitude, radiusKm));
        }
        if (interests.contains("COOLING_CENTERS")) {
            builder.coolingCenters(findNearbyCoolingCenters(latitude, longitude, radiusKm));
        }

        return builder.build();
    }

    /**
     * 위치 기반 데이터 통합 결과
     */
    @lombok.Builder
    @lombok.Getter
    public static class LocationBasedData {
        private final List<Restaurant> restaurants;
        private final List<Library> libraries;
        private final List<Park> parks;
        private final List<SportsFacility> sportsFacilities;
        private final List<CoolingCenter> coolingCenters;
        private final Double latitude;
        private final Double longitude;
        private final Double radiusKm;

        public int getTotalCount() {
            int count = 0;
            if (restaurants != null) count += restaurants.size();
            if (libraries != null) count += libraries.size();
            if (parks != null) count += parks.size();
            if (sportsFacilities != null) count += sportsFacilities.size();
            if (coolingCenters != null) count += coolingCenters.size();
            return count;
        }
    }
}
