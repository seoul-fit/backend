package com.seoulfit.backend.env.adapter.in.web.dto;

import com.seoulfit.backend.env.domain.AirQualityEntity;
import lombok.Builder;

/**
 * 대기질 웹 응답 DTO
 */
@Builder
public record AirQualityWebResponse(
        String msrDt,
        String msrRgnNm,
        String pm10Grade,
        String pm25Grade,
        String overallGrade,
        boolean isGoodForOutdoorActivity,
        String qualityMessage
) {

    public static AirQualityWebResponse from(AirQualityEntity airQuality) {
        return AirQualityWebResponse.builder()
                .msrDt(airQuality.getMsrDt().toString())
                .msrRgnNm(airQuality.getMsrRgnNm())
                .pm10Grade(airQuality.getPm10Grade().getDescription())
                .pm25Grade(airQuality.getPm25Grade().getDescription())
                .overallGrade(airQuality.getOverallGrade().getDescription())
                .isGoodForOutdoorActivity(airQuality.isGoodForOutdoorActivity())
                .qualityMessage(airQuality.getQualityMessage())
                .build();
    }
}
