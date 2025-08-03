package com.seoulfit.backend.tmp.presentation.culture.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeoulApiResponse {

    @JsonProperty("culturalEventInfo")
    private CulturalEventInfo culturalEventInfo;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CulturalEventInfo {
        
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<CulturalEventData> row;

        public boolean hasData() {
            return row != null && !row.isEmpty();
        }

        public boolean isSuccess() {
            return result != null && "INFO-000".equals(result.getCode());
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
    public static class CulturalEventData {
        
        @JsonProperty("CODENAME")
        private String codeName;

        @JsonProperty("GUNAME")
        private String guName;

        @JsonProperty("TITLE")
        private String title;

        @JsonProperty("DATE")
        private String date;

        @JsonProperty("PLACE")
        private String place;

        @JsonProperty("ORG_NAME")
        private String orgName;

        @JsonProperty("USE_TRGT")
        private String useTarget;

        @JsonProperty("USE_FEE")
        private String useFee;

        @JsonProperty("PLAYER")
        private String player;

        @JsonProperty("PROGRAM")
        private String program;

        @JsonProperty("ETC_DESC")
        private String etcDesc;

        @JsonProperty("ORG_LINK")
        private String orgLink;

        @JsonProperty("MAIN_IMG")
        private String mainImg;

        @JsonProperty("RGSTDATE")
        private String registrationDate;

        @JsonProperty("TICKET")
        private String ticket;

        @JsonProperty("STRTDATE")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
        private LocalDateTime startDate;

        @JsonProperty("END_DATE")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
        private LocalDateTime endDate;

        @JsonProperty("THEMECODE")
        private String themeCode;

        @JsonProperty("LOT")
        private String latitude;

        @JsonProperty("LAT")
        private String longitude;

        @JsonProperty("IS_FREE")
        private String isFree;

        @JsonProperty("HMPG_ADDR")
        private String homepageAddr;

        /**
         * 필수 데이터 유효성 검증
         */
        public boolean isValid() {
            return title != null && !title.trim().isEmpty() &&
                   guName != null && !guName.trim().isEmpty();
        }

        /**
         * 위치 정보 유효성 검증
         */
        public boolean hasValidLocation() {
            try {
                if (latitude == null || longitude == null) return false;
                double lat = Double.parseDouble(latitude.trim());
                double lng = Double.parseDouble(longitude.trim());
                
                // 서울시 대략적인 좌표 범위 검증
                return lat >= 37.4 && lat <= 37.7 && lng >= 126.7 && lng <= 127.2;
            } catch (NumberFormatException e) {
                return false;
            }
        }

    }

    /**
     * 응답 유효성 검증
     */
    public boolean isValid() {
        return culturalEventInfo != null && culturalEventInfo.getResult() != null;
    }

    /**
     * 성공 응답 여부
     */
    public boolean isSuccess() {
        return isValid() && culturalEventInfo.isSuccess();
    }

    /**
     * 데이터 존재 여부
     */
    public boolean hasData() {
        return isValid() && culturalEventInfo.hasData();
    }
}
