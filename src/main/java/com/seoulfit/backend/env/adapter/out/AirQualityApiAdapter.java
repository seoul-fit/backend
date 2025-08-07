package com.seoulfit.backend.env.adapter.out;

import com.seoulfit.backend.env.adapter.in.web.dto.response.SeoulAirQualityApiResponse;
import com.seoulfit.backend.env.application.port.out.LoadAirQualityPort;
import com.seoulfit.backend.env.domain.AirQualityEntity;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 대기질 외부 API 어댑터
 * 헥사고날 아키텍처의 Adapter Out
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AirQualityApiAdapter implements LoadAirQualityPort {

    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.environment.service-name[0]}")
    private String serviceName;

    private final RestClientUtils<SeoulAirQualityApiResponse> restClientUtils;

    private static final int MAX_ROWS_PER_REQUEST = 1000;

    @Override
    public List<AirQualityEntity> loadAirQuality(int startIndex, int endIndex, String district, String stationName) {
        String url = buildApiUrl(startIndex, endIndex, district, stationName);

        try {
            SeoulAirQualityApiResponse response = restClientUtils.callGetApi(url, SeoulAirQualityApiResponse.class);

            if (response == null || response.getRealtimeCityAir() == null ||
                    response.getRealtimeCityAir().getRow() == null) {
                log.warn("Received empty response from Seoul Air Quality API");
                return Collections.emptyList();
            }

            List<AirQualityEntity> airQualityList = response.getRealtimeCityAir().getRow().stream()
                    .map(this::mapToAirQuality)
                    .toList();

            log.info("Successfully loaded {} air quality records from API", airQualityList.size());
            return airQualityList;

        } catch (Exception e) {
            log.error("Error loading air quality from API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load air quality data: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<AirQualityEntity> loadAirQualityByDistrict(String district) {
        List<AirQualityEntity> airQualityList = loadAirQuality(1, MAX_ROWS_PER_REQUEST, district, null);
        return null;
    }

    @Override
    public Optional<AirQualityEntity> loadAirQualityByStation(String stationName) {
        List<AirQualityEntity> airQualityList = loadAirQuality(1, MAX_ROWS_PER_REQUEST, null, stationName);
        return null;
    }

    @Override
    public List<AirQualityEntity> loadGoodAirQualityAreas() {
        List<AirQualityEntity> allAirQuality = loadAirQuality(1, MAX_ROWS_PER_REQUEST, null, null);
        return allAirQuality.stream()
                .filter(AirQualityEntity::isGoodForOutdoorActivity)
                .toList();
    }

    /**
     * API URL 생성
     */
    private String buildApiUrl(int startIndex, int endIndex, String district, String stationName) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl).append("/").append(serviceName)
                .append("/").append(startIndex).append("/").append(endIndex).append("/");

        if (district != null && !district.trim().isEmpty()) {
            urlBuilder.append(district).append("/");
        }

        if (stationName != null && !stationName.trim().isEmpty()) {
            urlBuilder.append(stationName);
        }

        return urlBuilder.toString();
    }

    /**
     * API 응답을 도메인 객체로 변환
     */
    private AirQualityEntity mapToAirQuality(SeoulAirQualityApiResponse.AirQualityData row) {
        return AirQualityEntity.builder()
                .msrDt(LocalDateTime.parse(row.getMsrDt()))
                .msrRgnNm(row.getMsrRgnNm())
                .msrSteNm(row.getMsrSteNm())
                .pm10(row.getPm10())
                .pm25(row.getPm25())
                .o3(row.getO3())
                .no2(row.getNo2())
                .co(row.getCo())
                .so2(row.getSo2())
                .idexNm(row.getIdexNm())
                .idexMvl(row.getIdexMvl())
                .arpltMain(row.getArpltMain())
                .build();
    }

    private LocalDateTime parseMeasureTime(String measureTime) {
        try {
            if (measureTime != null && !measureTime.trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(measureTime, formatter);
            }
        } catch (Exception e) {
            log.warn("Failed to parse measure time: {}", measureTime);
        }
        return LocalDateTime.now();
    }

    private Double parseDouble(String value) {
        try {
            return value != null && !value.trim().isEmpty() && !"-".equals(value) ?
                    Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            log.warn("Failed to parse double value: {}", value);
            return null;
        }
    }
}
