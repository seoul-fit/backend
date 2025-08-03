package com.seoulfit.backend.tmp.presentation.culture.dtos.response;

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
public class SeoulCulturalSpaceApiResponse {

    @JsonProperty("culturalSpaceInfo")
    private CulturalSpaceInfo culturalSpaceInfo;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CulturalSpaceInfo {
        
        @JsonProperty("list_total_count")
        private Integer listTotalCount = 0;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<CulturalSpaceData> row;

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
    public static class CulturalSpaceData {

        @JsonProperty("NUM")
        private Integer num; // 번호

        @JsonProperty("SUBJCODE")
        private String subjCode; // 주제분류

        @JsonProperty("FAC_NAME")
        private String facName; // 문화시설명

        @JsonProperty("ADDR")
        private String addr; // 주소

        @JsonProperty("X_COORD")
        private String xCoord; // 경도 (longitude)

        @JsonProperty("Y_COORD")
        private String yCoord; // 위도 (latitude)

        @JsonProperty("PHNE")
        private String phne; // 전화번호

        @JsonProperty("FAX")
        private String fax; // 팩스번호

        @JsonProperty("HOMEPAGE")
        private String homepage; // 홈페이지

        @JsonProperty("OPENHOUR")
        private String openHour; // 관람시간

        @JsonProperty("ENTR_FEE")
        private String entrFee; // 관람료

        @JsonProperty("CLOSEDAY")
        private String closeDay; // 휴관일

        @JsonProperty("OPEN_DAY")
        private String openDay; // 개관일자

        @JsonProperty("SEAT_CNT")
        private String seatCnt; // 객석수

        @JsonProperty("MAIN_IMG")
        private String mainImg; // 대표이미지

        @JsonProperty("ETC_DESC")
        private String etcDesc; // 기타사항

        @JsonProperty("FAC_DESC")
        private String facDesc; // 시설소개

        @JsonProperty("ENTRFREE")
        private String entrFree; // 무료구분

        @JsonProperty("SUBWAY")
        private String subway; // 지하철

        @JsonProperty("BUSSTOP")
        private String busStop; // 버스정거장

        @JsonProperty("YELLOW")
        private String yellow; // YELLOW (노란색 지하철 노선)

        @JsonProperty("GREEN")
        private String green; // GREEN (초록색 지하철 노선)

        @JsonProperty("BLUE")
        private String blue; // BLUE (파란색 지하철 노선)

        @JsonProperty("RED")
        private String red; // RED (빨간색 지하철 노선)

        @JsonProperty("AIRPORT")
        private String airport; // 공항버스

        /**
         * 필수 데이터 유효성 검증
         */
        public boolean isValid() {
            return facName != null && !facName.trim().isEmpty() &&
                    addr != null && !addr.trim().isEmpty();
        }

        /**
         * 위치 정보 유효성 검증
         */
        public boolean hasValidLocation() {
            try {
                if (yCoord == null || xCoord == null) return false;
                double lat = Double.parseDouble(yCoord.trim());
                double lng = Double.parseDouble(xCoord.trim());
                
                // 서울시 대략적인 좌표 범위 검증
                return lat >= 37.4 && lat <= 37.7 && lng >= 126.7 && lng <= 127.2;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        /**
         * 구 이름 추출 (주소에서)
         */
        public String getDistrict() {
            if (addr == null) return null;
            
            // 서울시 구 이름 패턴 매칭
            String[] districts = {"강남구", "강동구", "강북구", "강서구", "관악구", "광진구", 
                                "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", 
                                "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", 
                                "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"};
            
            for (String district : districts) {
                if (addr.contains(district)) {
                    return district;
                }
            }
            
            return null; // 매칭되지 않으면 null 반환
        }
    }

    // 편의 메서드들 - 기존 코드와의 호환성을 위해
    public Integer getListTotalCount() {
        return culturalSpaceInfo != null ? culturalSpaceInfo.getListTotalCount() : 0;
    }

    public Result getResult() {
        return culturalSpaceInfo != null ? culturalSpaceInfo.getResult() : null;
    }

    public List<CulturalSpaceData> getRow() {
        return culturalSpaceInfo != null ? culturalSpaceInfo.getRow() : null;
    }

    /**
     * 응답 유효성 검증
     */
    public boolean isValid() {
        return culturalSpaceInfo != null && culturalSpaceInfo.getResult() != null;
    }

    /**
     * 성공 응답 여부
     */
    public boolean isSuccess() {
        return isValid() && culturalSpaceInfo.isSuccess();
    }

    /**
     * 데이터 존재 여부
     */
    public boolean hasData() {
        return isValid() && culturalSpaceInfo.hasData();
    }
}
