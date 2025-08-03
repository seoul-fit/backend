package com.seoulfit.backend.presentation.culture.dtos.response;

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
public class SeoulReservationApiResponse {

    @JsonProperty("ListPublicReservationSport")
    private ReservationInfo reservationInfo;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReservationInfo {
        
        @JsonProperty("list_total_count")
        private Integer listTotalCount = 0;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<ReservationData> row;

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
    public static class ReservationData {

        @JsonProperty("GUBUN")
        private String gubun; // 서비스구분

        @JsonProperty("SVCID")
        private String svcId; // 서비스ID

        @JsonProperty("MAXCLASSNM")
        private String maxClassNm; // 대분류명

        @JsonProperty("MINCLASSNM")
        private String minClassNm; // 소분류명

        @JsonProperty("SVCSTATNM")
        private String svcStatNm; // 서비스상태

        @JsonProperty("SVCNM")
        private String svcNm; // 서비스명

        @JsonProperty("PAYATNM")
        private String payAtNm; // 결제방법

        @JsonProperty("PLACENM")
        private String placeNm; // 장소명

        @JsonProperty("USETGTINFO")
        private String useTgtInfo; // 서비스대상

        @JsonProperty("SVCURL")
        private String svcUrl; // 바로가기URL

        @JsonProperty("X")
        private String x; // 장소X좌표 (경도)

        @JsonProperty("Y")
        private String y; // 장소Y좌표 (위도)

        @JsonProperty("SVCOPNBGNDT")
        private String svcOpnBgnDt; // 서비스개시시작일시 (문자열로 받아서 파싱)

        @JsonProperty("SVCOPNENDDT")
        private String svcOpnEndDt; // 서비스개시종료일시

        @JsonProperty("RCPTBGNDT")
        private String rcptBgnDt; // 접수시작일시

        @JsonProperty("RCPTENDDT")
        private String rcptEndDt; // 접수종료일시

        @JsonProperty("AREANM")
        private String areaNm; // 지역명

        @JsonProperty("IMGURL")
        private String imgUrl; // 이미지경로

        @JsonProperty("DTLCONT")
        private String dtlCont; // 상세내용

        @JsonProperty("TELNO")
        private String telNo; // 전화번호

        @JsonProperty("V_MIN")
        private String vMin; // 서비스이용 시작시간

        @JsonProperty("V_MAX")
        private String vMax; // 서비스이용 종료시간

        @JsonProperty("REVSTDDAYNM")
        private String revStdDayNm; // 취소기간 기준정보

        @JsonProperty("REVSTDDAY")
        private String revStdDay; // 취소기간 기준일까지

        /**
         * 필수 데이터 유효성 검증
         */
        public boolean isValid() {
            return svcNm != null && !svcNm.trim().isEmpty() &&
                    placeNm != null && !placeNm.trim().isEmpty();
        }

        /**
         * 위치 정보 유효성 검증
         */
        public boolean hasValidLocation() {
            try {
                if (y == null || x == null) return false;
                double lat = Double.parseDouble(y.trim());
                double lng = Double.parseDouble(x.trim());
                
                // 서울시 대략적인 좌표 범위 검증
                return lat >= 37.4 && lat <= 37.7 && lng >= 126.7 && lng <= 127.2;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        /**
         * 활성 상태 확인
         */
        public boolean isActive() {
            return "접수중".equals(svcStatNm) || "예약중".equals(svcStatNm);
        }

        /**
         * 구 이름 추출 (지역명에서)
         */
        public String getDistrict() {
            if (areaNm == null) return null;
            
            // 서울시 구 이름 패턴 매칭
            String[] districts = {"강남구", "강동구", "강북구", "강서구", "관악구", "광진구", 
                                "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구", 
                                "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", 
                                "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"};
            
            for (String district : districts) {
                if (areaNm.contains(district)) {
                    return district;
                }
            }
            
            return areaNm; // 매칭되지 않으면 원본 반환
        }
    }

    // 편의 메서드들 - 기존 코드와의 호환성을 위해
    public Integer getListTotalCount() {
        return reservationInfo != null ? reservationInfo.getListTotalCount() : 0;
    }

    public Result getResult() {
        return reservationInfo != null ? reservationInfo.getResult() : null;
    }

    public List<ReservationData> getRow() {
        return reservationInfo != null ? reservationInfo.getRow() : null;
    }

    /**
     * 응답 유효성 검증
     */
    public boolean isValid() {
        return reservationInfo != null && reservationInfo.getResult() != null;
    }

    /**
     * 성공 응답 여부
     */
    public boolean isSuccess() {
        return isValid() && reservationInfo.isSuccess();
    }

    /**
     * 데이터 존재 여부
     */
    public boolean hasData() {
        return isValid() && reservationInfo.hasData();
    }
}