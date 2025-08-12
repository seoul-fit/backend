package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 서울시 공공체육시설 프로그램 정보 API 응답 DTO
 */
@Data
public class SportsFacilityProgramApiResponse {

    @JsonProperty("ListProgramByPublicSportsFacilitiesService")
    private ListProgramByPublicSportsFacilitiesService listProgramByPublicSportsFacilitiesService;

    @Data
    public static class ListProgramByPublicSportsFacilitiesService {
        
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<ProgramInfo> row;
    }

    @Data
    public static class Result {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }

    @Data
    public static class ProgramInfo {
        
        @JsonProperty("CENTER_NAME")
        private String centerName;

        @JsonProperty("GROUND_NAME")
        private String groundName;

        @JsonProperty("PROGRAM_NAME")
        private String programName;

        @JsonProperty("SUBJECT_NAME")
        private String subjectName;

        @JsonProperty("PLACE")
        private String place;

        @JsonProperty("ADDRESS")
        private String address;

        @JsonProperty("HOMEPAGE")
        private String homepage;

        @JsonProperty("PARKING_SIDE")
        private String parkingSide;

        @JsonProperty("TEL")
        private String tel;

        @JsonProperty("FAX")
        private String fax;

        @JsonProperty("EMAIL")
        private String email;

        @JsonProperty("CLASS_NAME")
        private String className;

        @JsonProperty("P_LEVEL")
        private String pLevel;

        @JsonProperty("TARGET")
        private String target;

        @JsonProperty("TERM")
        private String term;

        @JsonProperty("WEEK")
        private String week;

        @JsonProperty("CLASS_TIME")
        private String classTime;

        @JsonProperty("FEE")
        private String fee;

        @JsonProperty("INTRO")
        private String intro;

        @JsonProperty("CAPACITY")
        private String capacity;

        @JsonProperty("ENTER_WAY")
        private String enterWay;

        @JsonProperty("ENTER_TERM")
        private String enterTerm;

        @JsonProperty("SELECT_WAY")
        private String selectWay;

        @JsonProperty("ONLINE_LINK")
        private String onlineLink;

        @JsonProperty("USE_YN")
        private String useYn;

        @JsonProperty("CLASS_S")
        private String classS;

        @JsonProperty("CLASS_E")
        private String classE;

        @JsonProperty("FEE_FREE")
        private String feeFree;
    }

    /**
     * API 호출이 성공했는지 확인
     */
    public boolean isSuccess() {
        return listProgramByPublicSportsFacilitiesService != null &&
               listProgramByPublicSportsFacilitiesService.getResult() != null &&
               "INFO-000".equals(listProgramByPublicSportsFacilitiesService.getResult().getCode());
    }

    /**
     * 프로그램 정보 목록 반환
     */
    public List<ProgramInfo> getProgramInfoList() {
        if (listProgramByPublicSportsFacilitiesService != null && 
            listProgramByPublicSportsFacilitiesService.getRow() != null) {
            return listProgramByPublicSportsFacilitiesService.getRow();
        }
        return List.of();
    }

    /**
     * 전체 데이터 개수 반환
     */
    public int getTotalCount() {
        if (listProgramByPublicSportsFacilitiesService != null && 
            listProgramByPublicSportsFacilitiesService.getListTotalCount() != null) {
            return listProgramByPublicSportsFacilitiesService.getListTotalCount();
        }
        return 0;
    }
}
