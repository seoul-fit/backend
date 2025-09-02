package com.seoulfit.backend.location.application.strategy;

import com.seoulfit.backend.user.domain.InterestCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 위치 기반 검색 조건을 담는 값 객체
 * 
 * Single Responsibility Principle에 따라 위치 검색에 필요한
 * 모든 조건을 하나의 객체로 캡슐화합니다.
 * 
 * **불변 객체 특성:**
 * - 생성 후 수정 불가능
 * - 스레드 안전성 보장
 * - 값 기반 비교 지원
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationCriteria {

    /**
     * 기준점 위도 (WGS84)
     */
    private Double latitude;

    /**
     * 기준점 경도 (WGS84)
     */
    private Double longitude;

    /**
     * 검색 반경 (킬로미터)
     */
    private Double radiusKm;

    /**
     * 필터링할 관심사 목록 (선택사항)
     */
    private List<InterestCategory> interests;

    /**
     * 최대 결과 개수 제한 (선택사항)
     */
    private Integer limit;

    /**
     * 정렬 기준 (선택사항)
     */
    private SortBy sortBy;

    /**
     * 정렬 방향 (선택사항)
     */
    private SortDirection sortDirection;

    /**
     * 정렬 기준 열거형
     */
    public enum SortBy {
        DISTANCE,    // 거리순
        NAME,        // 이름순
        RATING,      // 평점순
        CREATED_AT   // 생성일순
    }

    /**
     * 정렬 방향 열거형
     */
    public enum SortDirection {
        ASC,   // 오름차순
        DESC   // 내림차순
    }

    /**
     * 기본 위치 조건으로 LocationCriteria를 생성합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @return LocationCriteria 인스턴스
     */
    public static LocationCriteria of(Double latitude, Double longitude, Double radiusKm) {
        return LocationCriteria.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radiusKm(radiusKm)
                .sortBy(SortBy.DISTANCE)
                .sortDirection(SortDirection.ASC)
                .build();
    }

    /**
     * 관심사 필터를 포함한 LocationCriteria를 생성합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @param interests 관심사 목록
     * @return LocationCriteria 인스턴스
     */
    public static LocationCriteria withInterests(Double latitude, Double longitude, 
                                               Double radiusKm, List<InterestCategory> interests) {
        return LocationCriteria.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radiusKm(radiusKm)
                .interests(interests)
                .sortBy(SortBy.DISTANCE)
                .sortDirection(SortDirection.ASC)
                .build();
    }

    /**
     * 제한된 결과 개수를 포함한 LocationCriteria를 생성합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param radiusKm 반경 (km)
     * @param limit 최대 결과 개수
     * @return LocationCriteria 인스턴스
     */
    public static LocationCriteria withLimit(Double latitude, Double longitude, 
                                           Double radiusKm, Integer limit) {
        return LocationCriteria.builder()
                .latitude(latitude)
                .longitude(longitude)
                .radiusKm(radiusKm)
                .limit(limit)
                .sortBy(SortBy.DISTANCE)
                .sortDirection(SortDirection.ASC)
                .build();
    }

    /**
     * 관심사 필터가 설정되어 있는지 확인합니다.
     * 
     * @return 관심사 필터 존재 여부
     */
    public boolean hasInterestFilter() {
        return interests != null && !interests.isEmpty();
    }

    /**
     * 결과 개수 제한이 설정되어 있는지 확인합니다.
     * 
     * @return 결과 개수 제한 존재 여부
     */
    public boolean hasLimit() {
        return limit != null && limit > 0;
    }

    /**
     * 거리순 정렬인지 확인합니다.
     * 
     * @return 거리순 정렬 여부
     */
    public boolean isSortByDistance() {
        return sortBy == SortBy.DISTANCE;
    }

    /**
     * 지정된 관심사가 필터에 포함되어 있는지 확인합니다.
     * 
     * @param interest 확인할 관심사
     * @return 포함 여부
     */
    public boolean includesInterest(InterestCategory interest) {
        return hasInterestFilter() && interests.contains(interest);
    }

    /**
     * 유효한 위치 조건인지 검증합니다.
     * 
     * @throws IllegalArgumentException 유효하지 않은 조건인 경우
     */
    public void validate() {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("위도와 경도는 필수입니다.");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("위도는 -90도에서 90도 사이여야 합니다.");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("경도는 -180도에서 180도 사이여야 합니다.");
        }

        if (radiusKm == null || radiusKm <= 0) {
            throw new IllegalArgumentException("검색 반경은 0보다 큰 값이어야 합니다.");
        }

        if (radiusKm > 50) {
            throw new IllegalArgumentException("검색 반경은 50km를 초과할 수 없습니다.");
        }

        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("결과 개수 제한은 0보다 큰 값이어야 합니다.");
        }

        if (limit != null && limit > 1000) {
            throw new IllegalArgumentException("결과 개수 제한은 1000개를 초과할 수 없습니다.");
        }
    }

    /**
     * 검색 조건의 요약 정보를 문자열로 반환합니다.
     * 
     * @return 검색 조건 요약
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LocationCriteria{");
        sb.append("lat=").append(latitude);
        sb.append(", lng=").append(longitude);
        sb.append(", radius=").append(radiusKm).append("km");
        
        if (hasInterestFilter()) {
            sb.append(", interests=").append(interests.size()).append(" items");
        }
        
        if (hasLimit()) {
            sb.append(", limit=").append(limit);
        }
        
        sb.append(", sort=").append(sortBy).append(" ").append(sortDirection);
        sb.append("}");
        
        return sb.toString();
    }
}