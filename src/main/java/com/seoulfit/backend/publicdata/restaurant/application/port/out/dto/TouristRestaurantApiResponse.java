package com.seoulfit.backend.publicdata.restaurant.application.port.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 서울시 관광 음식점 정보 API 응답 DTO
 */
@Data
public class TouristRestaurantApiResponse {

    @JsonProperty("TbVwRestaurants")
    private TbVwRestaurants tbVwRestaurants;

    @Data
    public static class TbVwRestaurants {
        
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<RestaurantInfo> row;
    }

    @Data
    public static class Result {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }

    @Data
    public static class RestaurantInfo {
        
        @JsonProperty("POST_SN")
        private String postSn;

        @JsonProperty("LANG_CODE_ID")
        private String langCodeId;

        @JsonProperty("POST_SJ")
        private String postSj;

        @JsonProperty("POST_URL")
        private String postUrl;

        @JsonProperty("ADDRESS")
        private String address;

        @JsonProperty("NEW_ADDRESS")
        private String newAddress;

        @JsonProperty("CMMN_TELNO")
        private String cmmnTelno;

        @JsonProperty("CMMN_HMPG_URL")
        private String cmmnHmpgUrl;

        @JsonProperty("CMMN_USE_TIME")
        private String cmmnUseTime;

        @JsonProperty("SUBWAY_INFO")
        private String subwayInfo;

        @JsonProperty("CMMN_HMPG_LANG")
        private String cmmnHmpgLang;

        @JsonProperty("FD_REPRSNT_MENU")
        private String fdReprsntMenu;
    }

    /**
     * API 호출이 성공했는지 확인
     */
    public boolean isSuccess() {
        return tbVwRestaurants != null &&
               tbVwRestaurants.getResult() != null &&
               "INFO-000".equals(tbVwRestaurants.getResult().getCode());
    }

    /**
     * 음식점 정보 목록 반환
     */
    public List<RestaurantInfo> getRestaurantInfoList() {
        if (tbVwRestaurants != null && tbVwRestaurants.getRow() != null) {
            return tbVwRestaurants.getRow();
        }
        return List.of();
    }

    /**
     * 전체 데이터 개수 반환
     */
    public int getTotalCount() {
        if (tbVwRestaurants != null && tbVwRestaurants.getListTotalCount() != null) {
            return tbVwRestaurants.getListTotalCount();
        }
        return 0;
    }
}
