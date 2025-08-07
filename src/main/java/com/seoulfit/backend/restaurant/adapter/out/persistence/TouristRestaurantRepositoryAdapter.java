package com.seoulfit.backend.restaurant.adapter.out.persistence;

import com.seoulfit.backend.restaurant.application.port.out.TouristRestaurantRepository;
import com.seoulfit.backend.restaurant.domain.TouristRestaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 관광 음식점 정보 Repository 어댑터
 * 헥사고날 아키텍처의 출력 어댑터
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TouristRestaurantRepositoryAdapter implements TouristRestaurantRepository {

    private final TouristRestaurantJpaRepository jpaRepository;

    @Override
    public TouristRestaurant save(TouristRestaurant restaurant) {
        log.debug("음식점 정보 저장 - 고유번호: {}, 상호명: {}", 
            restaurant.getPostSn(), restaurant.getRestaurantName());
        return jpaRepository.save(restaurant);
    }

    @Override
    public List<TouristRestaurant> saveAll(List<TouristRestaurant> restaurants) {
        log.debug("음식점 정보 일괄 저장 - 개수: {}", restaurants.size());
        return jpaRepository.saveAll(restaurants);
    }

    @Override
    public Optional<TouristRestaurant> findById(Long id) {
        log.debug("음식점 정보 조회 - ID: {}", id);
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<TouristRestaurant> findByPostSnAndDataDate(String postSn, String dataDate) {
        log.debug("음식점 정보 조회 - 고유번호: {}, 날짜: {}", postSn, dataDate);
        return jpaRepository.findByPostSnAndDataDate(postSn, dataDate);
    }

    @Override
    public List<TouristRestaurant> findByDataDate(String dataDate) {
        log.debug("날짜별 음식점 정보 조회 - 날짜: {}", dataDate);
        return jpaRepository.findByDataDate(dataDate);
    }

    @Override
    public List<TouristRestaurant> findByLangCodeIdAndDataDate(String langCodeId, String dataDate) {
        log.debug("언어별 음식점 정보 조회 - 언어: {}, 날짜: {}", langCodeId, dataDate);
        return jpaRepository.findByLangCodeIdAndDataDate(langCodeId, dataDate);
    }

    @Override
    public List<TouristRestaurant> findByRestaurantNameContainingAndDataDate(String restaurantName, String dataDate) {
        log.debug("음식점명 검색 - 검색어: {}, 날짜: {}", restaurantName, dataDate);
        return jpaRepository.findByRestaurantNameContainingAndDataDate(restaurantName, dataDate);
    }

    @Override
    public List<TouristRestaurant> findByAddressContainingAndDataDate(String address, String dataDate) {
        log.debug("주소 검색 - 검색어: {}, 날짜: {}", address, dataDate);
        return jpaRepository.findByAddressContainingAndDataDate(address, dataDate);
    }

    @Override
    public List<TouristRestaurant> findByRepresentativeMenuContainingAndDataDate(String menu, String dataDate) {
        log.debug("대표메뉴 검색 - 검색어: {}, 날짜: {}", menu, dataDate);
        return jpaRepository.findByRepresentativeMenuContainingAndDataDate(menu, dataDate);
    }

    @Override
    public List<TouristRestaurant> findByWebsiteUrlIsNotNullAndDataDate(String dataDate) {
        log.debug("웹사이트가 있는 음식점 조회 - 날짜: {}", dataDate);
        return jpaRepository.findByWebsiteUrlIsNotNullAndDataDate(dataDate);
    }

    @Override
    public List<TouristRestaurant> findByPhoneNumberIsNotNullAndDataDate(String dataDate) {
        log.debug("전화번호가 있는 음식점 조회 - 날짜: {}", dataDate);
        return jpaRepository.findByPhoneNumberIsNotNullAndDataDate(dataDate);
    }

    @Override
    public int deleteByDataDateBefore(String beforeDate) {
        log.debug("이전 데이터 삭제 - 기준 날짜: {}", beforeDate);
        return jpaRepository.deleteByDataDateBefore(beforeDate);
    }

    @Override
    public long countByDataDate(String dataDate) {
        log.debug("날짜별 데이터 개수 조회 - 날짜: {}", dataDate);
        return jpaRepository.countByDataDate(dataDate);
    }

    @Override
    public List<String> findAllDataDatesOrderByDesc() {
        log.debug("사용 가능한 데이터 날짜 목록 조회");
        return jpaRepository.findAllDataDatesOrderByDesc();
    }

    @Override
    public List<Object[]> countByLangCodeIdAndDataDate(String dataDate) {
        log.debug("언어별 음식점 개수 조회 - 날짜: {}", dataDate);
        return jpaRepository.countByLangCodeIdAndDataDate(dataDate);
    }
}
