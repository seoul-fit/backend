package com.seoulfit.backend.location.adapter.dto;

import com.seoulfit.backend.location.application.AdvancedLocationDataService;
import com.seoulfit.backend.location.domain.*;
import com.seoulfit.backend.location.util.GeoUtils;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.user.domain.InterestCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 고도화된 위치 기반 데이터 응답 DTO
 * 
 * 거리 정보와 지도 축적 정보를 포함한 고급 위치 기반 데이터 응답
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "고도화된 위치 기반 데이터 응답")
@Getter
@Builder
public class AdvancedLocationResponse {

    @Schema(description = "검색 중심점 위도", example = "37.5665")
    private final Double centerLatitude;

    @Schema(description = "검색 중심점 경도", example = "126.9780")
    private final Double centerLongitude;

    @Schema(description = "사용된 지도 축적 정보")
    private final MapScaleInfo mapScaleInfo;

    @Schema(description = "바운딩 박스 정보")
    private final BoundingBoxInfo boundingBoxInfo;

    @Schema(description = "맛집 목록 (거리 정보 포함)")
    private final List<RestaurantWithDistance> restaurants;

    @Schema(description = "도서관 목록 (거리 정보 포함)")
    private final List<LibraryWithDistance> libraries;

    @Schema(description = "공원 목록 (거리 정보 포함)")
    private final List<ParkWithDistance> parks;

    @Schema(description = "체육시설 목록 (거리 정보 포함)")
    private final List<SportsFacilityWithDistance> sportsFacilities;

    @Schema(description = "무더위쉼터 목록 (거리 정보 포함)")
    private final List<CoolingCenterWithDistance> coolingCenters;

    @Schema(description = "사용자 관심사 목록")
    private final List<String> userInterests;

    @Schema(description = "총 데이터 개수", example = "87")
    private final Integer totalCount;

    @Schema(description = "평균 거리 (km)", example = "1.25")
    private final Double averageDistance;

    @Schema(description = "최근접 거리 (km)", example = "0.15")
    private final Double nearestDistance;

    @Schema(description = "최원거리 (km)", example = "2.85")
    private final Double farthestDistance;

    /**
     * AdvancedLocationData로부터 응답 DTO 생성
     */
    public static AdvancedLocationResponse from(AdvancedLocationDataService.AdvancedLocationData data, 
                                              List<InterestCategory> userInterests) {
        return AdvancedLocationResponse.builder()
                .centerLatitude(data.getCenterLatitude())
                .centerLongitude(data.getCenterLongitude())
                .mapScaleInfo(MapScaleInfo.from(data.getMapScale()))
                .boundingBoxInfo(BoundingBoxInfo.from(data.getBoundingBox()))
                .restaurants(data.getRestaurants() != null ? 
                    data.getRestaurants().stream().map(RestaurantWithDistance::from).toList() : List.of())
                .libraries(data.getLibraries() != null ? 
                    data.getLibraries().stream().map(LibraryWithDistance::from).toList() : List.of())
                .parks(data.getParks() != null ? 
                    data.getParks().stream().map(ParkWithDistance::from).toList() : List.of())
                .sportsFacilities(data.getSportsFacilities() != null ? 
                    data.getSportsFacilities().stream().map(SportsFacilityWithDistance::from).toList() : List.of())
                .coolingCenters(data.getCoolingCenters() != null ? 
                    data.getCoolingCenters().stream().map(CoolingCenterWithDistance::from).toList() : List.of())
                .userInterests(userInterests.stream().map(Enum::name).toList())
                .totalCount(data.getTotalCount())
                .averageDistance(data.getAverageDistance())
                .nearestDistance(calculateNearestDistance(data))
                .farthestDistance(calculateFarthestDistance(data))
                .build();
    }

    /**
     * 지도 축적 정보
     */
    @Schema(description = "지도 축적 정보")
    @Getter
    @Builder
    public static class MapScaleInfo {
        @Schema(description = "축적 레벨", example = "6")
        private final Integer level;

