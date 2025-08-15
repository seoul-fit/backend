package com.seoulfit.backend.publicdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.api.dto.SeoulLibraryInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * 서울시 공공 데이터 API 클라이언트입니다.
 * 
 * <p>서울시에서 제공하는 다양한 공공 데이터 API를 호출하고 관리하는 중앙 클라이언트입니다.
 * 오픈소스 확장성을 고려하여 설계되었으며, 새로운 API 엔드포인트를 쉽게 추가할 수 있습니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>서울시 실시간 도시 데이터 조회</li>
 *   <li>따릉이 현황 데이터 조회</li>
 *   <li>실시간 대기환경 현황 조회</li>
 *   <li>문화행사 정보 조회</li>
 *   <li>공공도서관 현황 정보 조회</li>
 *   <li>API 응답 캐싱 및 재시도 메커니즘</li>
 *   <li>위치 기반 실시간 데이터 통합 조회</li>
 * </ul>
 * 
 * <p>이 클래스는 WebClient를 사용하여 비동기적으로 API를 호출하며,
 * 메모리 기반 캐싱을 통해 성능을 최적화합니다. 또한 재시도 메커니즘을 통해
 * 일시적인 네트워크 오류에 대한 복원력을 제공합니다.</p>
 * 
 * @author Seoul Fit
 * @version 1.0.0
 * @since 2025-01-01
 * @see WebClient
 * @see SeoulLibraryInfoDto
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataApiClient {

    /**
     * 비동기 HTTP 클라이언트입니다.
     */
    private final WebClient webClient;
    
    /**
     * JSON 파싱을 위한 ObjectMapper입니다.
     */
    private final ObjectMapper objectMapper;

    /**
     * 서울시 공공 데이터 API의 기본 URL입니다.
     */
    @Value("${urbanping.api.seoul.base-url:http://openapi.seoul.go.kr:8088}")
    private String baseUrl;

    /**
     * 서울시 공공 데이터 API 인증 키입니다.
     */
    @Value("${urbanping.api.seoul.api-key:sample}")
    private String apiKey;

    /**
     * API 호출 타임아웃 시간(초)입니다.
     */
    @Value("${urbanping.api.seoul.timeout:10}")
    private int timeoutSeconds;

    /**
     * API 호출 실패 시 재시도 횟수입니다.
     */
    @Value("${urbanping.api.seoul.retry-attempts:3}")
    private int retryAttempts;

    /**
     * API 응답을 캐싱하는 메모리 기반 캐시입니다.
     * 동시성을 고려하여 ConcurrentHashMap을 사용합니다.
     */
    private final Map<String, CachedResponse> responseCache = new ConcurrentHashMap<>();

    /**
     * 서울시 실시간 도시 데이터를 조회합니다.
     * 
     * <p>지정된 장소의 실시간 도시 데이터를 조회하며, 5분간 캐싱됩니다.</p>
     *
     * @param locationName 조회할 장소명 (예: "광화문·덕수궁")
     * @return 실시간 도시 데이터를 포함한 Mono 객체
     */
    public Mono<Map<String, Object>> getCityData(String locationName) {
        String cacheKey = "citydata_" + locationName;

        // 캐시 확인 (5분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(5))) {
            log.debug("캐시된 도시 데이터 반환: location={}", locationName);
            return Mono.just((Map<String, Object>) cached.data);
        }

        String url = String.format("%s/%s/json/citydata/1/5/%s", baseUrl, apiKey, locationName);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofSeconds(1)))
                .map(this::parseJsonResponse)
                .doOnSuccess(data -> {
                    responseCache.put(cacheKey, new CachedResponse(data));
                    log.debug("도시 데이터 조회 성공: location={}", locationName);
                })
                .doOnError(error -> log.error("도시 데이터 조회 실패: location={}", locationName, error));
    }

    /**
     * 따릉이 현황 데이터를 조회합니다.
     * 
     * <p>서울시 공공자전거 따릉이의 대여소별 현황 정보를 조회하며, 3분간 캐싱됩니다.</p>
     *
     * @param startIndex 조회 시작 인덱스
     * @param endIndex 조회 종료 인덱스
     * @return 따릉이 현황 데이터를 포함한 Mono 객체
     */
    public Mono<Map<String, Object>> getBikeData(int startIndex, int endIndex) {
        String cacheKey = String.format("bikedata_%d_%d", startIndex, endIndex);

        // 캐시 확인 (3분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(3))) {
            log.debug("캐시된 따릉이 데이터 반환: start={}, end={}", startIndex, endIndex);
            return Mono.just((Map<String, Object>) cached.data);
        }

        String url = String.format("%s/%s/json/bikeList/%d/%d/", baseUrl, apiKey, startIndex, endIndex);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofSeconds(1)))
                .map(this::parseJsonResponse)
                .doOnSuccess(data -> {
                    responseCache.put(cacheKey, new CachedResponse(data));
                    log.debug("따릉이 데이터 조회 성공: start={}, end={}", startIndex, endIndex);
                })
                .doOnError(error -> log.error("따릉이 데이터 조회 실패: start={}, end={}", startIndex, endIndex, error));
    }

    /**
     * 모든 따릉이 대여소 정보를 조회합니다.
     * 
     * <p>총 3000건의 데이터를 1000건씩 3번에 나누어 조회하며, 5분간 캐싱됩니다.</p>
     * 
     * @return 모든 따릉이 대여소 정보 리스트
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllBikeStations() {
        String cacheKey = "all_bike_stations";
        
        // 캐시 확인 (5분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(5))) {
            log.debug("캐시된 모든 따릉이 데이터 반환: {}건", ((List<?>) cached.data).size());
            return (List<Map<String, Object>>) cached.data;
        }
        
        List<Map<String, Object>> allStations = new ArrayList<>();
        
        try {
            // 3번에 나누어 조회 (1-1000, 1001-2000, 2001-3000)
            for (int i = 0; i < 3; i++) {
                int startIndex = i * 1000 + 1;
                int endIndex = (i + 1) * 1000;
                
                log.debug("따릉이 데이터 조회 시작: {}-{}", startIndex, endIndex);
                
                Map<String, Object> bikeData = getBikeData(startIndex, endIndex).block();
                if (bikeData != null && bikeData.containsKey("rentBikeStatus")) {
                    Object rentBikeStatusData = bikeData.get("rentBikeStatus");
                    if (rentBikeStatusData instanceof Map) {
                        Map<String, Object> rentBikeStatusMap = (Map<String, Object>) rentBikeStatusData;
                        Object rowData = rentBikeStatusMap.get("row");
                        if (rowData instanceof List) {
                            List<Map<String, Object>> stations = (List<Map<String, Object>>) rowData;
                            allStations.addAll(stations);
                            log.debug("따릉이 데이터 추가: {}건, 총 {}건", stations.size(), allStations.size());
                        }
                    }
                }
                
                // API 호출 간격 조절 (0.5초 대기)
                if (i < 2) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("따릉이 데이터 조회 중 인터럽트 발생", e);
                        break;
                    }
                }
            }
            
            log.info("모든 따릉이 데이터 조회 완료: 총 {}건", allStations.size());
            
            // 캐시에 저장
            if (!allStations.isEmpty()) {
                responseCache.put(cacheKey, new CachedResponse(allStations));
                log.debug("모든 따릉이 데이터 캐시 저장 완료: {}건", allStations.size());
            }
            
            return allStations;
            
        } catch (Exception e) {
            log.error("따릉이 데이터 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    /**
     * 실시간 대기환경 현황을 조회합니다.
     * 
     * <p>서울시 각 구별 실시간 대기질 정보를 조회하며, 10분간 캐싱됩니다.</p>
     *
     * @param startIndex 조회 시작 인덱스
     * @param endIndex 조회 종료 인덱스
     * @return 대기환경 현황 데이터를 포함한 Mono 객체
     */
    public Mono<Map<String, Object>> getAirQualityData(int startIndex, int endIndex) {
        String cacheKey = String.format("airquality_%d_%d", startIndex, endIndex);

        // 캐시 확인 (10분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(10))) {
            log.debug("캐시된 대기질 데이터 반환: start={}, end={}", startIndex, endIndex);
            return Mono.just((Map<String, Object>) cached.data);
        }

        String url = String.format("%s/%s/json/RealtimeCityAir/%d/%d/", baseUrl, apiKey, startIndex, endIndex);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofSeconds(1)))
                .map(this::parseJsonResponse)
                .doOnSuccess(data -> {
                    responseCache.put(cacheKey, new CachedResponse(data));
                    log.debug("대기질 데이터 조회 성공: start={}, end={}", startIndex, endIndex);
                })
                .doOnError(error -> log.error("대기질 데이터 조회 실패: start={}, end={}", startIndex, endIndex, error));
    }

    /**
     * 문화행사 정보를 조회합니다.
     * 
     * <p>서울시에서 진행되는 다양한 문화행사 정보를 조회하며, 30분간 캐싱됩니다.</p>
     *
     * @param startIndex 조회 시작 인덱스
     * @param endIndex 조회 종료 인덱스
     * @return 문화행사 정보를 포함한 Mono 객체
     */
    public Mono<Map<String, Object>> getCulturalEventData(int startIndex, int endIndex) {
        String cacheKey = String.format("cultural_%d_%d", startIndex, endIndex);

        // 캐시 확인 (30분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(30))) {
            log.debug("캐시된 문화행사 데이터 반환: start={}, end={}", startIndex, endIndex);
            return Mono.just((Map<String, Object>) cached.data);
        }

        String url = String.format("%s/%s/json/culturalEventInfo/%d/%d/", baseUrl, apiKey, startIndex, endIndex);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofSeconds(1)))
                .map(this::parseJsonResponse)
                .doOnSuccess(data -> {
                    responseCache.put(cacheKey, new CachedResponse(data));
                    log.debug("문화행사 데이터 조회 성공: start={}, end={}", startIndex, endIndex);
                })
                .doOnError(error -> log.error("문화행사 데이터 조회 실패: start={}, end={}", startIndex, endIndex, error));
    }

    /**
     * 서울시 공공도서관 현황 정보를 조회합니다.
     * 
     * <p>서울시 공공도서관의 위치, 운영시간, 연락처 등의 정보를 조회하며, 30분간 캐싱됩니다.</p>
     *
     * @param startIndex 조회 시작 인덱스
     * @param endIndex 조회 종료 인덱스
     * @return 공공도서관 정보를 포함한 Mono 객체
     */
    public Mono<SeoulLibraryInfoDto> getSeoulLibraryData(int startIndex, int endIndex) {
        String cacheKey = String.format("SeoulPublicLibraryInfo_%d_%d", startIndex, endIndex);

        // 캐시 확인 (30분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(30))) {
            return Mono.just((SeoulLibraryInfoDto) cached.data);
        }

        String url = String.format("%s/%s/json/SeoulPublicLibraryInfo/%d/%d/", baseUrl, apiKey, startIndex, endIndex);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(SeoulLibraryInfoDto.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofSeconds(1)))
                .doOnSuccess(data -> {
                    log.info("도서관 데이터 조회 성공: start={} end={}", startIndex, endIndex);
                })
                .doOnError(error -> log.error("도서관 데이터 조회 실패: start={} end={} cause={}", startIndex, endIndex, error.getMessage()));
    }

    /**
     * 범용 API 호출 메서드입니다.
     * 
     * <p>새로운 API 엔드포인트를 쉽게 추가할 수 있도록 제공되는 확장성 메서드입니다.
     * 지정된 캐시 지속 시간에 따라 응답이 캐싱됩니다.</p>
     *
     * @param serviceName 서비스명 (API 엔드포인트 이름)
     * @param startIndex 조회 시작 인덱스
     * @param endIndex 조회 종료 인덱스
     * @param parameters 추가 파라미터 (선택사항)
     * @param cacheDuration 캐시 지속 시간
     * @return API 응답 데이터를 포함한 Mono 객체
     */
    public Mono<Map<String, Object>> callApi(String serviceName, int startIndex, int endIndex,
            String parameters, Duration cacheDuration) {
        String cacheKey = String.format("%s_%d_%d_%s", serviceName, startIndex, endIndex,
                parameters != null ? parameters.hashCode() : "");

        // 캐시 확인
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(cacheDuration)) {
            log.debug("캐시된 API 데이터 반환: service={}", serviceName);
            return Mono.just((Map<String, Object>) cached.data);
        }

        String url = String.format("%s/%s/json/%s/%d/%d/%s",
                baseUrl, apiKey, serviceName, startIndex, endIndex,
                parameters != null ? parameters : "");

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .retryWhen(Retry.backoff(retryAttempts, Duration.ofSeconds(1)))
                .map(this::parseJsonResponse)
                .doOnSuccess(data -> {
                    responseCache.put(cacheKey, new CachedResponse(data));
                    log.debug("API 데이터 조회 성공: service={}", serviceName);
                })
                .doOnError(error -> log.error("API 데이터 조회 실패: service={}", serviceName, error));
    }

    /**
     * 위치 기반 실시간 데이터를 통합 조회합니다.
     * 
     * <p>주어진 위치 좌표를 기반으로 도시 데이터, 따릉이, 대기질, 문화행사 정보를
     * 통합하여 조회합니다. 각 API는 개별적으로 호출되며, 실패한 API가 있어도
     * 다른 데이터는 정상적으로 반환됩니다.</p>
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @return 통합된 실시간 데이터 맵
     */
    public Map<String, Object> fetchRealtimeData(Double latitude, Double longitude) {
        log.debug("위치 기반 실시간 데이터 조회: lat={}, lng={}", latitude, longitude);
        
        Map<String, Object> realtimeData = new HashMap<>();
        
        try {
            // 도시 데이터 조회 (비동기를 동기로 변환)
            Map<String, Object> cityData = getCityData("광화문·덕수궁").block();
            if (cityData != null) {
                realtimeData.put("CITY_DATA", cityData);
            }
            
            // 따릉이 데이터 조회 (총 3000건을 3번에 나누어 조회)
            List<Map<String, Object>> allBikeStations = getAllBikeStations();
            if (!allBikeStations.isEmpty()) {
                realtimeData.put("BIKE_SHARE", allBikeStations);
            }
            
            // 대기질 데이터 조회
            Map<String, Object> airData = getAirQualityData(1, 25).block();
            if (airData != null) {
                realtimeData.put("AIR_QUALITY", airData);
            }
            
            // 문화행사 데이터 조회
            Map<String, Object> culturalData = getCulturalEventData(1, 50).block();
            if (culturalData != null) {
                realtimeData.put("CULTURAL_EVENTS", culturalData);
            }
            
        } catch (Exception e) {
            log.error("실시간 데이터 조회 중 오류 발생: lat={}, lng={}, error={}", 
                    latitude, longitude, e.getMessage(), e);
        }
        
        return realtimeData;
    }

    /**
     * JSON 문자열을 Map 객체로 파싱합니다.
     * 
     * <p>API 응답으로 받은 JSON 문자열을 파싱하여 Map 형태로 변환합니다.
     * 파싱에 실패한 경우 빈 Map을 반환합니다.</p>
     *
     * @param jsonString 파싱할 JSON 문자열
     * @return 파싱된 Map 객체 (실패 시 빈 Map)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonResponse(String jsonString) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            return objectMapper.convertValue(rootNode, Map.class);
        } catch (Exception e) {
            log.error("JSON 파싱 실패: {}", jsonString, e);
            return new HashMap<>();
        }
    }

    /**
     * 모든 캐시를 초기화합니다.
     * 
     * <p>메모리에 저장된 모든 API 응답 캐시를 제거합니다.
     * 주로 관리자 기능이나 테스트 목적으로 사용됩니다.</p>
     */
    public void clearCache() {
        responseCache.clear();
        log.info("API 응답 캐시 초기화 완료");
    }

    /**
     * 현재 캐시 상태 정보를 반환합니다.
     * 
     * <p>캐시된 항목의 개수와 키 목록을 포함한 상태 정보를 제공합니다.
     * 모니터링이나 디버깅 목적으로 사용됩니다.</p>
     *
     * @return 캐시 상태 정보를 포함한 Map
     */
    public Map<String, Object> getCacheStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("cacheSize", responseCache.size());
        status.put("cacheKeys", responseCache.keySet());
        return status;
    }

    /**
     * 캐시된 API 응답을 저장하는 내부 클래스입니다.
     * 
     * <p>API 응답 데이터와 캐시 생성 시간을 함께 저장하여
     * 캐시 만료 여부를 확인할 수 있습니다.</p>
     * 
     * @author Seoul Fit
     * @since 1.0.0
     */
    private static class CachedResponse {

        /**
         * 캐시된 API 응답 데이터입니다.
         */
        private final Object data;
        
        /**
         * 캐시가 생성된 시간(밀리초)입니다.
         */
        private final long timestamp;

        /**
         * 캐시된 응답 객체를 생성합니다.
         * 
         * @param data 캐시할 API 응답 데이터
         */
        public CachedResponse(Object data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        /**
         * 캐시가 만료되었는지 확인합니다.
         * 
         * @param duration 캐시 유효 기간
         * @return 만료 여부 (true: 만료됨, false: 유효함)
         */
        public boolean isExpired(Duration duration) {
            return System.currentTimeMillis() - timestamp > duration.toMillis();
        }
    }
}
