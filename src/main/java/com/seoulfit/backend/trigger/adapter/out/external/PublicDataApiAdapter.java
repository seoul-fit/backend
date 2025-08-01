package com.seoulfit.backend.trigger.adapter.out.external;

import com.seoulfit.backend.trigger.application.port.out.PublicDataPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

    private final WebClient webClient;

    @Override
    public Map<String, Object> getCityData(String location) {
        try {
            // TODO: 실제 서울시 실시간 도시 데이터 API 호출 구현
            log.info("서울시 실시간 도시 데이터 조회: location={}", location);
            
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("location", location);
            mockData.put("temperature", 25.5);
            mockData.put("humidity", 60);
            mockData.put("congestionLevel", "보통");
            
            return mockData;
        } catch (Exception e) {
            log.error("서울시 도시 데이터 조회 실패: location={}, error={}", location, e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getBikeShareData() {
        try {
            // TODO: 실제 따릉이 현황 API 호출 구현
            log.info("따릉이 현황 데이터 조회");
            
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("totalStations", 2000);
            mockData.put("availableBikes", 15000);
            mockData.put("emptyStations", 50);
            
            return mockData;
        } catch (Exception e) {
            log.error("따릉이 현황 데이터 조회 실패: error={}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getAirQualityData() {
        try {
            // TODO: 실제 대기환경 현황 API 호출 구현
            log.info("실시간 대기환경 데이터 조회");
            
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("pm10", 45);
            mockData.put("pm25", 25);
            mockData.put("o3", 0.05);
            mockData.put("grade", "보통");
            
            return mockData;
        } catch (Exception e) {
            log.error("대기환경 데이터 조회 실패: error={}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getRainfallData() {
        try {
            // TODO: 실제 강수량 정보 API 호출 구현
            log.info("강수량 데이터 조회");
            
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("rainfall10min", 0.0);
            mockData.put("rainfallHour", 0.0);
            mockData.put("rainfallDay", 5.2);
            
            return mockData;
        } catch (Exception e) {
            log.error("강수량 데이터 조회 실패: error={}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getCulturalEventData() {
        try {
            // TODO: 실제 문화행사 정보 API 호출 구현
            log.info("문화행사 데이터 조회");
            
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("totalEvents", 150);
            mockData.put("todayEvents", 12);
            mockData.put("weekendEvents", 45);
            
            return mockData;
        } catch (Exception e) {
            log.error("문화행사 데이터 조회 실패: error={}", e.getMessage());
            return new HashMap<>();
        }
    }
}
