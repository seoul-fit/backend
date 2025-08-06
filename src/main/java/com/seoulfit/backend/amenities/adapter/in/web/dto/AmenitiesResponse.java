package com.seoulfit.backend.amenities.adapter.in.web.dto;

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
public class AmenitiesResponse {
    private TbGtnHwcwP tbGtnHwcwP;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TbGtnHwcwP {
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<AmenitiesData> row;
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

        public boolean isSuccess() {
            return "INFO-000".equals(code);
        }

        public boolean isError() {
            return code != null && code.startsWith("ERROR");
        }
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AmenitiesData {
        @JsonProperty("YEAR")
        private String year;

        @JsonProperty("AREA_CD")
        private String areaCode;

        @JsonProperty("FACILITY_TYPE1")
        private String facilityType1;

        @JsonProperty("FACILITY_TYPE2")
        private String facilityType2;

        @JsonProperty("R_AREA_NM")
        private String facilityName;

        @JsonProperty("R_DETL_ADD")
        private String detailedAddress;

        @JsonProperty("LOTNO_ADDR")
        private String lotNumberAddress;

        @JsonProperty("R_AREA_SQR")
        private Double areaSquareMeters;

        @JsonProperty("USE_PRNB")
        private Integer usePersonNumber;

        @JsonProperty("RMRK")
        private String remark;

        @JsonProperty("LON")
        private String longitude;

        @JsonProperty("LAT")
        private String latitude;

        @JsonProperty("MAP_COORD_X")
        private Double mapCoordX;

        @JsonProperty("MAP_COORD_Y")
        private Double mapCoordY;
    }

}