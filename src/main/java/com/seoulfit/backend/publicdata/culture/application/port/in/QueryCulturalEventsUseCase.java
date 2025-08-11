package com.seoulfit.backend.publicdata.culture.application.port.in;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;

import java.util.List;

/**
 * 문화행사 조회 유스케이스
 */
public interface QueryCulturalEventsUseCase {

    List<CulturalEvent> getAllCulturalEvents();

}
