package com.seoulfit.backend.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
@Getter
public class SeoulLibraryInfoDto {
    @JsonProperty("SeoulPublicLibraryInfo")
    private SeoulPublicLibraryInfo seoulPublicLibraryInfo;

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeoulPublicLibraryInfo {
        @JsonProperty("list_total_count")
        private int listTotalCount;

        @JsonProperty("RESULT")
        private ResultDTO result;

        @JsonProperty("row")
        private List<LibraryRowDTO> rows;
    }


    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultDTO {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;

        // Getters and Setters
    }

    @Getter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LibraryRowDTO {
        @JsonProperty("LBRRY_SEQ_NO")
        private String lbrrySeqNo;

        @JsonProperty("LBRRY_NAME")
        private String lbrryName;

        @JsonProperty("GU_CODE")
        private String guCode;

        @JsonProperty("CODE_VALUE")
        private String codeValue;

        @JsonProperty("ADRES")
        private String adres;

        @JsonProperty("TEL_NO")
        private String telNo;

        @JsonProperty("HMPG_URL")
        private String hmpgUrl;

        @JsonProperty("OP_TIME")
        private String opTime;

        @JsonProperty("FDRM_CLOSE_DATE")
        private String fdrmCloseDate;

        @JsonProperty("LBRRY_SE_NAME")
        private String lbrrySeName;

        @JsonProperty("XCNTS")
        private double xcnts;

        @JsonProperty("YDNTS")
        private double ydnts;

        // Getters and Setters
    }
}
