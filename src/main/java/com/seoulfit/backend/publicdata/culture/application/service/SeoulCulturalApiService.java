package com.seoulfit.backend.publicdata.culture.application.service;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.res.SeoulApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class SeoulCulturalApiService {

    private final RestClient seoulApiRestClient;

    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.culture.service-name[0]}")
    private String serviceName;

    public SeoulCulturalApiService(@Qualifier("seoulApiRestClient") RestClient seoulApiRestClient) {
        this.seoulApiRestClient = seoulApiRestClient;
    }

    /**
     * 기본 문화행사 조회
     */
    public SeoulApiResponse fetchCulturalEvents(int startIndex, int endIndex) {
        return fetchCulturalEvents(startIndex, endIndex, null, null, null);
    }

    /**
     * 조건부 문화행사 관련 API 호출
     *
     * @param startIndex 시작 인덱스
     * @param endIndex   종료 인덱스
     * @param codeName   분류 (선택)
     * @param title      공연/행사명 (선택)
     * @param date       날짜 (선택)
     */
    private SeoulApiResponse fetchCulturalEvents(int startIndex, int endIndex, String codeName, String title, LocalDate date) {
        try {
            String url = buildApiUrl(startIndex, endIndex, codeName, title, date);

            SeoulApiResponse response = seoulApiRestClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, clientResponse) -> {
                        log.error("Client error calling Seoul API: {} - {}",
                                clientResponse.getStatusCode(), clientResponse.getStatusText());
                        throw new RestClientException("Seoul API client error: " + clientResponse.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, serverResponse) -> {
                        log.error("Server error calling Seoul API: {} - {}",
                                serverResponse.getStatusCode(), serverResponse.getStatusText());
                        throw new RestClientException("Seoul API server error: " + serverResponse.getStatusCode());
                    })
                    .body(SeoulApiResponse.class);

            if (response != null && response.getCulturalEventInfo() != null) {
                int totalCount = response.getCulturalEventInfo().getListTotalCount();
                int rowCount = response.getCulturalEventInfo().getRow() != null ?
                        response.getCulturalEventInfo().getRow().size() : 0;

                log.info("Successfully fetched {} cultural events (total: {})", rowCount, totalCount);

                // API 응답 상태 확인
                if (response.getCulturalEventInfo().getResult() != null) {
                    String resultCode = response.getCulturalEventInfo().getResult().getCode();
                    String resultMessage = response.getCulturalEventInfo().getResult().getMessage();

                    if (!"INFO-000".equals(resultCode)) {
                        log.warn("Seoul API returned non-success code: {} - {}", resultCode, resultMessage);
                    }
                }
            } else {
                log.warn("Received null or empty response from Seoul API");
            }

            return response;

        } catch (RestClientException e) {
            log.error("RestClient error fetching cultural events from Seoul API", e);
            throw new RuntimeException("Failed to fetch cultural events: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching cultural events from Seoul API", e);
            throw new RuntimeException("Failed to fetch cultural events", e);
        }
    }

    /**
     * 전체 문화행사 조회
     */
    public SeoulApiResponse fetchAllCulturalEvents(int startIndex, int endIndex) {
        try {
            // 먼저 총 개수를 확인
            SeoulApiResponse firstResponse = fetchCulturalEvents(startIndex, endIndex);
            if (firstResponse == null || firstResponse.getCulturalEventInfo() == null)
                throw new RuntimeException("Failed to get total count from Seoul API");

            int totalCount = firstResponse.getCulturalEventInfo().getListTotalCount();
            log.info("Total cultural events available: {}", totalCount);

            if (totalCount == 0) {
                log.info("No cultural events found");
                return firstResponse;
            }

            return firstResponse;

        } catch (Exception e) {
            log.error("Error fetching all cultural events", e);
            throw new RuntimeException("Failed to fetch all cultural events", e);
        }
    }

    /**
     * API URL 생성
     */
    private String buildApiUrl(int startIndex, int endIndex, String codeName, String title, LocalDate date) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl)
                .append("/").append(serviceName)
                .append("/").append(startIndex)
                .append("/").append(endIndex);

        // 쿼리 파라미터 추가
        boolean hasParams = false;

        if (codeName != null && !codeName.trim().isEmpty()) {
            urlBuilder.append(hasParams ? "&" : "?").append("CODENAME=").append(codeName.trim());
            hasParams = true;
        }

        if (title != null && !title.trim().isEmpty()) {
            urlBuilder.append(hasParams ? "&" : "?").append("TITLE=").append(title.trim());
            hasParams = true;
        }

        if (date != null) {
            urlBuilder.append(hasParams ? "&" : "?")
                    .append("DATE=")
                    .append(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        return urlBuilder.toString();
    }

    /**
     * API 상태 확인
     */
    public boolean isApiHealthy() {
        try {
            SeoulApiResponse response = fetchCulturalEvents(1, 1);

            boolean isHealthy = response != null &&
                    response.getCulturalEventInfo() != null &&
                    response.getCulturalEventInfo().getResult() != null &&
                    "INFO-000".equals(response.getCulturalEventInfo().getResult().getCode());

            log.info("Seoul Cultural API health check result: {}", isHealthy ? "HEALTHY" : "UNHEALTHY");
            return isHealthy;

        } catch (Exception e) {
            log.warn("Seoul Cultural API health check failed", e);
            return false;
        }
    }
}
