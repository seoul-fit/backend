package com.seoulfit.backend.env.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seoulfit.backend.env.domain.AirQuality;
import com.seoulfit.backend.env.domain.AirQualityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 대기질 정보 응답 DTO
 */
@Getter
@Builder
@Schema(description = "대기질 정보 응답")
public class AirQualityResponse {

    @Schema(description = "대기질 정보 ID", example = "1")
    private Long id;

    @Schema(description = "측정일시", example = "2024-08-10T14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime measureDateTime;

    @Schema(description = "측정소 지역명", example = "강남구")
    private String regionName;

    @Schema(description = "측정소명", example = "강남구")
    private String stationName;

    @Schema(description = "PM10 농도 (㎍/㎥)", example = "45")
    private Integer pm10Value;

    @Schema(description = "PM2.5 농도 (㎍/㎥)", example = "25")
    private Integer pm25Value;

    @Schema(description = "오존 농도 (ppm)", example = "0.045")
    private Double o3Value;

    @Schema(description = "이산화질소 농도 (ppm)", example = "0.025")
    private Double no2Value;

    @Schema(description = "일산화탄소 농도 (ppm)", example = "0.5")
    private Double coValue;

    @Schema(description = "아황산가스 농도 (ppm)", example = "0.003")
    private Double so2Value;

    @Schema(description = "통합대기환경지수", example = "85")
    private Integer khaiValue;

    @Schema(description = "통합대기환경지수 등급", example = "보통")
    private String khaiGrade;

    @Schema(description = "PM10 24시간 예측이동평균농도 (㎍/㎥)", example = "42")
    private Integer pm1024hAvg;

    @Schema(description = "PM2.5 24시간 예측이동평균농도 (㎍/㎥)", example = "23")
    private Integer pm2524hAvg;

    @Schema(description = "대기질 상태", example = "MODERATE")
    private AirQualityStatus airQualityStatus;

    @Schema(description = "대기질 상태 설명", example = "보통")
    private String airQualityDescription;

    @Schema(description = "PM2.5 기준 대기질 상태", example = "GOOD")
    private AirQualityStatus pm25Status;

    @Schema(description = "알림 필요 여부", example = "false")
    private boolean requiresNotification;

    @Schema(description = "민감군 주의 필요 여부", example = "false")
    private boolean requiresSensitiveGroupAlert;

    @Schema(description = "생성일시", example = "2024-08-10T14:05:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-08-10T14:05:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * AirQuality 도메인 객체로부터 응답 DTO 생성
     */
    public static AirQualityResponse from(AirQuality airQuality) {
        AirQualityStatus status = airQuality.getAirQualityStatus();
        AirQualityStatus pm25Status = airQuality.getPm25Status();

        return AirQualityResponse.builder()
            .id(airQuality.getId())
            .measureDateTime(airQuality.getMsrDt())
            .regionName(airQuality.getMsrRgnNm())
            .stationName(airQuality.getMsrSteNm())
            .pm10Value(airQuality.getPm10Value())
            .pm25Value(airQuality.getPm25Value())
            .o3Value(airQuality.getO3Value())
            .no2Value(airQuality.getNo2Value())
            .coValue(airQuality.getCoValue())
            .so2Value(airQuality.getSo2Value())
            .khaiValue(airQuality.getKhaiValue())
            .khaiGrade(airQuality.getKhaiGrade())
            .pm1024hAvg(airQuality.getPm1024hAvg())
            .pm2524hAvg(airQuality.getPm2524hAvg())
            .airQualityStatus(status)
            .airQualityDescription(status.getDescription())
            .pm25Status(pm25Status)
            .requiresNotification(status.requiresNotification())
            .requiresSensitiveGroupAlert(status.requiresSensitiveGroupAlert())
            .createdAt(airQuality.getCreatedAt())
            .updatedAt(airQuality.getUpdatedAt())
            .build();
    }

    /**
     * 대기질 상태에 따른 색상 코드 반환
     */
    @Schema(description = "대기질 상태 색상 코드", example = "#4CAF50")
    public String getStatusColor() {
        return switch (airQualityStatus) {
            case GOOD -> "#4CAF50";              // 녹색
            case MODERATE -> "#FFC107";          // 노란색
            case UNHEALTHY_FOR_SENSITIVE -> "#FF9800"; // 주황색
            case UNHEALTHY -> "#F44336";         // 빨간색
            case VERY_UNHEALTHY -> "#9C27B0";    // 보라색
            case HAZARDOUS -> "#795548";         // 갈색
            case UNKNOWN -> "#9E9E9E";           // 회색
        };
    }

    /**
     * 대기질 상태에 따른 권고사항 반환
     */
    @Schema(description = "대기질 상태별 권고사항")
    public String getRecommendation() {
        return switch (airQualityStatus) {
            case GOOD -> "야외활동하기 좋은 날씨입니다.";
            case MODERATE -> "보통 수준의 대기질입니다. 평상시 활동 가능합니다.";
            case UNHEALTHY_FOR_SENSITIVE -> "민감군은 장시간 또는 무리한 실외활동을 줄이는 것이 좋습니다.";
            case UNHEALTHY -> "장시간 또는 무리한 실외활동을 줄이고, 실외활동 시 마스크를 착용하세요.";
            case VERY_UNHEALTHY -> "불필요한 실외활동을 자제하고, 실외활동 시 마스크를 반드시 착용하세요.";
            case HAZARDOUS -> "실외활동을 중단하고 실내에 머물러 주세요.";
            case UNKNOWN -> "대기질 정보를 확인할 수 없습니다.";
        };
    }

    /**
     * 간단한 대기질 정보 요약
     */
    @Schema(description = "대기질 정보 요약")
    public AirQualitySummary getSummary() {
        return new AirQualitySummary(
            stationName,
            airQualityStatus,
            airQualityDescription,
            pm10Value,
            pm25Value,
            khaiValue,
            measureDateTime
        );
    }

    /**
     * 대기질 정보 요약 DTO
     */
    public record AirQualitySummary(
        @Schema(description = "측정소명", example = "강남구")
        String stationName,
        
        @Schema(description = "대기질 상태", example = "MODERATE")
        AirQualityStatus status,
        
        @Schema(description = "대기질 상태 설명", example = "보통")
        String statusDescription,
        
        @Schema(description = "PM10 농도", example = "45")
        Integer pm10Value,
        
        @Schema(description = "PM2.5 농도", example = "25")
        Integer pm25Value,
        
        @Schema(description = "통합대기환경지수", example = "85")
        Integer khaiValue,
        
        @Schema(description = "측정일시", example = "2024-08-10T14:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime measureDateTime
    ) {}
}
