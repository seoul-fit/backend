package com.seoulfit.backend.publicdata.culture.application.service;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.res.SeoulApiResponse;
import com.seoulfit.backend.publicdata.culture.adapter.out.custom.CulturalEventRepository;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.culture.infrastructure.mapper.CulturalEventMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CulturalEventService {
    private final EntityManager entityManager;
    private final CulturalEventRepository culturalEventRepository;
    private final CulturalEventMapper culturalEventMapper;

    private final SeoulCulturalApiService seoulCulturalApiService;

    @Transactional
    public int saveCultureEvents() {
        try {
            log.info("Starting cultural events synchronization from Seoul API");

            // API 상태 확인
            if (!seoulCulturalApiService.isApiHealthy())
                log.warn("Seoul API health check failed, but proceeding with sync attempt");

            // truncate
            entityManager.createNativeQuery("TRUNCATE TABLE cultural_events").executeUpdate();

            // call api
            SeoulApiResponse response = seoulCulturalApiService.fetchAllCulturalEvents();

            if (response == null || !response.isValid()) {
                log.warn("Invalid response received from Seoul API");
                return 0;
            }

            if (!response.isSuccess()) {
                String errorCode = response.getCulturalEventInfo().getResult().getCode();
                String errorMessage = response.getCulturalEventInfo().getResult().getMessage();
                log.error("Seoul API returned error: {} - {}", errorCode, errorMessage);
                throw new RuntimeException("Seoul API error: " + errorCode + " - " + errorMessage);
            }

            if (!response.hasData()) {
                log.info("No cultural events data received from API");
                return 0;
            }

            List<SeoulApiResponse.CulturalEventData> data = response.getCulturalEventInfo().getRow();

            List<CulturalEvent> culturalEvents = culturalEventMapper.mapToEntity(data);
            culturalEventRepository.saveAll(culturalEvents);

            return response.getCulturalEventInfo().getRow().size();

        } catch (Exception e) {
            log.error("Error during cultural events synchronization", e);
            throw new RuntimeException("Failed to sync cultural events: " + e.getMessage(), e);
        }
    }

    public SeoulApiResponse getSeoulCulturalEventList(int startIndex, int endIndex) {
        try {
            // API 상태 확인
            if (!seoulCulturalApiService.isApiHealthy())
                log.warn("Seoul API health check failed, but proceeding with sync attempt");

            // call api
            SeoulApiResponse response = seoulCulturalApiService.fetchAllCulturalEvents();

            if (response == null || !response.isValid()) {
                log.warn("Invalid response received from Seoul API");
                return response;
            }

            if (!response.isSuccess()) {
                String errorCode = response.getCulturalEventInfo().getResult().getCode();
                String errorMessage = response.getCulturalEventInfo().getResult().getMessage();
                log.error("Seoul API returned error: {} - {}", errorCode, errorMessage);
                throw new RuntimeException("Seoul API error: " + errorCode + " - " + errorMessage);
            }

            return response;

        } catch (Exception e) {
            log.error("Error during cultural events synchronization", e);
            throw new RuntimeException("Failed to sync cultural events: " + e.getMessage(), e);
        }
    }

}
