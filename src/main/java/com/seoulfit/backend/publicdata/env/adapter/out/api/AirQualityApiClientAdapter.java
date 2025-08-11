package com.seoulfit.backend.publicdata.env.adapter.out.api;

import com.seoulfit.backend.publicdata.env.adapter.out.api.dto.AirQualityApiResponse;
import com.seoulfit.backend.publicdata.env.application.port.out.AirQualityApiClient;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 대기질 정보 외부 API 클라이언트 어댑터
 * 헥사고날 아키텍처의 Adapter Out
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AirQualityApiClientAdapter implements AirQualityApiClient {

    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.environment.service-name[0]}")
    private String serviceName;

    @Value("${seoul-api.v1.environment.api-key}")
    private String apiKey;

    private final RestClientUtils<AirQualityApiResponse> restClientUtils;

    private static final int DEFAULT_START_INDEX = 1;
    private static final int DEFAULT_END_INDEX = 1000;

    @Override
    public AirQualityApiResponse fetchRealTimeAirQuality() {
        log.debug("실시간 대기질 정보 API 호출");
        return fetchAirQuality(DEFAULT_START_INDEX, DEFAULT_END_INDEX);
    }

    @Override
    public AirQualityApiResponse fetchAllAirQuality() {
        log.debug("전체 대기질 정보 API 호출");

        try {
            // 먼저 전체 데이터 수를 확인
            AirQualityApiResponse response = fetchAirQuality(DEFAULT_START_INDEX, DEFAULT_END_INDEX);

            int totalCount = response.getRealtimeCityAir().getListTotalCount();
            log.debug("전체 대기질 데이터 수: {}", totalCount);

            return response;

        } catch (Exception e) {
            String errorMessage = "전체 대기질 정보 API 호출 중 예외 발생: " + e.getMessage();
            throw new RuntimeException(errorMessage, e);
        }
    }

    private AirQualityApiResponse fetchAirQuality(int startIndex, int endIndex) {
        log.debug("대기질 정보 API 호출 - startIndex: {}, endIndex: {}", startIndex, endIndex);

        try {
            String url = buildApiUrl(startIndex, endIndex);
            log.debug("API URL: {}", url);

            AirQualityApiResponse response = restClientUtils.callGetApi(url, AirQualityApiResponse.class);
            AirQualityApiResponse.RealtimeCityAir realtimeCityAir = response.getRealtimeCityAir();
            
            if (realtimeCityAir.getResult() == null ||
                !"INFO-000".equals(realtimeCityAir.getResult().getCode())) {
                String errorMessage = realtimeCityAir.getResult() != null ?
                    realtimeCityAir.getResult().getMESSAGE() : "알 수 없는 오류";
                log.error("API 호출 실패: {}", errorMessage);
            }

            log.info("대기질 정보 API 호출 성공 - 조회된 데이터 수: {}, 전체 데이터 수: {}",
                    realtimeCityAir.getRow().size(), realtimeCityAir.getListTotalCount());

            return response;
        } catch (Exception e) {
            String errorMessage = "대기질 정보 API 호출 중 예외 발생: " + e.getMessage();
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * API URL 생성
     */
    private String buildApiUrl(int startIndex, int endIndex) {
        return String.format("%s/%s/%s/%d/%d/",
            baseUrl, apiKey, serviceName, startIndex, endIndex);
    }

}