        @Schema(description = "축적 값", example = "2000")
        private final Integer scaleValue;

        @Schema(description = "검색 반경 (km)", example = "2.0")
        private final Double radiusKm;

        @Schema(description = "표시명", example = "근거리 지역")
        private final String displayName;

        @Schema(description = "설명", example = "인근 동 단위")
        private final String description;

        public static MapScaleInfo from(MapScale mapScale) {
            return MapScaleInfo.builder()
                    .level(mapScale.getLevel())
                    .scaleValue(mapScale.getScaleValue())
                    .radiusKm(mapScale.getRadiusKm())
                    .displayName(mapScale.getDisplayName())
                    .description(mapScale.getDescription())
                    .build();
        }
    }

    /**
     * 바운딩 박스 정보
     */
    @Schema(description = "바운딩 박스 정보")
    @Getter
    @Builder
    public static class BoundingBoxInfo {
        @Schema(description = "최소 위도", example = "37.5485")
        private final Double minLatitude;

        @Schema(description = "최대 위도", example = "37.5845")
        private final Double maxLatitude;

        @Schema(description = "최소 경도", example = "126.9600")
        private final Double minLongitude;

        @Schema(description = "최대 경도", example = "126.9960")
        private final Double maxLongitude;

        @Schema(description = "너비 (km)", example = "2.52")
        private final Double widthKm;

        @Schema(description = "높이 (km)", example = "4.00")
        private final Double heightKm;

        public static BoundingBoxInfo from(MapScale.BoundingBox boundingBox) {
            double[] sizeKm = boundingBox.getSizeKm();
            return BoundingBoxInfo.builder()
                    .minLatitude(boundingBox.getMinLat())
                    .maxLatitude(boundingBox.getMaxLat())
                    .minLongitude(boundingBox.getMinLng())
                    .maxLongitude(boundingBox.getMaxLng())
                    .widthKm(sizeKm[0])
                    .heightKm(sizeKm[1])
                    .build();
        }
    }

    // 거리 정보를 포함한 각 엔티티 정보 클래스들

    /**
     * 거리 정보를 포함한 맛집 정보
     */
    @Schema(description = "거리 정보를 포함한 맛집 정보")
    @Getter
    @Builder
    public static class RestaurantWithDistance {
        @Schema(description = "맛집 ID", example = "1")
        private final Long id;

        @Schema(description = "상호명", example = "서울식당")
        private final String name;

        @Schema(description = "주소", example = "서울시 중구 명동길 123")
        private final String address;

        @Schema(description = "위도", example = "37.5665")
        private final Double latitude;

        @Schema(description = "경도", example = "126.9780")
        private final Double longitude;

        @Schema(description = "거리 (km)", example = "0.85")
        private final Double distance;

        @Schema(description = "대표메뉴", example = "김치찌개, 불고기")
        private final String representativeMenu;

        @Schema(description = "운영시간", example = "09:00~22:00")
        private final String operatingHours;

        public static RestaurantWithDistance from(GeoUtils.GeoPointWithDistance<Restaurant> data) {
            Restaurant restaurant = data.getPoint();
            return RestaurantWithDistance.builder()
                    .id(restaurant.getId())
                    .name(restaurant.getName())
                    .address(restaurant.getAddress())
                    .latitude(restaurant.getLatitude())
                    .longitude(restaurant.getLongitude())
                    .distance(Math.round(data.getDistance() * 100.0) / 100.0) // 소수점 2자리
                    .representativeMenu(restaurant.getRepresentativeMenu())
                    .operatingHours(restaurant.getOperatingHours())
                    .build();
        }
    }

    /**
     * 거리 정보를 포함한 도서관 정보
     */
    @Schema(description = "거리 정보를 포함한 도서관 정보")
    @Getter
    @Builder
    public static class LibraryWithDistance {
        @Schema(description = "도서관 ID", example = "1")
        private final Long id;

