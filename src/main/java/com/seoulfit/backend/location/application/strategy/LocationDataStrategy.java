package com.seoulfit.backend.location.application.strategy;

import com.seoulfit.backend.user.domain.InterestCategory;

import java.util.List;

/**
 * 위치 기반 데이터 조회 전략 인터페이스
 * 
 * Strategy Pattern을 사용하여 다양한 데이터 소스별로 
 * 위치 기반 검색 로직을 캡슐화합니다.
 * 
 * **SRP 적용 효과:**
 * - 각 데이터 소스별로 별도의 전략 클래스 구현
 * - 새로운 데이터 소스 추가 시 기존 코드 수정 불필요
 * - 각 전략은 하나의 데이터 타입에 대한 책임만 가짐
 * 
 * **구현 클래스들:**
 * - RestaurantLocationStrategy: 음식점 데이터 전략
 * - LibraryLocationStrategy: 도서관 데이터 전략  
 * - ParkLocationStrategy: 공원 데이터 전략
 * - SportsFacilityLocationStrategy: 스포츠시설 데이터 전략
 * - CoolingCenterLocationStrategy: 무더위쉼터 데이터 전략
 * 
 * @param <T> 조회할 데이터의 엔티티 타입
 * @author Seoul Fit
 * @since 1.0.0
 */
public interface LocationDataStrategy<T> {

    /**
     * 지정된 위치 기준으로 근처 데이터를 조회합니다.
     * 
     * 각 전략 구현체는 해당 데이터 소스에 최적화된
     * 검색 로직을 구현해야 합니다.
     * 
     * @param criteria 위치 검색 조건
     * @return 검색된 데이터 목록 (거리순 정렬)
     */
    List<T> findNearby(LocationCriteria criteria);

    /**
     * 이 전략이 처리하는 데이터 타입을 반환합니다.
     * 
     * @return 데이터 타입 식별자 (예: "RESTAURANTS", "LIBRARIES")
     */
    String getDataType();

    /**
     * 지정된 관심사를 지원하는지 확인합니다.
     * 
     * @param interest 확인할 관심사 카테고리
     * @return 지원 여부
     */
    boolean supportsInterest(InterestCategory interest);

    /**
     * 전략의 실행 우선순위를 반환합니다.
     * 
     * 낮은 숫자일수록 높은 우선순위를 가집니다.
     * 성능이 좋은 데이터 소스부터 조회하도록 순서를 조정할 수 있습니다.
     * 
     * @return 우선순위 (1이 가장 높음)
     */
    int getPriority();

    /**
     * 이 전략이 활성화되어 있는지 확인합니다.
     * 
     * 설정이나 외부 요인으로 인해 비활성화될 수 있습니다.
     * 
     * @return 활성화 여부
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * 전략별 최대 검색 반경을 반환합니다.
     * 
     * 데이터 특성에 따라 검색 가능한 최대 반경이 다를 수 있습니다.
     * 
     * @return 최대 검색 반경 (km)
     */
    default double getMaxSearchRadius() {
        return 10.0; // 기본값: 10km
    }

    /**
     * 전략별 기본 검색 반경을 반환합니다.
     * 
     * 반경이 지정되지 않았을 때 사용할 기본값입니다.
     * 
     * @return 기본 검색 반경 (km)
     */
    default double getDefaultSearchRadius() {
        return 2.0; // 기본값: 2km
    }

    /**
     * 이 전략에 대한 설명을 반환합니다.
     * 
     * API 문서나 로그에서 사용됩니다.
     * 
     * @return 전략 설명
     */
    default String getDescription() {
        return String.format("%s 데이터 조회 전략", getDataType());
    }
}