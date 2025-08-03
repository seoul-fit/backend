package com.seoulfit.backend.application.service.impl;

import com.seoulfit.backend.domain.CulturalSpace;
import com.seoulfit.backend.infra.CulturalSpaceRepository;
import com.seoulfit.backend.infra.mapper.CulturalSpaceMapper;
import com.seoulfit.backend.presentation.culture.dtos.response.SeoulCulturalSpaceApiResponse;
import com.seoulfit.backend.utils.RestClientUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CulturalSpaceServiceImpl {

    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.culture.service-name[2]}")
    private String serviceName;

    private final EntityManager entityManager;
    private final CulturalSpaceMapper culturalSpaceMapper;
    private final CulturalSpaceRepository culturalSpaceRepository;

    private final RestClientUtils<SeoulCulturalSpaceApiResponse> restClientUtils;

    @Transactional
    public SeoulCulturalSpaceApiResponse saveCultureSpace(int startIndex, int endIndex) {

        SeoulCulturalSpaceApiResponse response = fetchCulturalSpaceInfo(startIndex, endIndex);

        if (response == null || !response.isValid()) {
            log.warn("Invalid response received from Seoul API");
            return null;
        }

        if (!response.isSuccess()) {
            String errorCode = response.getCulturalSpaceInfo().getResult().getCode();
            String errorMessage = response.getCulturalSpaceInfo().getResult().getMessage();
            log.error("Seoul API returned error: {} - {}", errorCode, errorMessage);
            throw new RuntimeException("Seoul API error: " + errorCode + " - " + errorMessage);
        }

        if (!response.hasData()) {
            log.info("No cultural events data received from API");
            return null;
        }
        entityManager.createNativeQuery("TRUNCATE TABLE CULTURAL_SPACES").executeUpdate();

        List<SeoulCulturalSpaceApiResponse.CulturalSpaceData> data = response.getCulturalSpaceInfo().getRow();

        List<CulturalSpace> culturalSpaces = culturalSpaceMapper.mapToEntity(data);
        culturalSpaceRepository.saveAll(culturalSpaces);

        return response;

    }

    private  SeoulCulturalSpaceApiResponse fetchCulturalSpaceInfo(int startIndex, int endIndex) {
        String url = buildApiUrl(startIndex, endIndex, "", "", "");
        
        log.info("Calling Seoul Cultural Space API: {}", url);

        SeoulCulturalSpaceApiResponse response = restClientUtils.callGetApi(url, SeoulCulturalSpaceApiResponse.class);

        if (response != null && response.getRow() != null) {
            int totalCount = response.getListTotalCount();
            int rowCount = response.getRow().size();

            log.info("Successfully fetched {} cultural spaces (total: {})", rowCount, totalCount);

            // API 응답 상태 확인
            if (response.getResult() != null) {
                String resultCode = response.getResult().getCode();
                String resultMessage = response.getResult().getMessage();

                if (!"INFO-000".equals(resultCode)) {
                    log.warn("Seoul API returned non-success code: {} - {}", resultCode, resultMessage);
                }
            }
        } else {
            log.warn("Received null or empty response from Seoul Cultural Space API");
        }

        return response;
    }

    private String buildApiUrl(int startIndex, int endIndex, String num, String subjCode, String addr) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl)
                .append("/").append(serviceName)
                .append("/").append(startIndex)
                .append("/").append(endIndex);

        // 쿼리 파라미터 추가
        boolean hasParams = false;

        if (num != null && !num.trim().isEmpty()) {
            urlBuilder.append(hasParams ? "&" : "?").append("NUM=").append(num.trim());
            hasParams = true;
        }

        if (subjCode != null && !subjCode.trim().isEmpty()) {
            urlBuilder.append(hasParams ? "&" : "?").append("SUBJCODE=").append(subjCode.trim());
            hasParams = true;
        }

        if (addr != null && !addr.trim().isEmpty()) {
            urlBuilder.append(hasParams ? "&" : "?").append("ADDR=").append(addr.trim());
            hasParams = true;
        }

        return urlBuilder.toString();
    }

}
