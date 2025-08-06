package com.seoulfit.backend.facilities.adapter.in.web.dto;

import com.seoulfit.backend.facilities.domain.CoolingShelter;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * 편의시설 웹 응답 DTO
 */
@Builder
public record AmenitiesWebResponse(
        String facilityName,
        String address,
        String detailedAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String facilityType,
        String remark,
        String areaCode,
        Integer usePersonNumber

) {

    public static AmenitiesWebResponse from(CoolingShelter coolingShelter) {
        return AmenitiesWebResponse.builder()
                .facilityName(coolingShelter.getFacilityName())
                .address(coolingShelter.getLotNumberAddress())
                .detailedAddress(coolingShelter.getDetailedAddress())
                .latitude(coolingShelter.getLatitude())
                .longitude(coolingShelter.getLongitude())
                .facilityType(coolingShelter.getFacilityType1() + coolingShelter.getFacilityType2())
                .usePersonNumber(coolingShelter.getUsePersonNumber())
                .remark(coolingShelter.getRemark())
                .areaCode(coolingShelter.getAreaCode())
                .build();

    }
}
