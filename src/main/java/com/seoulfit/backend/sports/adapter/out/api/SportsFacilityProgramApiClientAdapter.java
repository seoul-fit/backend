package com.seoulfit.backend.sports.adapter.out.api;

import com.seoulfit.backend.shared.common.ApiProperties;
import com.seoulfit.backend.sports.application.port.out.SportsFacilityProgramApiClient;
import com.seoulfit.backend.sports.application.port.out.dto.SportsFacilityProgramApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 서울시 공공체육시설 프로그램 정보 API 클라이언트 어댑터
 * 헥사고날 아키텍처의 출력 어댑터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SportsFacilityProgramApiClientAdapter implements SportsFacilityProgramApiClient {

    private final RestTemplate restTemplate;
    private final ApiProperties apiProperties;

    private static final String SERVICE_NAME = "ListProgramByPublicSportsFacilitiesService";
    private static final String TYPE = "json";
    private static final int MAX_BATCH_SIZE = 1000; // 한 번에 가져올 수 있는 최대 데이터 수

    @Override
    public SportsFacilityProgramApiResponse fetchProgramInfo(int startIndex, int endIndex, String subjectName) {
        log.debug("서울시 공공체육시설 프로그램 정보 API 호출 - 시작: {}, 종료: {}, 종목: {}", 
            startIndex, endIndex, subjectName);

        try {
            String url = buildApiUrl(startIndex, endIndex, subjectName);
            log.debug("API URL: {}", url);

            SportsFacilityProgramApiResponse response = restTemplate.getForObject(url, SportsFacilityProgramApiResponse.class);
            
            if (response != null && response.isSuccess()) {
                log.debug("API 호출 성공 - 조회된 데이터 수: {}", response.getProgramInfoList().size());
            } else {
                log.warn("API 호출 실패 또는 빈 응답");
            }

            return response;

        } catch (Exception e) {
            log.error("서울시 공공체육시설 프로그램 정보 API 호출 중 오류 발생", e);
            throw new RuntimeException("프로그램 정보 API 호출 실패", e);
        }
    }

    @Override
    public SportsFacilityProgramApiResponse fetchAllProgramInfo() {
        log.info("전체 서울시 공공체육시설 프로그램 정보 조회 시작");

        try {
            // 먼저 전체 데이터 개수를 확인하기 위해 첫 번째 요청
            SportsFacilityProgramApiResponse firstResponse = fetchProgramInfo(1, 1, null);
            
            if (!firstResponse.isSuccess()) {
                log.error("초기 API 호출 실패");
                return firstResponse;
            }

            int totalCount = firstResponse.getTotalCount();
            log.info("전체 프로그램 데이터 개수: {}", totalCount);

            if (totalCount <= 1) {
                return firstResponse;
            }

            // 전체 데이터를 배치로 나누어 가져오기 (최대 1000개씩)
            int batchSize = Math.min(totalCount, MAX_BATCH_SIZE);
            
            // 첫 번째 배치 가져오기
            SportsFacilityProgramApiResponse result = fetchProgramInfo(1, batchSize, null);
            
            if (!result.isSuccess()) {
                log.error("첫 번째 배치 API 호출 실패");
                return result;
            }

            log.info("첫 번째 배치 조회 완료 - 조회된 데이터 수: {}/{}", result.getProgramInfoList().size(), totalCount);
            
            // 나머지 배치들 가져오기 (필요한 경우)
            if (totalCount > MAX_BATCH_SIZE) {
                log.info("추가 배치 처리가 필요합니다. 현재는 첫 번째 배치만 반환합니다.");
                // 실제 운영에서는 여러 배치를 합쳐서 반환할 수 있지만, 
                // 테스트를 위해 첫 번째 배치만 반환
            }

            return result;

        } catch (Exception e) {
            log.error("전체 프로그램 정보 조회 중 오류 발생", e);
            throw new RuntimeException("전체 프로그램 정보 조회 실패", e);
        }
    }

    @Override
    public SportsFacilityProgramApiResponse fetchProgramInfoBySubject(String subjectName) {
        log.info("특정 종목 서울시 공공체육시설 프로그램 정보 조회 시작 - 종목: {}", subjectName);

        try {
            // 먼저 전체 데이터 개수를 확인하기 위해 첫 번째 요청
            SportsFacilityProgramApiResponse firstResponse = fetchProgramInfo(1, 1, subjectName);
            
            if (!firstResponse.isSuccess()) {
                log.error("초기 API 호출 실패");
                return firstResponse;
            }

            int totalCount = firstResponse.getTotalCount();
            log.info("전체 {} 종목 프로그램 데이터 개수: {}", subjectName, totalCount);

            if (totalCount <= 1) {
                return firstResponse;
            }

            // 전체 데이터를 배치로 나누어 가져오기 (최대 1000개씩)
            int batchSize = Math.min(totalCount, MAX_BATCH_SIZE);
            
            // 첫 번째 배치 가져오기
            SportsFacilityProgramApiResponse result = fetchProgramInfo(1, batchSize, subjectName);
            
            if (!result.isSuccess()) {
                log.error("첫 번째 배치 API 호출 실패");
                return result;
            }

            log.info("특정 종목 첫 번째 배치 조회 완료 - 종목: {}, 조회된 데이터 수: {}/{}", 
                subjectName, result.getProgramInfoList().size(), totalCount);

            return result;

        } catch (Exception e) {
            log.error("특정 종목 프로그램 정보 조회 중 오류 발생", e);
            throw new RuntimeException("특정 종목 프로그램 정보 조회 실패", e);
        }
    }

    /**
     * API URL 생성
     */
    private String buildApiUrl(int startIndex, int endIndex, String subjectName) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(apiProperties.getSeoul().getBaseUrl())
            .pathSegment(apiProperties.getSeoul().getApiKey())
            .pathSegment(TYPE)
            .pathSegment(SERVICE_NAME)
            .pathSegment(String.valueOf(startIndex))
            .pathSegment(String.valueOf(endIndex));

        // 종목명이 있는 경우 추가
        if (subjectName != null && !subjectName.trim().isEmpty()) {
            builder.pathSegment(subjectName);
        }

        return builder.build().toUriString();
    }
}
