package com.seoulfit.backend.env.application.service;

import com.seoulfit.backend.env.adapter.in.web.dto.response.SeoulAirQualityApiResponse;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AirEnvService {
    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.environment.service-name[0]}")
    private String serviceName;

    private final RestClientUtils<SeoulAirQualityApiResponse> restClientUtils;

    private static final int MAX_ROWS_PER_REQUEST = 1000;


    public SeoulAirQualityApiResponse fetchAirEnvInfo(int startIndex, int endIndex, String msrgnNmms, String msrsteNm) {
        return fetchAirQuality(startIndex, endIndex, msrgnNmms, msrsteNm);
    }

    private SeoulAirQualityApiResponse fetchAirQuality(int startIndex, int endIndex, String msrgnNmms, String msrsteNm) {
        String url = buildApiUrl(startIndex, endIndex, msrgnNmms, msrsteNm);

        try {
            SeoulAirQualityApiResponse response = restClientUtils.callGetApi(url, SeoulAirQualityApiResponse.class);

            log.info("hi : {}",response.getRealtimeCityAir().getRow().get(0).getArpltMain());

            if (response != null) {
                log.info("Received response from Seoul Air Quality API: {}", response);
                
                if (response.getRealtimeCityAir() != null && response.getRealtimeCityAir().getRow() != null) {
                    int totalCount = response.getRealtimeCityAir().getListTotalCount();
                    int rowCount = response.getRealtimeCityAir().getRow().size();

                    log.info("Successfully fetched {} air quality records (total: {})", rowCount, totalCount);

                    if (response.getRealtimeCityAir().getResult() != null) {
                        String resultCode = response.getRealtimeCityAir().getResult().getCode();
                        String resultMessage = response.getRealtimeCityAir().getResult().getMessage();

                        if (!"INFO-000".equals(resultCode)) {
                            log.warn("Seoul Air Quality API returned non-success code: {} - {}", resultCode, resultMessage);
                        }
                    }
                } else {
                    log.warn("Received response but airQualityInfo or row data is null");
                }
            } else {
                log.error("Received null response from Seoul Air Quality API for URL: {}", url);
            }

            return response;
        } catch (Exception e) {
            log.error("Error calling Seoul Air Quality API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch air quality data: " + e.getMessage(), e);
        }
    }

    private String buildApiUrl(int startIndex, int endIndex, String msrgnNmms, String msrsteNm) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl)
                .append("/").append(serviceName)
                .append("/").append(startIndex)
                .append("/").append(endIndex);

        if (msrgnNmms != null && !msrgnNmms.trim().isEmpty()) {
            urlBuilder.append("/").append(msrgnNmms.trim());
        }

        if (msrsteNm != null && !msrsteNm.trim().isEmpty()) {
            urlBuilder.append("/").append(msrsteNm.trim());
        }

        String url = urlBuilder.toString();
        log.info("Built Air Quality API URL: {}", url);
        return url;
    }


}
