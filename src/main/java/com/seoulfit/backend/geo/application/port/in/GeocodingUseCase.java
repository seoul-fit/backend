package com.seoulfit.backend.geo.application.port.in;

import com.seoulfit.backend.geo.domain.AdministrativeDistrict;
import com.seoulfit.backend.geo.domain.Coordinate;

import java.util.Optional;

/**
 * 지오코딩 관련 유스케이스 인터페이스
 */
public interface GeocodingUseCase {
    
    /**
     * 위경도 좌표로부터 행정구역 정보를 조회
     * 
     * @param coordinate 위경도 좌표
     * @return 행정구역 정보 (없으면 Optional.empty())
     */
    Optional<AdministrativeDistrict> getAdministrativeDistrict(Coordinate coordinate);
    
    /**
     * 위경도 좌표로부터 행정구역 정보를 조회 (편의 메서드)
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @return 행정구역 정보 (없으면 Optional.empty())
     */
    default Optional<AdministrativeDistrict> getAdministrativeDistrict(double latitude, double longitude) {
        return getAdministrativeDistrict(Coordinate.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build());
    }
}
