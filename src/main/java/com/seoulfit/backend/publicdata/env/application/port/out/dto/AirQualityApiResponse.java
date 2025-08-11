package com.seoulfit.backend.publicdata.env.application.port.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 서울시 대기질 API 응답 DTO
 */
@Getter
@Builder
public class AirQualityApiResponse {

    private boolean success;
    private String errorMessage;
    private List<AirQualityData> data;
    private int totalCount;

    /**
     * 성공 응답 생성
     */
    public static AirQualityApiResponse success(List<AirQualityData> data, int totalCount) {
        return AirQualityApiResponse.builder()
                .success(true)
                .data(data)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static AirQualityApiResponse failure(String errorMessage) {
        return AirQualityApiResponse.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * 대기질 데이터 DTO
     */
    @Getter
    @Builder
    public static class AirQualityData {

        @JsonProperty("MSRDT")
        private String msrDt; // 측정일시

        @JsonProperty("MSRRGNNM")
        private String msrRgnNm; // 측정소 지역명

        @JsonProperty("MSRSTENM")
        private String msrSteNm; // 측정소명

        @JsonProperty("PM10")
        private String pm10Value; // PM10 농도

        @JsonProperty("PM25")
        private String pm25Value; // PM2.5 농도

        @JsonProperty("O3")
        private String o3Value; // 오존 농도

        @JsonProperty("NO2")
        private String no2Value; // 이산화질소 농도

        @JsonProperty("CO")
        private String coValue; // 일산화탄소 농도

        @JsonProperty("SO2")
        private String so2Value; // 아황산가스 농도

        @JsonProperty("KHAI")
        private String khaiValue; // 통합대기환경지수

        @JsonProperty("KHAIGRADE")
        private String khaiGrade; // 통합대기환경지수 등급

        @JsonProperty("PM10_24H")
        private String pm1024hAvg; // PM10 24시간 예측이동평균농도

        @JsonProperty("PM25_24H")
        private String pm2524hAvg; // PM2.5 24시간 예측이동평균농도

        /**
         * 문자열을 Integer로 안전하게 변환
         */
        public Integer parseIntegerValue(String value) {
            if (value == null || value.trim().isEmpty() || "-".equals(value.trim())) {
                return null;
            }
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        /**
         * 문자열을 Double로 안전하게 변환
         */
        public Double parseDoubleValue(String value) {
            if (value == null || value.trim().isEmpty() || "-".equals(value.trim())) {
                return null;
            }
            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        /**
         * 측정일시를 LocalDateTime으로 변환
         */
        public LocalDateTime parseMsrDt() {
            if (msrDt == null || msrDt.trim().isEmpty()) {
                return LocalDateTime.now();
            }
            try {
                // 예상 형식: "2024-08-10 14:00"
                return LocalDateTime.parse(msrDt.trim().replace(" ", "T"));
            } catch (Exception e) {
                return LocalDateTime.now();
            }
        }

        // Getter methods for parsed values
        public Integer getPm10ValueAsInteger() {
            return parseIntegerValue(pm10Value);
        }

        public Integer getPm25ValueAsInteger() {
            return parseIntegerValue(pm25Value);
        }

        public Double getO3ValueAsDouble() {
            return parseDoubleValue(o3Value);
        }

        public Double getNo2ValueAsDouble() {
            return parseDoubleValue(no2Value);
        }

        public Double getCoValueAsDouble() {
            return parseDoubleValue(coValue);
        }

        public Double getSo2ValueAsDouble() {
            return parseDoubleValue(so2Value);
        }

        public Integer getKhaiValueAsInteger() {
            return parseIntegerValue(khaiValue);
        }

        public Integer getPm1024hAvgAsInteger() {
            return parseIntegerValue(pm1024hAvg);
        }

        public Integer getPm2524hAvgAsInteger() {
            return parseIntegerValue(pm2524hAvg);
        }
    }
}
