package com.seoulfit.backend.geo.application.service;

import com.seoulfit.backend.geo.application.port.in.GeocodingUseCase;
import com.seoulfit.backend.geo.application.port.out.ShapefileRepository;
import com.seoulfit.backend.geo.domain.AdministrativeDistrict;
import com.seoulfit.backend.geo.domain.Coordinate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 지오코딩 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService implements GeocodingUseCase {
    
    private final ShapefileRepository shapefileRepository;
    
    @Override
    public Optional<AdministrativeDistrict> getAdministrativeDistrict(Coordinate coordinate) {
        log.debug("행정구역 조회 요청: {}", coordinate);
        
        // 좌표 유효성 검증
        if (!coordinate.isValid()) {
            log.warn("유효하지 않은 좌표: {}", coordinate);
            return Optional.empty();
        }
        
        // 한국 영역 내 좌표인지 확인
        if (!coordinate.isInKorea()) {
            log.warn("한국 영역 밖의 좌표: {}", coordinate);
            return Optional.empty();
        }
        
        try {
            Optional<AdministrativeDistrict> result = shapefileRepository.findByCoordinate(coordinate);
            
            if (result.isPresent()) {
                log.debug("행정구역 조회 성공: {} -> {}", coordinate, result.get().getFullAddress());
            } else {
                log.debug("행정구역을 찾을 수 없음: {}", coordinate);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("행정구역 조회 중 오류 발생: {}", coordinate, e);
            return Optional.empty();
        }
    }
}
