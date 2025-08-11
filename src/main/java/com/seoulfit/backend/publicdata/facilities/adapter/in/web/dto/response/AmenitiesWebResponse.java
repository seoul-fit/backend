package com.seoulfit.backend.publicdata.facilities.adapter.in.web.dto.response;

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

}
