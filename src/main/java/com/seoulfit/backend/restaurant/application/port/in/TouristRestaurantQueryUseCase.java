package com.seoulfit.backend.restaurant.application.port.in;

import com.seoulfit.backend.restaurant.domain.TouristRestaurant;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 관광 음식점 정보 조회 유스케이스
 * 헥사고날 아키텍처의 입력 포트
 */
public interface TouristRestaurantQueryUseCase {

    /**
     * 특정 날짜의 모든 음식점 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> findRestaurantsByDate(String dataDate);

    /**
     * 최신 음식점 정보 조회
     * @return 최신 음식점 정보 목록
     */
    List<TouristRestaurant> findLatestRestaurants();

    /**
     * 특정 언어의 음식점 정보 조회
     * @param langCodeId 언어 코드
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> findRestaurantsByLanguage(String langCodeId, String dataDate);

    /**
     * 음식점명으로 검색
     * @param restaurantName 음식점명 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> searchRestaurantsByName(String restaurantName, String dataDate);

    /**
     * 주소로 검색
     * @param address 주소 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> searchRestaurantsByAddress(String address, String dataDate);

    /**
     * 대표메뉴로 검색
     * @param menu 대표메뉴 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> searchRestaurantsByMenu(String menu, String dataDate);

    /**
     * 음식점 ID로 특정 음식점 정보 조회
     * @param id 음식점 ID
     * @return 음식점 정보
     */
    Optional<TouristRestaurant> findRestaurantById(Long id);

    /**
     * 웹사이트가 있는 음식점 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 웹사이트가 있는 음식점 정보 목록
     */
    List<TouristRestaurant> findRestaurantsWithWebsite(String dataDate);

    /**
     * 전화번호가 있는 음식점 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 전화번호가 있는 음식점 정보 목록
     */
    List<TouristRestaurant> findRestaurantsWithPhoneNumber(String dataDate);

    /**
     * 한국어 음식점 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 한국어 음식점 정보 목록
     */
    List<TouristRestaurant> findKoreanRestaurants(String dataDate);

    /**
     * 영어 음식점 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 영어 음식점 정보 목록
     */
    List<TouristRestaurant> findEnglishRestaurants(String dataDate);

    /**
     * 사용 가능한 데이터 날짜 목록 조회
     * @return 데이터 날짜 목록 (최신순)
     */
    List<String> getAvailableDataDates();

    /**
     * 특정 날짜의 음식점 데이터 통계 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 음식점 데이터 통계
     */
    RestaurantDataStatistics getRestaurantStatistics(String dataDate);

    /**
     * 음식점 데이터 통계 DTO
     */
    record RestaurantDataStatistics(
        String dataDate,
        long totalRestaurants,
        long restaurantsWithWebsite,
        long restaurantsWithPhoneNumber,
        long koreanRestaurants,
        long englishRestaurants,
        List<String> languages,
        long restaurantsPerLanguage
    ) {}
}
