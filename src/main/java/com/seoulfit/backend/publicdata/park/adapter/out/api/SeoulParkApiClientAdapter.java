package com.seoulfit.backend.publicdata.park.adapter.out.api;

import com.seoulfit.backend.publicdata.park.application.port.out.SeoulParkApiClient;
import com.seoulfit.backend.publicdata.park.adapter.out.api.dto.SeoulParkApiResponse;
import com.seoulfit.backend.shared.common.ApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 서울시 공원 정보 API 클라이언트 어댑터
 * 헥사고날 아키텍처의 출력 어댑터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeoulParkApiClientAdapter implements SeoulParkApiClient {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    private static final String SERVICE_NAME = "SearchParkInfoService";
    private static final String TYPE = "json";
    private static final int DEFAULT_BATCH_SIZE = 1000;

    @Override
    public SeoulParkApiResponse fetchParkInfo(int startIndex, int endIndex) {
        log.debug("서울시 공원 정보 API 호출 - 시작: {}, 종료: {}", startIndex, endIndex);

        try {
            String url = buildApiUrl(startIndex, endIndex);
            log.debug("API URL: {}", url);

            SeoulParkApiResponse response = restTemplate.getForObject(url, SeoulParkApiResponse.class);
            
            if (response != null && response.isSuccess()) {
                log.debug("API 호출 성공 - 조회된 데이터 수: {}", response.getParkInfoList().size());
            } else {
                log.warn("API 호출 실패 또는 빈 응답");
            }

            return response;

        } catch (Exception e) {
            log.error("서울시 공원 정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("공원 정보 API 호출 실패", e);
        }
    }

    @Override
    public SeoulParkApiResponse fetchAllParkInfo() {
        log.info("전체 서울시 공원 정보 조회 시작");

        try {
            // 먼저 전체 데이터 개수를 확인하기 위해 첫 번째 요청
            SeoulParkApiResponse firstResponse = fetchParkInfo(1, 1);
            
            if (!firstResponse.isSuccess()) {
                log.error("초기 API 호출 실패");
                return firstResponse;
            }

            int totalCount = firstResponse.getTotalCount();
            log.info("전체 공원 데이터 개수: {}", totalCount);

            if (totalCount <= 1) {
                return firstResponse;
            }

            // 전체 데이터 조회
            return fetchParkInfo(1, totalCount);

        } catch (Exception e) {
            log.error("전체 공원 정보 조회 중 오류 발생", e);
            throw new RuntimeException("전체 공원 정보 조회 실패", e);
        }
    }

    /**
     * API URL 생성
     */
    private String buildApiUrl(int startIndex, int endIndex) {
        return UriComponentsBuilder
            .fromHttpUrl(apiProperties.getSeoul().getBaseUrl())
            .pathSegment(apiProperties.getSeoul().getApiKey())
            .pathSegment(TYPE)
            .pathSegment(SERVICE_NAME)
            .pathSegment(String.valueOf(startIndex))
            .pathSegment(String.valueOf(endIndex))
            .build()
            .toUriString();
    }
}
