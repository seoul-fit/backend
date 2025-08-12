package com.seoulfit.backend.publicdata.park.application.port.in;

/**
 * 서울시 공원 정보 배치 처리 유스케이스
 * 헥사고날 아키텍처의 입력 포트
 */
public interface ParkBatchUseCase {
    void processDailyBatch();
}
