package com.seoulfit.backend.env.infrastructure.mapper;

import com.seoulfit.backend.env.adapter.out.api.dto.AirQualityApiResponse;
import com.seoulfit.backend.env.domain.AirQuality;

import java.time.LocalDateTime;
import java.util.List;

public class AirQualityMapper {

    public static List<AirQuality> toEntity(AirQualityApiResponse.RealtimeCityAir row) {
        List<AirQualityApiResponse.AirQualityRow> data = row.getRow();

        return data.stream()
                .map(rows -> {
                    return AirQuality.builder()
                            .msrDt(LocalDateTime.parse(rows.getMSRDT()))
                            .msrRgnNm(rows.getMSRRGNNM())
                            .msrSteNm(rows.getMSRSTENNM())
                            .pm10Value(Integer.parseInt(rows.getPM10()))
                            .pm25Value(Integer.parseInt(rows.getPM25()))
                            .o3Value(Double.parseDouble(rows.getO3()))
                            .no2Value(Double.parseDouble(rows.getNO2()))
                            .coValue(Double.parseDouble(rows.getCO()))
                            .so2Value(Double.parseDouble(rows.getSO2()))
                            .khaiValue(Integer.parseInt(rows.getKHAI()))
                            .khaiGrade(rows.getKHAIGRADE())
                            .pm1024hAvg(Integer.parseInt(rows.getPM10_24H()))
                            .pm2524hAvg(Integer.parseInt(rows.getPM25_24H()))
                            .build();
                }).toList();

    }
}
