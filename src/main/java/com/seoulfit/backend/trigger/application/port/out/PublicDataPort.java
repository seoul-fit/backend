package com.seoulfit.backend.trigger.application.port.out;

import java.util.Map;

/**
 * 공공 데이터 출력 포트
 * <p>
 * 헥사고날 아키텍처의 출력 포트
 * 외부 공공 데이터 API 접근을 위한 인터페이스
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
public interface PublicDataPort {

    /**
     * 서울시 실시간 도시 데이터 조회
     *
     * @param location 위치명
     * @return 도시 데이터
     */
    Map<String, Object> getCityData(String location);

    /**
     * 따릉이 현황 데이터 조회
     *
     * @return 따릉이 현황 데이터
     */
    Map<String, Object> getBikeShareData();

    /**
     * 실시간 대기환경 데이터 조회
     *
     * @return 대기환경 데이터
     */
    Map<String, Object> getAirQualityData();

    /**
     * 강수량 데이터 조회
     *
     * @return 강수량 데이터
     */
    Map<String, Object> getRainfallData();

    /**
     * 문화행사 데이터 조회
     *
     * @return 문화행사 데이터
     */
    Map<String, Object> getCulturalEventData();
}
