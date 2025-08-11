package com.seoulfit.backend.env.application.port.out;

import com.seoulfit.backend.env.application.port.out.dto.AirQualityApiResponse;

/**
 * 대기질 정보 외부 API 클라이언트 포트
 */
public interface AirQualityApiClient {

    /**
     * 실시간 대기질 정보 조회
     */
    AirQualityApiResponse fetchRealTimeAirQuality();

    /**
     * 특정 페이지의 대기질 정보 조회
     */
    AirQualityApiResponse fetchAirQuality(int startIndex, int endIndex);

    /**
     * 모든 대기질 정보 조회
     */
    AirQualityApiResponse fetchAllAirQuality();

    /**
     * 특정 측정소의 대기질 정보 조회
     */
    AirQualityApiResponse fetchAirQualityByStation(String stationName);
}
