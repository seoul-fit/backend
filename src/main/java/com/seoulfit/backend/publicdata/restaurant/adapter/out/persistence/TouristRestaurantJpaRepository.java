package com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence;

import com.seoulfit.backend.publicdata.restaurant.domain.TouristRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 관광 음식점 정보 JPA Repository
 */
public interface TouristRestaurantJpaRepository extends JpaRepository<TouristRestaurant, Long> {

    /**
     * 고유번호와 데이터 날짜로 조회
     */
    Optional<TouristRestaurant> findByPostSnAndDataDate(String postSn, String dataDate);

    /**
     * 특정 날짜의 모든 음식점 정보 조회
     */
    List<TouristRestaurant> findByDataDate(String dataDate);

    /**
     * 특정 언어의 음식점 정보 조회
     */
    List<TouristRestaurant> findByLangCodeIdAndDataDate(String langCodeId, String dataDate);

    /**
     * 음식점명으로 검색 (부분 검색)
     */
    List<TouristRestaurant> findByRestaurantNameContainingAndDataDate(String restaurantName, String dataDate);

    /**
     * 주소로 검색 (부분 검색)
     */
    List<TouristRestaurant> findByAddressContainingAndDataDate(String address, String dataDate);

    /**
     * 대표메뉴로 검색 (부분 검색)
     */
    List<TouristRestaurant> findByRepresentativeMenuContainingAndDataDate(String menu, String dataDate);

    /**
     * 웹사이트가 있는 음식점 조회
     */
    List<TouristRestaurant> findByWebsiteUrlIsNotNullAndDataDate(String dataDate);

    /**
     * 전화번호가 있는 음식점 조회
     */
    List<TouristRestaurant> findByPhoneNumberIsNotNullAndDataDate(String dataDate);

    /**
     * 특정 날짜 이전의 데이터 삭제
     */
    @Modifying
    @Query("DELETE FROM TouristRestaurant tr WHERE tr.dataDate < :beforeDate")
    int deleteByDataDateBefore(@Param("beforeDate") String beforeDate);

    /**
     * 특정 날짜의 데이터 개수 조회
     */
    long countByDataDate(String dataDate);

    /**
     * 모든 데이터 날짜 목록 조회 (최신순)
     */
    @Query("SELECT DISTINCT tr.dataDate FROM TouristRestaurant tr ORDER BY tr.dataDate DESC")
    List<String> findAllDataDatesOrderByDesc();

    /**
     * 언어별 음식점 개수 조회
     */
    @Query("SELECT tr.langCodeId, COUNT(tr) FROM TouristRestaurant tr WHERE tr.dataDate = :dataDate GROUP BY tr.langCodeId")
    List<Object[]> countByLangCodeIdAndDataDate(@Param("dataDate") String dataDate);
}
