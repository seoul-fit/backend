package com.seoulfit.backend.geo.application.port.out;

import com.seoulfit.backend.geo.domain.AdministrativeDistrict;
import com.seoulfit.backend.geo.domain.Coordinate;

import java.util.Optional;

/**
 * Shapefile 데이터 접근을 위한 포트 인터페이스
 */
public interface ShapefileRepository {
    
    /**
     * 좌표에 해당하는 행정구역 조회
     * 
     * @param coordinate 위경도 좌표
     * @return 행정구역 정보
     */
    Optional<AdministrativeDistrict> findByCoordinate(Coordinate coordinate);
    
    /**
     * Shapefile 데이터 초기화
     */
    void initialize();
    
    /**
     * 리소스 정리
     */
    void cleanup();
}
