package com.seoulfit.backend.trigger.adapter.out.external;

import com.seoulfit.backend.publicdata.PublicDataApiClient;
import com.seoulfit.backend.trigger.application.port.out.PublicDataPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 공공 데이터 API 어댑터
 * <p>
 * 헥사고날 아키텍처의 출력 어댑터
 * 외부 공공 데이터 API 호출을 담당
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataApiAdapter implements PublicDataPort {

    private final PublicDataApiClient publicDataApiClient;

    @Override
    public Map<String, Object> getCityData(String location) {
        try {
            log.info("서울시 실시간 도시 데이터 조회: location={}", location);
            
            Mono<Map<String, Object>> cityDataMono = publicDataApiClient.getCityData(location);
            Map<String, Object> cityData = cityDataMono.block();
            
            if (cityData != null && !cityData.isEmpty()) {
                return cityData;
            } else {
                log.warn("서울시 도시 데이터가 비어있음: location={}", location);
                return new HashMap<>();
            }
        } catch (Exception e) {
            log.error("서울시 도시 데이터 조회 실패: location={}, error={}", location, e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getBikeShareData() {
        try {
            log.info("따릉이 현황 데이터 조회");
            
            // 따릉이 데이터는 많은 데이터를 가져오므로 페이징 처리
            Mono<Map<String, Object>> bikeDataMono = publicDataApiClient.getBikeData(1, 1000);
            Map<String, Object> bikeData = bikeDataMono.block();
            
            if (bikeData != null && !bikeData.isEmpty()) {
                return bikeData;
            } else {
                log.warn("따릉이 현황 데이터가 비어있음");
                return new HashMap<>();
            }
        } catch (Exception e) {
            log.error("따릉이 현황 데이터 조회 실패: error={}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getAirQualityData() {
        try {
            log.info("실시간 대기환경 데이터 조회");
            
            Mono<Map<String, Object>> airQualityDataMono = publicDataApiClient.getAirQualityData(1, 25);
            Map<String, Object> airQualityData = airQualityDataMono.block();
            
            if (airQualityData != null && !airQualityData.isEmpty()) {
                return airQualityData;
            } else {
                log.warn("대기환경 데이터가 비어있음");
                return new HashMap<>();
            }
        } catch (Exception e) {
            log.error("대기환경 데이터 조회 실패: error={}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getRainfallData() {
        try {
            log.info("강수량 데이터 조회");
            
            // 강수량 정보는 일반 API를 사용하여 조회 (서비스명: rainInfo)
            Mono<Map<String, Object>> rainfallDataMono = publicDataApiClient.callApi(
                    "rainInfo", 1, 100, null, java.time.Duration.ofMinutes(5));
            Map<String, Object> rainfallData = rainfallDataMono.block();
            
            if (rainfallData != null && !rainfallData.isEmpty()) {
                return rainfallData;
            } else {
                log.warn("강수량 데이터가 비어있음");
                return new HashMap<>();
            }
        } catch (Exception e) {
            log.error("강수량 데이터 조회 실패: error={}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getCulturalEventData() {
        try {
            log.info("문화행사 데이터 조회");
            
            Mono<Map<String, Object>> culturalDataMono = publicDataApiClient.getCulturalEventData(1, 200);
            Map<String, Object> culturalData = culturalDataMono.block();
            
            if (culturalData != null && !culturalData.isEmpty()) {
                return culturalData;
            } else {
                log.warn("문화행사 데이터가 비어있음");
                return new HashMap<>();
            }
        } catch (Exception e) {
            log.error("문화행사 데이터 조회 실패: error={}", e.getMessage());
            return new HashMap<>();
        }
    }
}
