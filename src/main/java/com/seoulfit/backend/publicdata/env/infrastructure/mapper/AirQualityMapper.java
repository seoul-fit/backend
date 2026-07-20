package com.seoulfit.backend.publicdata.env.infrastructure.mapper;

import com.seoulfit.backend.publicdata.env.adapter.out.api.dto.AirQualityApiResponse;
import com.seoulfit.backend.publicdata.env.domain.AirQuality;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AirQualityMapper {

    private static final DateTimeFormatter SEOUL_API_DATE_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public static List<AirQuality> toEntity(AirQualityApiResponse.RealtimeCityAir row) {
        List<AirQualityApiResponse.AirQualityRow> data = row.getRow();

        return data.stream()
                .map(rows -> {
                    return AirQuality.builder()
                            .msrDt(parseMeasureTime(rows.getMSRDT()))
                            .msrRgnNm(rows.getMSRRGNNM())
                            .msrSteNm(rows.getMSRSTENNM())
                            .pm10Value(parseInteger(rows.getPM10()))
                            .pm25Value(parseInteger(rows.getPM25()))
                            .o3Value(parseDouble(rows.getO3()))
                            .no2Value(parseDouble(rows.getNO2()))
                            .coValue(parseDouble(rows.getCO()))
                            .so2Value(parseDouble(rows.getSO2()))
                            .khaiValue(parseInteger(rows.getKHAI()))
                            .khaiGrade(rows.getKHAIGRADE())
                            .pm1024hAvg(parseInteger(rows.getPM10_24H()))
                            .pm2524hAvg(parseInteger(rows.getPM25_24H()))
                            .build();
                }).toList();

    }

    public static LocalDateTime parseMeasureTime(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("측정 일시가 비어 있습니다.");
        }

        String trimmed = value.trim();
        if (trimmed.length() == 12 && trimmed.chars().allMatch(Character::isDigit)) {
            return LocalDateTime.parse(trimmed, SEOUL_API_DATE_TIME);
        }
        return LocalDateTime.parse(trimmed);
    }

    public static Integer parseInteger(String value) {
        if (value == null || value.isBlank() || "-".equals(value.trim())) {
            return null;
        }
        return (int) Double.parseDouble(value.trim());
    }

    public static Double parseDouble(String value) {
        if (value == null || value.isBlank() || "-".equals(value.trim())) {
            return null;
        }
        return Double.parseDouble(value.trim());
    }
}
