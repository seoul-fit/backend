package com.seoulfit.backend.tmp.application.service.impl;

import com.seoulfit.backend.tmp.presentation.culture.dtos.response.SeoulReservationApiResponse;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CulturalReservationServiceImpl {

    @Value("${seoul-fit.culture-api-key:666e634468776c7339314668766844}")
    private String apiKey;

    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088";
    private static final String SERVICE_NAME = "ListPublicReservationSport"; // 체육시설 예약 정보

    private final RestClientUtils<SeoulReservationApiResponse> restClientUtils;

    public SeoulReservationApiResponse fetchCulturalReservations(int startIndex, int endIndex, String minClassName) {
        String url = buildApiUrl(startIndex, endIndex, minClassName);
        
        log.info("Calling Seoul Cultural Reservation API: {}", url);

        SeoulReservationApiResponse response = restClientUtils.callGetApi(url, SeoulReservationApiResponse.class);

        if (response != null && response.getRow() != null) {
            int totalCount = response.getListTotalCount();
            int rowCount = response.getRow().size();

            log.info("Successfully fetched {} cultural reservations (total: {})", rowCount, totalCount);

            // API 응답 상태 확인
            if (response.getResult() != null) {
                String resultCode = response.getResult().getCode();
                String resultMessage = response.getResult().getMessage();

                if (!"INFO-000".equals(resultCode)) {
                    log.warn("Seoul API returned non-success code: {} - {}", resultCode, resultMessage);
                }
            }
        } else {
            log.warn("Received null or empty response from Seoul Cultural Reservation API");
        }

        return response;
    }

    private String buildApiUrl(int startIndex, int endIndex, String minClassName) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL)
                .append("/").append(apiKey)
                .append("/json")
                .append("/").append(SERVICE_NAME)
                .append("/").append(startIndex)
                .append("/").append(endIndex);

        // 쿼리 파라미터 추가
        if (minClassName != null && !minClassName.trim().isEmpty()) {
            urlBuilder.append("?MINCLASSNM=").append(minClassName.trim());
        }

        return urlBuilder.toString();
    }

    /**
     * 소분류명별 예약 정보 조회
     */
    public SeoulReservationApiResponse fetchReservationsByCategory(String minClassName) {
        log.info("Fetching reservations by category: {}", minClassName);
        return fetchCulturalReservations(1, 1000, minClassName);
    }

    /**
     * 지역별 예약 정보 조회
     */
    public SeoulReservationApiResponse fetchReservationsByArea(String areaName) {
        String url = buildApiUrlWithArea(1, 1000, areaName);
        log.info("Calling Seoul API with area filter: {}", url);
        return restClientUtils.callGetApi(url, SeoulReservationApiResponse.class);
    }

    private String buildApiUrlWithArea(int startIndex, int endIndex, String areaName) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(BASE_URL)
                .append("/").append(apiKey)
                .append("/json")
                .append("/").append(SERVICE_NAME)
                .append("/").append(startIndex)
                .append("/").append(endIndex);

        if (areaName != null && !areaName.trim().isEmpty()) {
            urlBuilder.append("?AREANM=").append(areaName.trim());
        }

        return urlBuilder.toString();
    }

    /**
     * API 상태 확인
     */
    public boolean isApiHealthy() {
        try {
            log.debug("Checking Seoul Cultural Reservation API health");
            SeoulReservationApiResponse response = fetchCulturalReservations(1, 1, "");
            
            boolean isHealthy = response != null && 
                               response.getResult() != null &&
                               "INFO-000".equals(response.getResult().getCode());
            
            log.info("Seoul Cultural Reservation API health check result: {}", isHealthy ? "HEALTHY" : "UNHEALTHY");
            return isHealthy;
            
        } catch (Exception e) {
            log.warn("Seoul Cultural Reservation API health check failed", e);
            return false;
        }
    }

    /**
     * API 키 유효성 검증
     */
    public boolean validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("Seoul Cultural Reservation API key is not configured");
            return false;
        }
        
        if (apiKey.length() != 32) { // 서울시 API 키는 32자리
            log.error("Seoul Cultural Reservation API key format is invalid. Expected 32 characters, got {}", apiKey.length());
            return false;
        }
        
        return true;
    }
}
