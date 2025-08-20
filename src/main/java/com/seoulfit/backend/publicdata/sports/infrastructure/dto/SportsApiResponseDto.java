package com.seoulfit.backend.publicdata.sports.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 서울시 체육시설 API 응답 DTO
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Data
public class SportsApiResponseDto {
    
    @JsonProperty("ListPublicReservationSport")
    private ListPublicReservationSport listPublicReservationSport;
    
    @Data
    public static class ListPublicReservationSport {
        @JsonProperty("list_total_count")
        private Integer listTotalCount;
        
        @JsonProperty("RESULT")
        private Result result;
        
        @JsonProperty("row")
        private List<SportsRow> row;
    }
    
    @Data
    public static class Result {
        @JsonProperty("CODE")
        private String code;
        
        @JsonProperty("MESSAGE")
        private String message;
    }
    
    @Data
    public static class SportsRow {
        @JsonProperty("GUBUN")
        private String gubun; // 구분
        
        @JsonProperty("SVCID")
        private String svcId; // 서비스ID
        
        @JsonProperty("MAXCLASSNM")
        private String maxClassName; // 대분류명
        
        @JsonProperty("MINCLASSNM")
        private String minClassName; // 소분류명
        
        @JsonProperty("SVCSTATNM")
        private String svcStatName; // 서비스상태명
        
        @JsonProperty("SVCNM")
        private String svcName; // 서비스명
        
        @JsonProperty("PAYATNM")
        private String payAtName; // 결제방법명
        
        @JsonProperty("PLACENM")
        private String placeName; // 장소명
        
        @JsonProperty("USETGTINFO")
        private String useTargetInfo; // 이용대상정보
        
        @JsonProperty("SVCURL")
        private String svcUrl; // 서비스URL
        
        @JsonProperty("X")
        private String x; // X좌표
        
        @JsonProperty("Y")
        private String y; // Y좌표
        
        @JsonProperty("SVCOPNBGNDT")
        private String svcOpenBeginDate; // 서비스개시시작일시
        
        @JsonProperty("SVCOPNENDDT")
        private String svcOpenEndDate; // 서비스개시종료일시
        
        @JsonProperty("RCPTBGNDT")
        private String receiptBeginDate; // 접수시작일시
        
        @JsonProperty("RCPTENDDT")
        private String receiptEndDate; // 접수종료일시
        
        @JsonProperty("AREANM")
        private String areaName; // 지역명
        
        @JsonProperty("IMGURL")
        private String imgUrl; // 이미지URL
        
        @JsonProperty("DTLCONT")
        private String detailContent; // 상세내용
        
        @JsonProperty("TELNO")
        private String telNo; // 전화번호
        
        @JsonProperty("V_MIN")
        private String vMin; // 서비스이용시작시간
        
        @JsonProperty("V_MAX")
        private String vMax; // 서비스이용종료시간
        
        @JsonProperty("REVSTDDAY")
        private String revStdDay; // 취소기준일
        
        @JsonProperty("REVSTDDAYNM")
        private String revStdDayName; // 취소기준일명
    }
}
