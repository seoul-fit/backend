package com.seoulfit.backend.publicdata.env.adapter.out.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
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
        @JsonProperty("MSRMT_DT")
        private String MSRDT;

        @JsonProperty("SAREA_NM")
        private String MSRRGNNM;

        @JsonProperty("MSRSTN_NM")
        private String MSRSTENNM;

        @JsonProperty("PM")
        private String PM10;

        @JsonProperty("FPM")
        private String PM25;

        @JsonProperty("OZON")
        private String O3;

        @JsonProperty("NTDX")
        private String NO2;

        @JsonProperty("CBMX")
        private String CO;

        @JsonProperty("SPDX")
        private String SO2;

        @JsonProperty("CAI_IDX")
        private String KHAI;

        @JsonProperty("CAI_GRD")
        private String KHAIGRADE;

        @JsonProperty("CRST_SBSTN")
        private String ARPLTMAIN;

        // The replacement API no longer supplies the legacy 24-hour moving averages.
        private String PM10_24H;
        private String PM25_24H;
    }
}

