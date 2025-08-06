package com.seoulfit.backend.amenities.application.service;

import com.seoulfit.backend.shared.utils.RestClientUtils;
import com.seoulfit.backend.amenities.adapter.in.web.dto.AmenitiesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmenitiesService {
    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.amenities.service-name[0]}")
    private String serviceName;

    private final RestClientUtils<AmenitiesResponse> restClientUtils;

    public AmenitiesResponse fetchAmenitiesInfo(int startIndex, int endIndex) {
        return fetchAmenitiesData(startIndex, endIndex);
    }

    private AmenitiesResponse fetchAmenitiesData(int startIndex, int endIndex) {
        String url = String.format("%s/%s/%d/%d/", baseUrl, serviceName, startIndex, endIndex);

        try {
            AmenitiesResponse amenitiesResponse = restClientUtils.callGetApi(url, AmenitiesResponse.class);

            if(amenitiesResponse != null) {
                log.info("Received response from Seoul Amenities API: {}", amenitiesResponse);

                if(amenitiesResponse.getTbGtnHwcwP() != null && amenitiesResponse.getTbGtnHwcwP().getRow() != null) {
                    int totalCount = amenitiesResponse.getTbGtnHwcwP().getListTotalCount();
                    int rowCount = amenitiesResponse.getTbGtnHwcwP().getRow().size();

                    log.info("Successfully fetched {} amenities records (total: {})", rowCount, totalCount);

                    if(amenitiesResponse.getTbGtnHwcwP().getResult() != null) {
                        String resultCode = amenitiesResponse.getTbGtnHwcwP().getResult().getCode();
                        String resultMessage = amenitiesResponse.getTbGtnHwcwP().getResult().getMessage();

                        if(!"INFO-000".equals(resultCode)) {
                            log.warn("Seoul Amenities API returned non-success code: {} - {}", resultCode, resultMessage);
                        }
                    } else {
                        log.warn("Received response but amenitiesInfo or row data is null");
                    }
                }
            }
            return amenitiesResponse;
        } catch (Exception e) {
            log.error("Error calling Seoul Amenities API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch amenities data: " + e.getMessage(), e);
        }
    }

}
