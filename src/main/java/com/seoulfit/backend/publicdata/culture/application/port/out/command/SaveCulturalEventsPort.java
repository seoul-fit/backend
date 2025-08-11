package com.seoulfit.backend.publicdata.culture.application.port.out.command;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;

import java.util.List;

/**
 * 문화행사 데이터 저장 포트
 */
public interface SaveCulturalEventsPort {

    /**
     * 문화행사 데이터 일괄 저장
     */
    int saveCulturalEvents(List<CulturalEvent> events);

    /**
     * 기존 문화행사 데이터 삭제
     */
    void clearCulturalEvents();
}

