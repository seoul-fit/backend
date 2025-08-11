package com.seoulfit.backend.publicdata.env.application.port.out;

import com.seoulfit.backend.publicdata.env.adapter.out.api.dto.AirQualityApiResponse;

/**
 * 대기질 정보 외부 API 클라이언트 포트
 */
public interface AirQualityApiClient {

    /**
     * 실시간 대기질 정보 조회
     */
    AirQualityApiResponse fetchRealTimeAirQuality();

    /**
     * 모든 대기질 정보 조회
     */
    AirQualityApiResponse fetchAllAirQuality();

}
