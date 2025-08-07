package com.seoulfit.backend.culture.application.port.in;

/**
 * 문화행사 동기화 유스케이스
 */
public interface SyncCulturalEventsUseCase {
    
    /**
     * 외부 API에서 문화행사 데이터 동기화
     */
    int syncCulturalEvents();
    
    /**
     * API 상태 확인
     */
    boolean checkApiHealth();
}
