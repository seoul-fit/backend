package com.seoulfit.backend.publicdata.restaurant.infrastructure.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class GeoCodingUtil {

    private final RestClient restClient;

    @Value("${google.api-key}")
    private String key;

    public static final double EARTH_RADIUS = 6371.0088;

    public Map<String, Double> getGeoData(String address) {
        // 주소가 null이거나 비어있으면 빈 Map 반환
        if (address == null || address.trim().isEmpty()) {
            return new HashMap<>();
        }

        String apiUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + key;

        try {
            // API 호출 및 응답 본문 파싱
            Map body = restClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .body(Map.class);

            if (!"OK".equals(body.get("status"))) {
                log.error("Geocoding API 호출 실패: {} ", body.get("status"));
                return new HashMap<>();
            }

            // 결과 리스트에서 첫 번째 결과의 지오메트리 정보 추출
            List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("results");
            if (results == null || results.isEmpty()) {
                log.error("주소에 대한 Geocoding 결과가 없습니다: {}", address);
                return new HashMap<>();
            }

            Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
            Map<String, Double> location = (Map<String, Double>) geometry.get("location");

            // 위도와 경도 추출
            double lat = location.get("lat");
            double lng = location.get("lng");

            // 결과를 Map에 담아 반환
            Map<String, Double> result = new HashMap<>();
            result.put("latitude", lat);
            result.put("longitude", lng);

            return result;

        } catch (Exception e) {
            // API 호출 중 예외 발생 시 로그를 남기고 빈 Map 반환
            log.error("Geocoding API 호출 중 오류 발생: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}