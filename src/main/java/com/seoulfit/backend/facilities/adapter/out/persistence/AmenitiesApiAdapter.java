package com.seoulfit.backend.facilities.adapter.out.persistence;

import com.seoulfit.backend.facilities.adapter.in.web.dto.AmenitiesResponse;
import com.seoulfit.backend.facilities.application.port.out.LoadAmenitiesPort;
import com.seoulfit.backend.facilities.domain.CoolingShelter;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 편의시설 외부 API 어댑터
 * 헥사고날 아키텍처의 Adapter Out
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AmenitiesApiAdapter implements LoadAmenitiesPort {
    
    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.amenities.service-name[0]}")
    private String serviceName;

    private final RestClientUtils<AmenitiesResponse> restClientUtils;

    @Override
    public List<CoolingShelter> loadAmenities(int startIndex, int endIndex) {
        String url = String.format("%s/%s/%d/%d/", baseUrl, serviceName, startIndex, endIndex);

        try {
            AmenitiesResponse response = restClientUtils.callGetApi(url, AmenitiesResponse.class);
            
            if (response == null || response.getTbGtnHwcwP() == null || 
                response.getTbGtnHwcwP().getRow() == null) {
                log.warn("Received empty response from Seoul Amenities API");
                return Collections.emptyList();
            }

            List<CoolingShelter> coolingShelterList = response.getTbGtnHwcwP().getRow().stream()
                .map(this::mapToCoolingShelter)
                .toList();

            log.info("Successfully loaded {} amenities from API", coolingShelterList.size());
            return coolingShelterList;

        } catch (Exception e) {
            log.error("Error loading amenities from API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load amenities data: " + e.getMessage(), e);
        }
    }

    /**
     * API 응답을 도메인 객체로 변환
     */
    private CoolingShelter mapToCoolingShelter(AmenitiesResponse.AmenitiesData row) {
        return CoolingShelter.builder()
                .year(row.getYear())
                .areaCode(row.getAreaCode())
                .facilityType1(row.getFacilityType1())
                .facilityType2(row.getFacilityType2())
                .facilityName(row.getFacilityName())
                .detailedAddress(row.getDetailedAddress())
                .lotNumberAddress(row.getLotNumberAddress())
                .areaSquareMeters(row.getAreaSquareMeters())
                .usePersonNumber(row.getUsePersonNumber())
                .remark(row.getRemark())
                .longitude(BigDecimal.valueOf(Long.parseLong(row.getLongitude())))
                .latitude(BigDecimal.valueOf(Long.parseLong(row.getLatitude())))
                .mapCoordX(row.getMapCoordX())
                .mapCoordY(row.getMapCoordY())
                .build();
    }

    private BigDecimal parseDecimal(String value) {
        try {
            return value != null && !value.trim().isEmpty() ? new BigDecimal(value) : null;
        } catch (NumberFormatException e) {
            log.warn("Failed to parse decimal value: {}", value);
            return null;
        }
    }

}
