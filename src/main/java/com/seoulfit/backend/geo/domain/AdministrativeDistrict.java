package com.seoulfit.backend.geo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 행정구역 정보를 나타내는 도메인 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministrativeDistrict {
    
    /**
     * 행정구역 코드 (예: 1101053)
     */
    private String adminCode;
    
    /**
     * 행정구역명 (예: 사직동)
     */
    private String adminName;
    
    /**
     * 시도 코드 (예: 11)
     */
    private String sidoCode;
    
    /**
     * 시도명 (예: 서울특별시)
     */
    private String sidoName;
    
    /**
     * 시군구 코드 (예: 010)
     */
    private String sigunguCode;
    
    /**
     * 시군구명 (예: 종로구)
     */
    private String sigunguName;
    
    /**
     * 읍면동 코드 (예: 530)
     */
    private String emdCode;
    
    /**
     * 읍면동명 (예: 사직동)
     */
    private String emdName;
    
    /**
     * 전체 주소 반환
     */
    public String getFullAddress() {
        return String.format("%s %s %s", sidoName, sigunguName, emdName);
    }
    
    /**
     * 행정구역 코드로부터 각 레벨 코드 추출
     */
    public static AdministrativeDistrict fromAdminCode(String adminCode, String adminName) {
        if (adminCode == null || adminCode.length() < 8) {
            throw new IllegalArgumentException("Invalid admin code: " + adminCode);
        }
        
        String sidoCode = adminCode.substring(0, 2);
        String sigunguCode = adminCode.substring(2, 5);
        String emdCode = adminCode.substring(5, 8);
        
        return AdministrativeDistrict.builder()
                .adminCode(adminCode)
                .adminName(adminName)
                .sidoCode(sidoCode)
                .sigunguCode(sigunguCode)
                .emdCode(emdCode)
                .build();
    }
}
