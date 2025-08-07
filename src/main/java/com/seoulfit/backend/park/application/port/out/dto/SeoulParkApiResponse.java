package com.seoulfit.backend.park.application.port.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 서울시 공원 정보 API 응답 DTO
 */
@Data
public class SeoulParkApiResponse {

    @JsonProperty("SearchParkInfoService")
    private SearchParkInfoService searchParkInfoService;

    @Data
    public static class SearchParkInfoService {
        
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<ParkInfo> row;
    }

    @Data
    public static class Result {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }

    @Data
    public static class ParkInfo {
        
        @JsonProperty("P_IDX")
        private Long pIdx;

        @JsonProperty("P_PARK")
        private String pPark;

        @JsonProperty("P_LIST_CONTENT")
        private String pListContent;

        @JsonProperty("AREA")
        private String area;

        @JsonProperty("OPEN_DT")
        private String openDt;

        @JsonProperty("MAIN_EQUIP")
        private String mainEquip;

        @JsonProperty("MAIN_PLANTS")
        private String mainPlants;

        @JsonProperty("GUIDANCE")
        private String guidance;

        @JsonProperty("VISIT_ROAD")
        private String visitRoad;

        @JsonProperty("USE_REFER")
        private String useRefer;

        @JsonProperty("P_IMG")
        private String pImg;

        @JsonProperty("P_ZONE")
        private String pZone;

        @JsonProperty("P_ADDR")
        private String pAddr;

        @JsonProperty("P_NAME")
        private String pName;

        @JsonProperty("P_ADMINTEL")
        private String pAdmintel;

        @JsonProperty("G_LONGITUDE")
        private String gLongitude;

        @JsonProperty("G_LATITUDE")
        private String gLatitude;

        @JsonProperty("LONGITUDE")
        private String longitude;

        @JsonProperty("LATITUDE")
        private String latitude;

        @JsonProperty("TEMPLATE_URL")
        private String templateUrl;
    }

    /**
     * API 호출이 성공했는지 확인
     */
    public boolean isSuccess() {
        return searchParkInfoService != null &&
               searchParkInfoService.getResult() != null &&
               "INFO-000".equals(searchParkInfoService.getResult().getCode());
    }

    /**
     * 공원 정보 목록 반환
     */
    public List<ParkInfo> getParkInfoList() {
        if (searchParkInfoService != null && searchParkInfoService.getRow() != null) {
            return searchParkInfoService.getRow();
        }
        return List.of();
    }

    /**
     * 전체 데이터 개수 반환
     */
    public int getTotalCount() {
        if (searchParkInfoService != null && searchParkInfoService.getListTotalCount() != null) {
            return searchParkInfoService.getListTotalCount();
        }
        return 0;
    }
}
