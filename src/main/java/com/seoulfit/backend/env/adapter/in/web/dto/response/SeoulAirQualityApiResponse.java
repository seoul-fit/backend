package com.seoulfit.backend.env.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeoulAirQualityApiResponse {

    @JsonProperty("RealtimeCityAir")
    private RealTimeCityAir realtimeCityAir;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RealTimeCityAir {
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<AirQualityData> row;

        /**
         * 응답 유효성 검증
         */
        public boolean isValid() {
            return result != null;
        }

        /**
         * 성공 응답 여부
         */
        public boolean isSuccess() {
            return isValid() && "INFO-000".equals(result.getCode());
        }

        /**
         * 데이터 존재 여부
         */
        public boolean hasData() {
            return row != null && !row.isEmpty();
        }
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AirQualityData {

        @JsonProperty("MSRDT")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private String msrDt; // 측정일시

        @JsonProperty("MSRRGN_NM")
        private String msrRgnNm; // 권역명

        @JsonProperty("MSRSTE_NM")
        private String msrSteNm; // 측정소명

        @JsonProperty("PM10")
        private Integer pm10; // 미세먼지(㎍/㎥)

        @JsonProperty("PM25")
        private Integer pm25; // 초미세먼지농도(㎍/㎥)

        @JsonProperty("O3")
        private Double o3; // 오존(ppm)

        @JsonProperty("NO2")
        private Double no2; // 이산화질소농도(ppm)

        @JsonProperty("CO")
        private Double co; // 일산화탄소농도(ppm)

        @JsonProperty("SO2")
        private Double so2; // 아황산가스농도(ppm)

        @JsonProperty("IDEX_NM")
        private String idexNm; // 통합대기환경등급

        @JsonProperty("IDEX_MVL")
        private Integer idexMvl; // 통합대기환경지수

        @JsonProperty("ARPLT_MAIN")
        private String arpltMain; // 지수결정물질

        /**
         * 필수 데이터 유효성 검증 (예: 측정소명, 측정일시 필수)
         */
        public boolean isValid() {
            return msrSteNm != null && !msrSteNm.trim().isEmpty() &&
                    msrDt != null;
        }

        /**
         * 대기질 데이터 유효성 검증 (예: 주요 수치가 null이 아닌지 확인)
         */
        public boolean hasValidAirQuality() {
            return pm10 != null || pm25 != null || o3 != null ||
                    no2 != null || co != null || so2 != null;
        }
    }
}