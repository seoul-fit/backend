package com.seoulfit.backend.admin.adapter.in.web;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.res.SeoulCulturalSpaceApiResponse;
import com.seoulfit.backend.publicdata.culture.application.service.CulturalEventService;
import com.seoulfit.backend.publicdata.culture.application.service.CulturalReservationService;
import com.seoulfit.backend.publicdata.culture.application.service.CulturalSpaceService;
import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityBatchUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandCoolingShelterUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandPublicLibraryUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.in.SportsFacilityProgramBatchUseCase;
import com.seoulfit.backend.publicdata.park.application.port.in.ParkBatchUseCase;
import com.seoulfit.backend.publicdata.restaurant.application.port.in.RestaurantBatchUseCase;
import com.seoulfit.backend.search.application.port.in.SearchIndexBatchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
public class AdminBatchController {

    private static final DateTimeFormatter DATA_DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    private final ParkBatchUseCase parkBatchUseCase;
    private final AirQualityBatchUseCase airQualityBatchUseCase;
    private final CulturalEventService culturalEventService;
    private final CulturalSpaceService culturalSpaceService;
    private final CulturalReservationService culturalReservationService;
    private final CommandCoolingShelterUseCase coolingShelterUseCase;
    private final CommandPublicLibraryUseCase libraryUseCase;
    private final SportsFacilityProgramBatchUseCase sportsProgramBatchUseCase;
    private final RestaurantBatchUseCase restaurantBatchUseCase;
    private final SearchIndexBatchUseCase searchIndexBatchUseCase;

    @PostMapping("/{dataset}/run")
    public BatchRunResponse run(@PathVariable String dataset) {
        int processedCount = switch (dataset) {
            case "park" -> parkBatchUseCase.processDailyBatch();
            case "air-quality" -> runAirQuality();
            case "culture" -> runCulture();
            case "cooling-shelter" -> coolingShelterUseCase.saveCoolingShelter(
                    new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, "", "")).size();
            case "library" -> libraryUseCase.savePublicLibraryList();
            case "sports-program" -> runSportsProgram();
            case "restaurant" -> restaurantBatchUseCase.processDailyBatch();
            case "search-index" -> searchIndexBatchUseCase.syncAllPublicDataToIndex();
            default -> throw new IllegalArgumentException("지원하지 않는 데이터셋입니다: " + dataset);
        };

        return new BatchRunResponse(dataset, processedCount);
    }

    private int runAirQuality() {
        AirQualityBatchUseCase.AirQualityBatchResult result = airQualityBatchUseCase.processRealTimeBatch();
        if (!result.success()) {
            throw new IllegalStateException(result.errorMessage());
        }
        return result.totalSaved() + result.totalUpdated();
    }

    private int runCulture() {
        int eventCount = culturalEventService.saveCultureEvents();
        SeoulCulturalSpaceApiResponse spaceResponse = culturalSpaceService.saveCultureSpace(1, 1000);
        int spaceCount = spaceResponse == null || spaceResponse.getCulturalSpaceInfo() == null
                || spaceResponse.getCulturalSpaceInfo().getRow() == null
                ? 0 : spaceResponse.getCulturalSpaceInfo().getRow().size();
        int reservationCount = culturalReservationService.saveCulturalReservation(1, 1000);
        return eventCount + spaceCount + reservationCount;
    }

    private int runSportsProgram() {
        SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult result =
                sportsProgramBatchUseCase.processDailyBatch(LocalDate.now().format(DATA_DATE_FORMAT));
        if (!result.success()) {
            throw new IllegalStateException(result.errorMessage());
        }
        return result.totalSaved() + result.totalUpdated();
    }

    public record BatchRunResponse(String dataset, int processedCount) {
    }
}
