package com.seoulfit.backend.culture.application.port.in;

import com.seoulfit.backend.culture.domain.CulturalEvent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 문화행사 조회 유스케이스
 */
public interface GetCulturalEventsUseCase {
    
    /**
     * 문화행사 목록 조회
     */
    List<CulturalEvent> getCulturalEvents(GetCulturalEventsQuery query);
    
    /**
     * 특정 위치 주변 문화행사 조회
     */
    List<CulturalEvent> getCulturalEventsNearby(BigDecimal latitude, BigDecimal longitude, double radiusKm);
    
    /**
     * 진행 중인 문화행사 조회
     */
    List<CulturalEvent> getOngoingCulturalEvents();
    
    /**
     * 무료 문화행사 조회
     */
    List<CulturalEvent> getFreeCulturalEvents();
    
    /**
     * 문화행사 조회 쿼리
     */
    record GetCulturalEventsQuery(
        String district,
        String codeName,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isFree,
        int page,
        int size
    ) {}
}
