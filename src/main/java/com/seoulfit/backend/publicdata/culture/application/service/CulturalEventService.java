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

/**
 * 문화행사 서비스
 * <p>
 * 서울시 문화행사 데이터를 동기화하고 관리하는 서비스입니다.
 * 서울시 Open API로부터 데이터를 가져와 데이터베이스에 저장합니다.
 * </p>
 * 
 * @author Seoul Fit
 * @since 1.0.0
 * @see SeoulCulturalApiService
 * @see CulturalEventRepository
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CulturalEventService {
    private final EntityManager entityManager;
    private final CulturalEventRepository culturalEventRepository;
    private final CulturalEventMapper culturalEventMapper;

    private final SeoulCulturalApiService seoulCulturalApiService;

    /**
     * 서울시 API로부터 문화행사 데이터를 가져와 저장합니다.
     * <p>
     * 기존 데이터를 모두 삭제하고 새로운 데이터로 대체합니다.
     * API로부터 최대 4000개의 데이터를 가져옵니다.
     * </p>
     * 
     * @return 저장된 문화행사 개수
     * @throws RuntimeException API 호출 실패 또는 데이터 처리 오류 시
     */
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
            SeoulApiResponse response = seoulCulturalApiService.fetchAllCulturalEvents(1,1000);
            SeoulApiResponse response2 = seoulCulturalApiService.fetchAllCulturalEvents(1001,2000);
            SeoulApiResponse response3 = seoulCulturalApiService.fetchAllCulturalEvents(2001,3000);
            SeoulApiResponse response4 = seoulCulturalApiService.fetchAllCulturalEvents(3001,4000);

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
            List<SeoulApiResponse.CulturalEventData> data2 = response2.getCulturalEventInfo().getRow();
            List<SeoulApiResponse.CulturalEventData> data3 = response3.getCulturalEventInfo().getRow();
            List<SeoulApiResponse.CulturalEventData> data4 = response4.getCulturalEventInfo().getRow();

            List<CulturalEvent> culturalEvents = culturalEventMapper.mapToEntity(data);
            List<CulturalEvent> culturalEvents2 = culturalEventMapper.mapToEntity(data2);
            List<CulturalEvent> culturalEvents3 = culturalEventMapper.mapToEntity(data3);
            List<CulturalEvent> culturalEvents4 = culturalEventMapper.mapToEntity(data4);

            culturalEventRepository.saveAll(culturalEvents);
            culturalEventRepository.saveAll(culturalEvents2);
            culturalEventRepository.saveAll(culturalEvents3);
            culturalEventRepository.saveAll(culturalEvents4);

            return response.getCulturalEventInfo().getRow().size();

        } catch (Exception e) {
            log.error("Error during cultural events synchronization", e);
            throw new RuntimeException("Failed to sync cultural events: " + e.getMessage(), e);
        }
    }

}
