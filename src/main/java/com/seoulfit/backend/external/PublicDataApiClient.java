package com.seoulfit.backend.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.seoulfit.backend.external.dto.SeoulLibraryInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * 서울시 공공 데이터 API 클라이언트
 * <p>
 * 오픈소스 확장성을 위해 설계된 공공 API 클라이언트 새로운 API 엔드포인트를 쉽게 추가할 수 있도록 구성
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataApiClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${urbanping.api.seoul.base-url:http://openapi.seoul.go.kr:8088}")
    private String baseUrl;

    @Value("${urbanping.api.seoul.api-key:sample}")
    private String apiKey;

    @Value("${urbanping.api.seoul.timeout:10}")
    private int timeoutSeconds;

    @Value("${urbanping.api.seoul.retry-attempts:3}")
    private int retryAttempts;

    // API 응답 캐시 (간단한 메모리 캐시)
    private final Map<String, CachedResponse> responseCache = new ConcurrentHashMap<>();

    /**
     * 서울시 실시간 도시 데이터를 조회합니다.
     *
     * @param locationName 장소명 (예: "광화문·덕수궁")
     * @return API 응답 데이터
     */
    public Mono<Map<String, Object>> getCityData(String locationName) {
        String cacheKey = "citydata_" + locationName;

        // 캐시 확인 (5분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(5))) {
            log.debug("캐시된 도시 데이터 반환: location={}", locationName);
            return Mono.just(cached.data);
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
     * @param startIndex 시작 인덱스
     * @param endIndex   종료 인덱스
     * @return API 응답 데이터
     */
    public Mono<Map<String, Object>> getBikeData(int startIndex, int endIndex) {
        String cacheKey = String.format("bikedata_%d_%d", startIndex, endIndex);

        // 캐시 확인 (3분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(3))) {
            log.debug("캐시된 따릉이 데이터 반환: start={}, end={}", startIndex, endIndex);
            return Mono.just(cached.data);
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
     * 실시간 대기환경 현황을 조회합니다.
     *
     * @param startIndex 시작 인덱스
     * @param endIndex   종료 인덱스
     * @return API 응답 데이터
     */
    public Mono<Map<String, Object>> getAirQualityData(int startIndex, int endIndex) {
        String cacheKey = String.format("airquality_%d_%d", startIndex, endIndex);

        // 캐시 확인 (10분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(10))) {
            log.debug("캐시된 대기질 데이터 반환: start={}, end={}", startIndex, endIndex);
            return Mono.just(cached.data);
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
     * @param startIndex 시작 인덱스
     * @param endIndex   종료 인덱스
     * @return API 응답 데이터
     */
    public Mono<Map<String, Object>> getCulturalEventData(int startIndex, int endIndex) {
        String cacheKey = String.format("cultural_%d_%d", startIndex, endIndex);

        // 캐시 확인 (30분 캐시)
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(Duration.ofMinutes(30))) {
            log.debug("캐시된 문화행사 데이터 반환: start={}, end={}", startIndex, endIndex);
            return Mono.just(cached.data);
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
     * @param startIndex 시작 인덱스
     * @param endIndex   종료 인덱스
     * @return API 응답 데이터
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
     * 일반적인 API 호출 메서드 (확장성을 위한 범용 메서드)
     *
     * @param serviceName   서비스명
     * @param startIndex    시작 인덱스
     * @param endIndex      종료 인덱스
     * @param parameters    추가 파라미터
     * @param cacheDuration 캐시 지속 시간
     * @return API 응답 데이터
     */
    public Mono<Map<String, Object>> callApi(String serviceName, int startIndex, int endIndex,
            String parameters, Duration cacheDuration) {
        String cacheKey = String.format("%s_%d_%d_%s", serviceName, startIndex, endIndex,
                parameters != null ? parameters.hashCode() : "");

        // 캐시 확인
        CachedResponse cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired(cacheDuration)) {
            log.debug("캐시된 API 데이터 반환: service={}", serviceName);
            return Mono.just(cached.data);
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
     * JSON 응답을 파싱합니다.
     *
     * @param jsonString JSON 문자열
     * @return 파싱된 Map 객체
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
     * 캐시를 초기화합니다.
     */
    public void clearCache() {
        responseCache.clear();
        log.info("API 응답 캐시 초기화 완료");
    }

    /**
     * 캐시 상태 정보를 반환합니다.
     *
     * @return 캐시 상태 정보
     */
    public Map<String, Object> getCacheStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("cacheSize", responseCache.size());
        status.put("cacheKeys", responseCache.keySet());
        return status;
    }

    /**
     * 위치 기반 실시간 데이터를 조회합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @return 실시간 데이터 맵
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
            
            // 따릉이 데이터 조회
            Map<String, Object> bikeData = getBikeData(1, 100).block();
            if (bikeData != null) {
                realtimeData.put("BIKE_SHARE", bikeData);
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
     * 캐시된 응답을 담는 내부 클래스
     */
    private static class CachedResponse {

        private final Map<String, Object> data;
        private final long timestamp;

        public CachedResponse(Map<String, Object> data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired(Duration duration) {
            return System.currentTimeMillis() - timestamp > duration.toMillis();
        }
    }
}
