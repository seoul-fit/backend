package com.seoulfit.backend.geo.adapter.in.web;

import com.seoulfit.backend.geo.domain.AdministrativeDistrict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 행정구역 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "행정구역 정보")
public class AdministrativeDistrictResponse {
    
    @Schema(description = "행정구역 코드", example = "1101053")
    private String adminCode;
    
    @Schema(description = "행정구역명", example = "사직동")
    private String adminName;
    
    @Schema(description = "시도 코드", example = "11")
    private String sidoCode;
    
    @Schema(description = "시도명", example = "서울특별시")
    private String sidoName;
    
    @Schema(description = "시군구 코드", example = "010")
    private String sigunguCode;
    
    @Schema(description = "시군구명", example = "종로구")
    private String sigunguName;
    
    @Schema(description = "읍면동 코드", example = "530")
    private String emdCode;
    
    @Schema(description = "읍면동명", example = "사직동")
    private String emdName;
    
    @Schema(description = "전체 주소", example = "서울특별시 종로구 사직동")
    private String fullAddress;
    
    /**
     * 도메인 객체로부터 응답 DTO 생성
     */
    public static AdministrativeDistrictResponse from(AdministrativeDistrict district) {
        return AdministrativeDistrictResponse.builder()
                .adminCode(district.getAdminCode())
                .adminName(district.getAdminName())
                .sidoCode(district.getSidoCode())
                .sidoName(district.getSidoName())
                .sigunguCode(district.getSigunguCode())
                .sigunguName(district.getSigunguName())
                .emdCode(district.getEmdCode())
                .emdName(district.getEmdName())
                .fullAddress(district.getFullAddress())
                .build();
    }
}
