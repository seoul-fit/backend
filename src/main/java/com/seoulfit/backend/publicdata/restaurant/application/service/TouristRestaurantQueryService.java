package com.seoulfit.backend.publicdata.restaurant.application.service;

import com.seoulfit.backend.publicdata.restaurant.application.port.in.TouristRestaurantQueryUseCase;
import com.seoulfit.backend.publicdata.restaurant.application.port.out.TouristRestaurantRepository;
import com.seoulfit.backend.publicdata.restaurant.domain.TouristRestaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 서울시 관광 음식점 정보 조회 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TouristRestaurantQueryService implements TouristRestaurantQueryUseCase {

    private final TouristRestaurantRepository repository;

    @Override
    public List<TouristRestaurant> findRestaurantsByDate(String dataDate) {
        log.debug("특정 날짜 음식점 정보 조회 - 날짜: {}", dataDate);
        return repository.findByDataDate(dataDate);
    }

    @Override
    public List<TouristRestaurant> findLatestRestaurants() {
        log.debug("최신 음식점 정보 조회");
        List<String> availableDates = repository.findAllDataDatesOrderByDesc();
        
        if (availableDates.isEmpty()) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        String latestDate = availableDates.get(0);
        log.debug("최신 데이터 날짜: {}", latestDate);
        
        return repository.findByDataDate(latestDate);
    }

    @Override
    public List<TouristRestaurant> findRestaurantsByLanguage(String langCodeId, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("언어별 음식점 정보 조회 - 언어: {}, 날짜: {}", langCodeId, targetDate);
        return repository.findByLangCodeIdAndDataDate(langCodeId, targetDate);
    }

    @Override
    public List<TouristRestaurant> searchRestaurantsByName(String restaurantName, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("음식점명 검색 - 검색어: {}, 날짜: {}", restaurantName, targetDate);
        return repository.findByRestaurantNameContainingAndDataDate(restaurantName, targetDate);
    }

    @Override
    public List<TouristRestaurant> searchRestaurantsByAddress(String address, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("주소 검색 - 검색어: {}, 날짜: {}", address, targetDate);
        return repository.findByAddressContainingAndDataDate(address, targetDate);
    }

    @Override
    public List<TouristRestaurant> searchRestaurantsByMenu(String menu, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("대표메뉴 검색 - 검색어: {}, 날짜: {}", menu, targetDate);
        return repository.findByRepresentativeMenuContainingAndDataDate(menu, targetDate);
    }

    @Override
    public Optional<TouristRestaurant> findRestaurantById(Long id) {
        log.debug("음식점 ID로 조회 - ID: {}", id);
        return repository.findById(id);
    }

    @Override
    public List<TouristRestaurant> findRestaurantsWithWebsite(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("웹사이트가 있는 음식점 조회 - 날짜: {}", targetDate);
        return repository.findByWebsiteUrlIsNotNullAndDataDate(targetDate);
    }

    @Override
    public List<TouristRestaurant> findRestaurantsWithPhoneNumber(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("전화번호가 있는 음식점 조회 - 날짜: {}", targetDate);
        return repository.findByPhoneNumberIsNotNullAndDataDate(targetDate);
    }

    @Override
    public List<TouristRestaurant> findKoreanRestaurants(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("한국어 음식점 정보 조회 - 날짜: {}", targetDate);
        List<TouristRestaurant> koRestaurants = repository.findByLangCodeIdAndDataDate("ko", targetDate);
        List<TouristRestaurant> KORestaurants = repository.findByLangCodeIdAndDataDate("KO", targetDate);
        
        koRestaurants.addAll(KORestaurants);
        return koRestaurants;
    }

    @Override
    public List<TouristRestaurant> findEnglishRestaurants(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("영어 음식점 정보 조회 - 날짜: {}", targetDate);
        List<TouristRestaurant> enRestaurants = repository.findByLangCodeIdAndDataDate("en", targetDate);
        List<TouristRestaurant> ENRestaurants = repository.findByLangCodeIdAndDataDate("EN", targetDate);
        
        enRestaurants.addAll(ENRestaurants);
        return enRestaurants;
    }

    @Override
    public List<String> getAvailableDataDates() {
        log.debug("사용 가능한 데이터 날짜 목록 조회");
        return repository.findAllDataDatesOrderByDesc();
    }

    @Override
    public RestaurantDataStatistics getRestaurantStatistics(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 음식점 데이터가 없습니다");
            return new RestaurantDataStatistics(targetDate, 0, 0, 0, 0, 0, List.of(), 0);
        }
        
        log.debug("음식점 데이터 통계 조회 - 날짜: {}", targetDate);
        
        List<TouristRestaurant> allRestaurants = repository.findByDataDate(targetDate);
        long totalRestaurants = allRestaurants.size();
        
        long restaurantsWithWebsite = allRestaurants.stream()
            .filter(TouristRestaurant::hasWebsite)
            .count();
        
        long restaurantsWithPhoneNumber = allRestaurants.stream()
            .filter(TouristRestaurant::hasPhoneNumber)
            .count();
        
        long koreanRestaurants = allRestaurants.stream()
            .filter(TouristRestaurant::isKorean)
            .count();
        
        long englishRestaurants = allRestaurants.stream()
            .filter(TouristRestaurant::isEnglish)
            .count();
        
        List<String> languages = allRestaurants.stream()
            .map(TouristRestaurant::getLangCodeId)
            .filter(lang -> lang != null && !lang.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        long restaurantsPerLanguage = languages.isEmpty() ? 0 : totalRestaurants / languages.size();
        
        return new RestaurantDataStatistics(targetDate, totalRestaurants, restaurantsWithWebsite, 
            restaurantsWithPhoneNumber, koreanRestaurants, englishRestaurants, languages, restaurantsPerLanguage);
    }

    /**
     * 최신 데이터 날짜 조회
     */
    private String getLatestDataDate() {
        List<String> availableDates = repository.findAllDataDatesOrderByDesc();
        return availableDates.isEmpty() ? null : availableDates.get(0);
    }
}