        @Schema(description = "도서관명", example = "국립중앙도서관")
        private final String name;

        @Schema(description = "구명", example = "서초구")
        private final String district;

        @Schema(description = "주소", example = "서울시 서초구 반포대로 201")
        private final String address;

        @Schema(description = "위도", example = "37.5056")
        private final Double latitude;

        @Schema(description = "경도", example = "127.0369")
        private final Double longitude;

        @Schema(description = "거리 (km)", example = "1.25")
        private final Double distance;

        @Schema(description = "운영시간", example = "09:00~18:00")
        private final String operatingHours;

        public static LibraryWithDistance from(GeoUtils.GeoPointWithDistance<Library> data) {
            Library library = data.getPoint();
            return LibraryWithDistance.builder()
                    .id(library.getId())
                    .name(library.getLbrryName())
                    .district(library.getCodeValue())
                    .address(library.getAdres())
                    .latitude(library.getXcnts())
                    .longitude(library.getYdnts())
                    .distance(Math.round(data.getDistance() * 100.0) / 100.0)
                    .operatingHours(library.getOpTime())
                    .build();
        }
    }

    /**
     * 거리 정보를 포함한 공원 정보
     */
    @Schema(description = "거리 정보를 포함한 공원 정보")
    @Getter
    @Builder
    public static class ParkWithDistance {
        @Schema(description = "공원 ID", example = "1")
        private final Long id;

        @Schema(description = "공원명", example = "한강공원")
        private final String name;

        @Schema(description = "주소", example = "서울시 영등포구 여의동로 330")
        private final String address;

        @Schema(description = "위도", example = "37.5219")
        private final Double latitude;

        @Schema(description = "경도", example = "126.9316")
        private final Double longitude;

        @Schema(description = "거리 (km)", example = "0.65")
        private final Double distance;

        @Schema(description = "면적", example = "1,000,000㎡")
        private final String area;

        @Schema(description = "주요시설", example = "산책로, 자전거도로, 체육시설")
        private final String mainEquipment;

        public static ParkWithDistance from(GeoUtils.GeoPointWithDistance<Park> data) {
            Park park = data.getPoint();
            return ParkWithDistance.builder()
                    .id(park.getId())
                    .name(park.getName())
                    .address(park.getAddress())
                    .latitude(park.getLatitude())
                    .longitude(park.getLongitude())
                    .distance(Math.round(data.getDistance() * 100.0) / 100.0)
                    .area(park.getArea())
                    .mainEquipment(park.getMainEquipment())
                    .build();
        }
    }

    /**
     * 거리 정보를 포함한 체육시설 정보
     */
    @Schema(description = "거리 정보를 포함한 체육시설 정보")
    @Getter
    @Builder
    public static class SportsFacilityWithDistance {
        @Schema(description = "체육시설 ID", example = "1")
        private final Long id;

        @Schema(description = "시설명", example = "서울시민체육관")
        private final String name;

        @Schema(description = "자치구", example = "송파구")
        private final String district;

        @Schema(description = "주소", example = "서울시 송파구 올림픽로 424")
        private final String address;

        @Schema(description = "위도", example = "37.5145")
        private final Double latitude;

        @Schema(description = "경도", example = "127.1026")
        private final Double longitude;

        @Schema(description = "거리 (km)", example = "1.85")
        private final Double distance;

        @Schema(description = "시설유형", example = "체육관")
        private final String facilityType;

        @Schema(description = "평일 운영시간", example = "06:00~22:00")
        private final String weekdayHours;

        public static SportsFacilityWithDistance from(GeoUtils.GeoPointWithDistance<SportsFacility> data) {
            SportsFacility facility = data.getPoint();
            return SportsFacilityWithDistance.builder()
                    .id(facility.getId())
                    .name(facility.getName())
                    .district(facility.getDistrict())
                    .address(facility.getAddress())
                    .latitude(facility.getLatitude())
                    .longitude(facility.getLongitude())
                    .distance(Math.round(data.getDistance() * 100.0) / 100.0)
                    .facilityType(facility.getFacilityType())
                    .weekdayHours(facility.getWeekdayHours())
                    .build();
        }
    }

