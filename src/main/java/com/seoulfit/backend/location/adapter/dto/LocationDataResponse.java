package com.seoulfit.backend.location.adapter.dto;

import com.seoulfit.backend.location.application.LocationBasedDataService;
import com.seoulfit.backend.location.domain.*;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 위치 기반 데이터 응답 DTO
 * 
 * 지도에 마커를 표시하기 위한 위치 기반 데이터 응답
 * 사용자 관심사에 따른 맞춤형 데이터 제공
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Schema(description = "위치 기반 통합 데이터 응답")
@Getter
@Builder
public class LocationDataResponse {

    @Schema(description = "맛집 목록")
    private final List<RestaurantInfo> restaurants;

    @Schema(description = "도서관 목록")
    private final List<LibraryInfo> libraries;

    @Schema(description = "공원 목록")
    private final List<ParkInfo> parks;

    @Schema(description = "체육시설 목록")
    private final List<SportsFacilityInfo> sportsFacilities;

    @Schema(description = "무더위쉼터 목록")
    private final List<CoolingCenterInfo> coolingCenters;

    @Schema(description = "검색 중심 위도", example = "37.5665")
    private final Double latitude;

    @Schema(description = "검색 중심 경도", example = "126.9780")
    private final Double longitude;

    @Schema(description = "검색 반경 (km)", example = "2.0")
    private final Double radiusKm;

    @Schema(description = "총 데이터 개수", example = "25")
    private final Integer totalCount;

    /**
     * LocationBasedData로부터 응답 DTO 생성
     */
    public static LocationDataResponse from(LocationBasedDataService.LocationBasedData data) {
        return LocationDataResponse.builder()
                .restaurants(data.getRestaurants() != null ? 
                    data.getRestaurants().stream().map(RestaurantInfo::from).toList() : List.of())
                .libraries(data.getLibraries() != null ? 
                    data.getLibraries().stream().map(LibraryInfo::from).toList() : List.of())
                .parks(data.getParks() != null ? 
                    data.getParks().stream().map(ParkInfo::from).toList() : List.of())
                .sportsFacilities(data.getSportsFacilities() != null ? 
                    data.getSportsFacilities().stream().map(SportsFacilityInfo::from).toList() : List.of())
                .coolingCenters(data.getCoolingCenters() != null ? 
                    data.getCoolingCenters().stream().map(CoolingCenterInfo::from).toList() : List.of())
                .latitude(data.getLatitude())
                .longitude(data.getLongitude())
                .radiusKm(data.getRadiusKm())
                .totalCount(data.getTotalCount())
                .build();
    }

    // 개별 정보 클래스들

    /**
     * 맛집 정보
     */
    @Schema(description = "맛집 정보")
    @Getter
    @Builder
    public static class RestaurantInfo {
        @Schema(description = "맛집 ID", example = "1")
        private final Long id;

        @Schema(description = "상호명", example = "서울식당")
        private final String name;

        @Schema(description = "주소", example = "서울시 중구 명동길 123")
        private final String address;

        @Schema(description = "전화번호", example = "02-1234-5678")
        private final String phone;

        @Schema(description = "위도", example = "37.5665")
        private final Double latitude;

        @Schema(description = "경도", example = "126.9780")
        private final Double longitude;

        @Schema(description = "대표메뉴", example = "김치찌개, 불고기")
        private final String representativeMenu;

        @Schema(description = "운영시간", example = "09:00~22:00")
        private final String operatingHours;

        @Schema(description = "웹사이트", example = "https://restaurant.com")
        private final String website;

        public static RestaurantInfo from(Restaurant restaurant) {
            return RestaurantInfo.builder()
                    .id(restaurant.getId())
                    .name(restaurant.getName())
                    .address(restaurant.getAddress())
                    .phone(restaurant.getPhone())
                    .latitude(restaurant.getLatitude())
                    .longitude(restaurant.getLongitude())
                    .representativeMenu(restaurant.getRepresentativeMenu())
                    .operatingHours(restaurant.getOperatingHours())
                    .website(restaurant.getWebsite())
                    .build();
        }
    }

    /**
     * 도서관 정보
     */
    @Schema(description = "도서관 정보")
    @Getter
    @Builder
    public static class LibraryInfo {
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

        @Schema(description = "전화번호", example = "02-590-0500")
        private final String phone;

        @Schema(description = "홈페이지", example = "https://www.nl.go.kr")
        private final String website;

        @Schema(description = "운영시간", example = "09:00~18:00")
        private final String operatingHours;

        public static LibraryInfo from(Library library) {
            return LibraryInfo.builder()
                    .id(library.getId())
                    .name(library.getLbrryName())
                    .district(library.getCodeValue())
                    .address(library.getAdres())
                    .latitude(library.getXcnts())
                    .longitude(library.getYdnts())
                    .phone(library.getTelNo())
                    .website(library.getHmpgUrl())
                    .operatingHours(library.getOpTime())
                    .build();
        }
    }

    /**
     * 공원 정보
     */
    @Schema(description = "공원 정보")
    @Getter
    @Builder
    public static class ParkInfo {
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

        @Schema(description = "면적", example = "1,000,000㎡")
        private final String area;

        @Schema(description = "주요시설", example = "산책로, 자전거도로, 체육시설")
        private final String mainEquipment;

        @Schema(description = "개원일", example = "1982-01-01")
        private final LocalDate openDate;

        @Schema(description = "관리부서 전화번호", example = "02-3780-0561")
        private final String adminTel;

