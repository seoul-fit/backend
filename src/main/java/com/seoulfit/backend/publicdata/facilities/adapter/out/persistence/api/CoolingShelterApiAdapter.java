package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.api;

import com.seoulfit.backend.publicdata.facilities.adapter.in.web.dto.response.AmenitiesResponse;
import com.seoulfit.backend.publicdata.facilities.application.port.out.LoadCoolingShelterPort;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 편의시설 외부 API 어댑터
 * 헥사고날 아키텍처의 Adapter Out
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CoolingShelterApiAdapter implements LoadCoolingShelterPort {
    
    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.amenities.service-name[0]}")
    private String serviceName;

    private final RestClientUtils<AmenitiesResponse> restClientUtils;

    @Override
    public List<CoolingCenter> loadAmenities(int startIndex, int endIndex) {
        String url = String.format("%s/%s/%d/%d/", baseUrl, serviceName, startIndex, endIndex);

        try {
            AmenitiesResponse response = restClientUtils.callGetApi(url, AmenitiesResponse.class);
            log.info("Amenities response Size: {}", response.getTbGtnHwcwP().getListTotalCount());
            
            List<CoolingCenter> coolingShelterList = response.getTbGtnHwcwP().getRow().stream()
                .map(this::mapToCoolingShelter)
                .toList();

            log.info("Successfully loaded {} from API", coolingShelterList.size());
            return coolingShelterList;

        } catch (Exception e) {
            log.error("Error loading amenities from API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load amenities data: " + e.getMessage(), e);
        }
    }

    /**
     * API 응답을 도메인 객체로 변환
     */
    private CoolingCenter mapToCoolingShelter(AmenitiesResponse.AmenitiesData row) {
        return CoolingCenter.builder()
                .facilityYear(Integer.parseInt(row.getYear()))
                .areaCode(row.getAreaCode())
                .facilityType1(row.getFacilityType1())
                .facilityType2(row.getFacilityType2())
                .name(row.getFacilityName())
                .roadAddress(row.getDetailedAddress())
                .lotAddress(row.getLotNumberAddress())
                .areaSize(row.getAreaSquareMeters())
                .remarks(row.getRemark())
                .longitude(Double.parseDouble(row.getLongitude().toString()))
                .latitude(Double.parseDouble(row.getLatitude().toString()))
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
