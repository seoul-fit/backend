package com.seoulfit.backend.env.adapter.out.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirQualityApiResponse {
    @JsonProperty("RealtimeCityAir")
    private RealtimeCityAir RealtimeCityAir;

    @ToString
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class RealtimeCityAir {
        @JsonProperty("list_total_count")
        private int listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<AirQualityRow> row;
    }

    @ToString
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class Result {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String MESSAGE;
    }

    @ToString
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class AirQualityRow {
        private String MSRDT;
        private String MSRRGNNM;
        private String MSRSTENNM;
        private String PM10;
        private String PM25;
        private String O3;
        private String NO2;
        private String CO;
        private String SO2;
        private String KHAI;
        private String KHAIGRADE;
        private String PM10_24H;
        private String PM25_24H;
    }
}