        public static ParkInfo from(Park park) {
            return ParkInfo.builder()
                    .id(park.getId())
                    .name(park.getName())
                    .address(park.getAddress())
                    .latitude(park.getLatitude())
                    .longitude(park.getLongitude())
                    .area(park.getArea())
                    .mainEquipment(park.getMainEquipment())
                    .openDate(park.getOpenDate())
                    .adminTel(park.getAdminTel())
                    .build();
        }
    }

    /**
     * 체육시설 정보
     */
    @Schema(description = "체육시설 정보")
    @Getter
    @Builder
    public static class SportsFacilityInfo {
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

        @Schema(description = "시설유형", example = "체육관")
        private final String facilityType;

        @Schema(description = "연락처", example = "02-410-1114")
        private final String phone;

        @Schema(description = "평일 운영시간", example = "06:00~22:00")
        private final String weekdayHours;

        @Schema(description = "주말 운영시간", example = "06:00~18:00")
        private final String weekendHours;

        @Schema(description = "시설사용료", example = "개인 2,000원/1시간")
        private final String usageFee;

        public static SportsFacilityInfo from(SportsFacility facility) {
            return SportsFacilityInfo.builder()
                    .id(facility.getId())
                    .name(facility.getName())
                    .district(facility.getDistrict())
                    .address(facility.getAddress())
                    .latitude(facility.getLatitude())
                    .longitude(facility.getLongitude())
                    .facilityType(facility.getFacilityType())
                    .phone(facility.getPhone())
                    .weekdayHours(facility.getWeekdayHours())
                    .weekendHours(facility.getWeekendHours())
                    .usageFee(facility.getUsageFee())
                    .build();
        }
    }

    /**
     * 무더위쉼터 정보
     */
    @Schema(description = "무더위쉼터 정보")
    @Getter
    @Builder
    public static class CoolingCenterInfo {
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

        @Schema(description = "시설구분1", example = "공공시설")
        private final String facilityType1;

        @Schema(description = "시설구분2", example = "주민센터")
        private final String facilityType2;

        @Schema(description = "이용가능인원", example = "50")
        private final Integer capacity;

        @Schema(description = "비고", example = "에어컨 완비, 무료 이용")
        private final String remarks;

        public static CoolingCenterInfo from(CoolingCenter center) {
            return CoolingCenterInfo.builder()
                    .id(center.getId())
                    .name(center.getName())
                    .address(center.getRoadAddress())
                    .latitude(center.getLatitude())
                    .longitude(center.getLongitude())
                    .facilityType1(center.getFacilityType1())
                    .facilityType2(center.getFacilityType2())
                    .capacity(center.getCapacity())
                    .remarks(center.getRemarks())
                    .build();
        }
    }

    // 개별 응답 클래스들

    /**
     * 맛집만 포함하는 응답
     */
    @Schema(description = "맛집 응답")
    @Getter
    @Builder
    public static class RestaurantsResponse {
        @Schema(description = "맛집 목록")
        private final List<RestaurantInfo> restaurants;

        @Schema(description = "총 개수")
        private final Integer count;

        public static RestaurantsResponse from(List<Restaurant> restaurants) {
            return RestaurantsResponse.builder()
                    .restaurants(restaurants.stream().map(RestaurantInfo::from).toList())
                    .count(restaurants.size())
                    .build();
        }
    }

    /**
     * 도서관만 포함하는 응답
     */
    @Schema(description = "도서관 응답")
    @Getter
    @Builder
    public static class LibrariesResponse {
        @Schema(description = "도서관 목록")
        private final List<LibraryInfo> libraries;

        @Schema(description = "총 개수")
        private final Integer count;

        public static LibrariesResponse from(List<Library> libraries) {
            return LibrariesResponse.builder()
                    .libraries(libraries.stream().map(LibraryInfo::from).toList())
                    .count(libraries.size())
                    .build();
        }
    }

    /**
     * 공원만 포함하는 응답
     */
    @Schema(description = "공원 응답")
    @Getter
    @Builder
    public static class ParksResponse {
        @Schema(description = "공원 목록")
        private final List<ParkInfo> parks;

        @Schema(description = "총 개수")
        private final Integer count;

        public static ParksResponse from(List<Park> parks) {
            return ParksResponse.builder()
                    .parks(parks.stream().map(ParkInfo::from).toList())
                    .count(parks.size())
                    .build();
        }
    }

    /**
     * 체육시설만 포함하는 응답
     */
    @Schema(description = "체육시설 응답")
    @Getter
    @Builder
    public static class SportsFacilitiesResponse {
        @Schema(description = "체육시설 목록")
        private final List<SportsFacilityInfo> sportsFacilities;

        @Schema(description = "총 개수")
        private final Integer count;

        public static SportsFacilitiesResponse from(List<SportsFacility> facilities) {
            return SportsFacilitiesResponse.builder()
                    .sportsFacilities(facilities.stream().map(SportsFacilityInfo::from).toList())
                    .count(facilities.size())
                    .build();
        }
    }

    /**
     * 무더위쉼터만 포함하는 응답
     */
    @Schema(description = "무더위쉼터 응답")
    @Getter
    @Builder
    public static class CoolingCentersResponse {
        @Schema(description = "무더위쉼터 목록")
        private final List<CoolingCenterInfo> coolingCenters;

        @Schema(description = "총 개수")
        private final Integer count;

        public static CoolingCentersResponse from(List<CoolingCenter> centers) {
            return CoolingCentersResponse.builder()
                    .coolingCenters(centers.stream().map(CoolingCenterInfo::from).toList())
                    .count(centers.size())
                    .build();
        }
    }
}
