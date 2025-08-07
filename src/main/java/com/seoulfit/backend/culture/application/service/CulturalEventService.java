package com.seoulfit.backend.culture.application.service;

import com.seoulfit.backend.culture.infrastructure.mapper.CulturalEventMapper;
import com.seoulfit.backend.culture.adapter.in.web.dto.response.SeoulApiResponse;
import com.seoulfit.backend.culture.domain.CulturalEvent;
import com.seoulfit.backend.culture.adapter.out.CulturalEventRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

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

    public List<CulturalEvent> findNearbyEvents(BigDecimal latitude, BigDecimal longitude, 
                                              double radiusKm, List<String> categories) {
        validateCoordinates(latitude, longitude);
        
        LocalDate now = LocalDate.now();
        LocalDate futureDate = now.plusMonths(3); // 3개월 후까지
        
        return culturalEventRepository.findNearbyEvents(
                latitude, longitude, radiusKm, categories, now, futureDate);
    }

    public List<CulturalEvent> findOngoingEvents() {
        return culturalEventRepository.findOngoingEventsByCategories(
                getAllCategories(), LocalDate.now());
    }

    public List<CulturalEvent> findOngoingEventsByDistrict(String district) {
        validateDistrict(district);
        return culturalEventRepository.findOngoingEventsByDistrict(district, LocalDate.now());
    }

    public List<CulturalEvent> findFreeEvents() {
        return culturalEventRepository.findOngoingFreeEvents(LocalDate.now());
    }

    public List<CulturalEvent> findEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        return culturalEventRepository.findEventsByDateRange(startDate, endDate);
    }

    public List<CulturalEvent> searchEventsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 필수입니다.");
        }
        return culturalEventRepository.findByTitleContainingIgnoreCase(keyword.trim());
    }

    public CulturalEvent findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("유효하지 않은 문화행사 ID입니다.");
        }
        return culturalEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문화행사를 찾을 수 없습니다."));
    }

    public List<String> getAllCategories() {
        return culturalEventRepository.findAllCategories();
    }

    public List<String> getAllDistricts() {
        return culturalEventRepository.findAllDistricts();
    }

    /**
     * API에서 받은 데이터를 엔티티로 변환
     */
    private CulturalEvent convertToEntity(SeoulApiResponse.CulturalEventData eventData, String externalId) {
        try {
            return CulturalEvent.builder()
                    .codeName(sanitizeString(eventData.getCodeName()))
                    .district(sanitizeString(eventData.getGuName()))
                    .title(sanitizeString(eventData.getTitle()))
                    .eventDate(sanitizeString(eventData.getDate()))
                    .startDate(eventData.getStartDate().toLocalDate())
                    .endDate(eventData.getEndDate().toLocalDate())
                    .place(sanitizeString(eventData.getPlace()))
                    .orgName(sanitizeString(eventData.getOrgName()))
                    .useTarget(sanitizeString(eventData.getUseTarget()))
                    .useFee(sanitizeString(eventData.getUseFee()))
                    .player(sanitizeString(eventData.getPlayer()))
                    .program(sanitizeString(eventData.getProgram()))
                    .etcDesc(sanitizeString(eventData.getEtcDesc()))
                    .orgLink(sanitizeUrl(eventData.getOrgLink()))
                    .mainImg(sanitizeUrl(eventData.getMainImg()))
                    .registrationDate(sanitizeString(eventData.getRegistrationDate()))
                    .ticket(sanitizeString(eventData.getTicket()))
                    .themeCode(sanitizeString(eventData.getThemeCode()))
                    .latitude(parseCoordinate(eventData.getLatitude()))
                    .longitude(parseCoordinate(eventData.getLongitude()))
                    .isFree(sanitizeString(eventData.getIsFree()))
                    .homepageAddr(sanitizeUrl(eventData.getHomepageAddr()))
                    .externalId(externalId)
                    .build();
        } catch (Exception e) {
            log.error("Error converting event data to entity: {}", eventData.getTitle(), e);
            return null;
        }
    }

    /**
     * 외부 ID 생성 (중복 방지용)
     */
    private String generateExternalId(SeoulApiResponse.CulturalEventData eventData) {
        String title = Objects.toString(eventData.getTitle(), "").replaceAll("[^a-zA-Z0-9가-힣]", "");
        String startDate = Objects.toString(eventData.getStartDate(), "0");
        String place = Objects.toString(eventData.getPlace(), "").replaceAll("[^a-zA-Z0-9가-힣]", "");
        
        String combined = String.format("%s_%s_%s", title, startDate, place);
        return combined.length() > 100 ? combined.substring(0, 100) : combined;
    }

    /**
     * 타임스탬프를 LocalDate로 변환
     */
    private LocalDate convertTimestampToLocalDate(Long timestamp) {
        if (timestamp == null || timestamp == 0) {
            return null;
        }
        try {
            return Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (Exception e) {
            log.warn("Failed to convert timestamp to LocalDate: {}", timestamp);
            return null;
        }
    }

    /**
     * 좌표 파싱
     */
    private BigDecimal parseCoordinate(String coordinate) {
        if (coordinate == null || coordinate.trim().isEmpty()) {
            return null;
        }
        try {
            BigDecimal value = new BigDecimal(coordinate.trim());
            // 서울시 좌표 범위 검증
            if (coordinate.contains(".") && coordinate.indexOf('.') < 3) { // 위도
                if (value.compareTo(new BigDecimal("37.4")) < 0 || value.compareTo(new BigDecimal("37.7")) > 0) {
                    log.warn("Invalid latitude value: {}", coordinate);
                    return null;
                }
            } else { // 경도
                if (value.compareTo(new BigDecimal("126.7")) < 0 || value.compareTo(new BigDecimal("127.2")) > 0) {
                    log.warn("Invalid longitude value: {}", coordinate);
                    return null;
                }
            }
            return value;
        } catch (NumberFormatException e) {
            log.warn("Failed to parse coordinate: {}", coordinate);
            return null;
        }
    }

    /**
     * 문자열 정제
     */
    private String sanitizeString(String input) {
        if (input == null) return null;
        String sanitized = input.trim();
        return sanitized.isEmpty() ? null : sanitized;
    }

    /**
     * URL 정제
     */
    private String sanitizeUrl(String url) {
        if (url == null) return null;
        String sanitized = url.trim();
        if (sanitized.isEmpty()) return null;
        
        // 기본적인 URL 형식 검증
        if (!sanitized.startsWith("http://") && !sanitized.startsWith("https://")) {
            return null;
        }
        
        return sanitized;
    }

    /**
     * 좌표 유효성 검증
     */
    private void validateCoordinates(BigDecimal latitude, BigDecimal longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("위도와 경도는 필수입니다.");
        }
        
        if (latitude.compareTo(new BigDecimal("37.4")) < 0 || latitude.compareTo(new BigDecimal("37.7")) > 0) {
            throw new IllegalArgumentException("유효하지 않은 위도입니다.");
        }
        
        if (longitude.compareTo(new BigDecimal("126.7")) < 0 || longitude.compareTo(new BigDecimal("127.2")) > 0) {
            throw new IllegalArgumentException("유효하지 않은 경도입니다.");
        }
    }

    /**
     * 구 이름 유효성 검증
     */
    private void validateDistrict(String district) {
        if (district == null || district.trim().isEmpty()) {
            throw new IllegalArgumentException("구 이름은 필수입니다.");
        }
    }

    /**
     * 날짜 범위 유효성 검증
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일과 종료일은 필수입니다.");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }
    }
}