    /**
     * 거리 정보를 포함한 무더위쉼터 정보
     */
    @Schema(description = "거리 정보를 포함한 무더위쉼터 정보")
    @Getter
    @Builder
    public static class CoolingCenterWithDistance {
        @Schema(description = "무더위쉼터 ID", example = "1")
        private final Long id;

        @Schema(description = "쉼터명칭", example = "○○동 주민센터")
        private final String name;

        @Schema(description = "도로명주소", example = "서울시 중구 세종대로 110")
        private final String address;

        @Schema(description = "위도", example = "37.5663")
        private final Double latitude;

        @Schema(description = "경도", example = "126.9779")
        private final Double longitude;

        @Schema(description = "거리 (km)", example = "0.35")
        private final Double distance;

        @Schema(description = "시설구분1", example = "공공시설")
        private final String facilityType1;

        @Schema(description = "이용가능인원", example = "50")
        private final Integer capacity;

        public static CoolingCenterWithDistance from(GeoUtils.GeoPointWithDistance<CoolingCenter> data) {
            CoolingCenter center = data.getPoint();
            return CoolingCenterWithDistance.builder()
                    .id(center.getId())
                    .name(center.getName())
                    .address(center.getRoadAddress())
                    .latitude(center.getLatitude())
                    .longitude(center.getLongitude())
                    .distance(Math.round(data.getDistance() * 100.0) / 100.0)
                    .facilityType1(center.getFacilityType1())
                    .capacity(center.getCapacity())
                    .build();
        }
    }

    // 유틸리티 메서드들

    private static Double calculateNearestDistance(AdvancedLocationDataService.AdvancedLocationData data) {
        double nearest = Double.MAX_VALUE;

        if (data.getRestaurants() != null) {
            nearest = Math.min(nearest, data.getRestaurants().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).min().orElse(Double.MAX_VALUE));
        }
        if (data.getLibraries() != null) {
            nearest = Math.min(nearest, data.getLibraries().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).min().orElse(Double.MAX_VALUE));
        }
        if (data.getParks() != null) {
            nearest = Math.min(nearest, data.getParks().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).min().orElse(Double.MAX_VALUE));
        }
        if (data.getSportsFacilities() != null) {
            nearest = Math.min(nearest, data.getSportsFacilities().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).min().orElse(Double.MAX_VALUE));
        }
        if (data.getCoolingCenters() != null) {
            nearest = Math.min(nearest, data.getCoolingCenters().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).min().orElse(Double.MAX_VALUE));
        }

        return nearest == Double.MAX_VALUE ? 0.0 : Math.round(nearest * 100.0) / 100.0;
    }

    private static Double calculateFarthestDistance(AdvancedLocationDataService.AdvancedLocationData data) {
        double farthest = 0.0;

        if (data.getRestaurants() != null) {
            farthest = Math.max(farthest, data.getRestaurants().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).max().orElse(0.0));
        }
        if (data.getLibraries() != null) {
            farthest = Math.max(farthest, data.getLibraries().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).max().orElse(0.0));
        }
        if (data.getParks() != null) {
            farthest = Math.max(farthest, data.getParks().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).max().orElse(0.0));
        }
        if (data.getSportsFacilities() != null) {
            farthest = Math.max(farthest, data.getSportsFacilities().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).max().orElse(0.0));
        }
        if (data.getCoolingCenters() != null) {
            farthest = Math.max(farthest, data.getCoolingCenters().stream()
                    .mapToDouble(GeoUtils.GeoPointWithDistance::getDistance).max().orElse(0.0));
        }

        return Math.round(farthest * 100.0) / 100.0;
    }
}
