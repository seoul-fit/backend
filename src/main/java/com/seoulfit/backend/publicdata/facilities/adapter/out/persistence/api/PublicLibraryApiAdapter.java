package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.api;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.api.dto.SeoulLibraryInfoDto;
import com.seoulfit.backend.publicdata.facilities.application.port.out.LoadPublicLibraryPort;
import com.seoulfit.backend.publicdata.facilities.domain.PublicLibrary;
import com.seoulfit.backend.publicdata.facilities.infrastructure.mapper.PublicLibraryMapper;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublicLibraryApiAdapter implements LoadPublicLibraryPort {
    @Value("${seoul-api.base-url}")
    private String baseUrl;

    @Value("${seoul-api.v1.facilities.service-name[0]}")
    private String serviceName;

    private final RestClientUtils<SeoulLibraryInfoDto> restClientUtils;

    @Override
    public List<PublicLibrary> loadPublicLibrary(int startIndex, int endIndex) {
        String url = String.format("%s/%s/%d/%d/", baseUrl, serviceName, startIndex, endIndex);

        try {
            SeoulLibraryInfoDto response = restClientUtils.callGetApi(url, SeoulLibraryInfoDto.class);

            List<PublicLibrary> publicLibraryList = PublicLibraryMapper.convertToEntity(response.getSeoulPublicLibraryInfo());

            log.info("Successfully loaded {} amenities from API", publicLibraryList.size());
            return publicLibraryList;

        } catch (Exception e) {
            log.error("Error loading amenities from API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load amenities data: " + e.getMessage(), e);
        }
    }
}
