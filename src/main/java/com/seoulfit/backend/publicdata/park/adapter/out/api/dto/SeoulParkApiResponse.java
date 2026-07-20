package com.seoulfit.backend.publicdata.park.adapter.out.api.dto;

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
        
        @JsonProperty("SN")
        private Integer pIdx;

        @JsonProperty("PARK_NM")
        private String pPark;

        @JsonProperty("PARK_OTLN")
        private String pListContent;

        @JsonProperty("AREA")
        private String area;

        @JsonProperty("OPEN_YMD")
        private String openDt;

        @JsonProperty("MAIN_FCLT")
        private String mainEquip;

        @JsonProperty("MAIN_PLNT")
        private String mainPlants;

        @JsonProperty("GD_DOC")
        private String guidance;

        @JsonProperty("VST_ROAD")
        private String visitRoad;

        @JsonProperty("UTZTN_REF")
        private String useRefer;

        @JsonProperty("IMG")
        private String pImg;

        @JsonProperty("RGN")
        private String pZone;

        @JsonProperty("PARK_ADDR")
        private String pAddr;

        @JsonProperty("MNG_DEPT")
        private String pName;

        @JsonProperty("TELNO")
        private String pAdmintel;

        @JsonProperty("XCRD_G")
        private String gLongitude;

        @JsonProperty("YCRD_G")
        private String gLatitude;

        @JsonProperty("XCRD")
        private String longitude;

        @JsonProperty("YCRD")
        private String latitude;

        @JsonProperty("URL")
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
