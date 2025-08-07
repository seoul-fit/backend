package com.seoulfit.backend.culture.application.port.out;

import com.seoulfit.backend.culture.domain.CulturalEvent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 문화행사 데이터 로드 포트
 */
public interface LoadCulturalEventsPort {
    
    /**
     * 외부 API에서 문화행사 데이터 조회
     */
    List<CulturalEvent> loadCulturalEventsFromApi();
    
    /**
     * 데이터베이스에서 문화행사 조회
     */
    List<CulturalEvent> loadCulturalEvents(String district, String codeName, LocalDate startDate, LocalDate endDate, Boolean isFree, int page, int size);
    
    /**
     * 특정 위치 주변 문화행사 조회
     */
    List<CulturalEvent> loadCulturalEventsNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm);
    
    /**
     * 진행 중인 문화행사 조회
     */
    List<CulturalEvent> loadOngoingCulturalEvents();
    
    /**
     * 무료 문화행사 조회
     */
    List<CulturalEvent> loadFreeCulturalEvents();
    
    /**
     * API 상태 확인
     */
    boolean isApiHealthy();
}
