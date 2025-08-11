package com.seoulfit.backend.publicdata.restaurant.application.port.out;

import com.seoulfit.backend.publicdata.restaurant.domain.TouristRestaurant;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 관광 음식점 정보 저장소 포트
 * 헥사고날 아키텍처의 출력 포트로, 도메인 계층에서 인프라스트럭처 계층으로의 의존성을 역전시킴
 */
public interface TouristRestaurantRepository {

    /**
     * 관광 음식점 정보 저장
     * @param restaurant 저장할 음식점 정보
     * @return 저장된 음식점 정보
     */
    TouristRestaurant save(TouristRestaurant restaurant);

    /**
     * 관광 음식점 정보 목록 저장
     * @param restaurants 저장할 음식점 정보 목록
     * @return 저장된 음식점 정보 목록
     */
    List<TouristRestaurant> saveAll(List<TouristRestaurant> restaurants);

    /**
     * ID로 조회
     * @param id 음식점 ID
     * @return 음식점 정보
     */
    Optional<TouristRestaurant> findById(Long id);

    /**
     * 고유번호와 데이터 날짜로 조회
     * @param postSn 고유번호
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 음식점 정보
     */
    Optional<TouristRestaurant> findByPostSnAndDataDate(String postSn, String dataDate);

    /**
     * 특정 날짜의 모든 음식점 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> findByDataDate(String dataDate);

    /**
     * 특정 언어의 음식점 정보 조회
     * @param langCodeId 언어 코드
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> findByLangCodeIdAndDataDate(String langCodeId, String dataDate);

    /**
     * 음식점명으로 검색
     * @param restaurantName 음식점명 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> findByRestaurantNameContainingAndDataDate(String restaurantName, String dataDate);

    /**
     * 주소로 검색
     * @param address 주소 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> findByAddressContainingAndDataDate(String address, String dataDate);

    /**
     * 대표메뉴로 검색
     * @param menu 대표메뉴 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 음식점 정보 목록
     */
    List<TouristRestaurant> findByRepresentativeMenuContainingAndDataDate(String menu, String dataDate);

    /**
     * 웹사이트가 있는 음식점 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 웹사이트가 있는 음식점 정보 목록
     */
    List<TouristRestaurant> findByWebsiteUrlIsNotNullAndDataDate(String dataDate);

    /**
     * 전화번호가 있는 음식점 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 전화번호가 있는 음식점 정보 목록
     */
    List<TouristRestaurant> findByPhoneNumberIsNotNullAndDataDate(String dataDate);

    /**
     * 특정 날짜 이전의 데이터 삭제
     * @param beforeDate 삭제 기준 날짜 (YYYYMMDD)
     * @return 삭제된 레코드 수
     */
    int deleteByDataDateBefore(String beforeDate);

    /**
     * 특정 날짜의 데이터 개수 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 데이터 개수
     */
    long countByDataDate(String dataDate);

    /**
     * 모든 데이터 날짜 목록 조회 (최신순)
     * @return 데이터 날짜 목록
     */
    List<String> findAllDataDatesOrderByDesc();

    /**
     * 언어별 음식점 개수 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 언어별 음식점 개수 목록
     */
    List<Object[]> countByLangCodeIdAndDataDate(String dataDate);
}
