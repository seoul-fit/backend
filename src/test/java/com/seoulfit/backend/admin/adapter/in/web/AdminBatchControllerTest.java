package com.seoulfit.backend.admin.adapter.in.web;

import com.seoulfit.backend.publicdata.culture.adapter.in.web.dto.res.SeoulCulturalSpaceApiResponse;
import com.seoulfit.backend.publicdata.culture.application.service.CulturalEventService;
import com.seoulfit.backend.publicdata.culture.application.service.CulturalReservationService;
import com.seoulfit.backend.publicdata.culture.application.service.CulturalSpaceService;
import com.seoulfit.backend.publicdata.env.application.port.in.AirQualityBatchUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandCoolingShelterUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandPublicLibraryUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.in.SportsFacilityProgramBatchUseCase;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.park.application.port.in.ParkBatchUseCase;
import com.seoulfit.backend.publicdata.restaurant.application.port.in.RestaurantBatchUseCase;
import com.seoulfit.backend.search.application.port.in.SearchIndexBatchUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBatchControllerTest {

    @Mock private ParkBatchUseCase parkBatchUseCase;
    @Mock private AirQualityBatchUseCase airQualityBatchUseCase;
    @Mock private CulturalEventService culturalEventService;
    @Mock private CulturalSpaceService culturalSpaceService;
    @Mock private CulturalReservationService culturalReservationService;
    @Mock private CommandCoolingShelterUseCase coolingShelterUseCase;
    @Mock private CommandPublicLibraryUseCase libraryUseCase;
    @Mock private SportsFacilityProgramBatchUseCase sportsProgramBatchUseCase;
    @Mock private RestaurantBatchUseCase restaurantBatchUseCase;
    @Mock private SearchIndexBatchUseCase searchIndexBatchUseCase;

    @InjectMocks private AdminBatchController controller;

    @Test
    void delegatesEverySupportedDatasetAndReturnsProcessedCount() {
        when(parkBatchUseCase.processDailyBatch()).thenReturn(133);
        when(airQualityBatchUseCase.processRealTimeBatch())
                .thenReturn(AirQualityBatchUseCase.AirQualityBatchResult.success(25, 20, 5, 0));
        when(culturalEventService.saveCultureEvents()).thenReturn(2000);

        SeoulCulturalSpaceApiResponse spaceResponse = mock(SeoulCulturalSpaceApiResponse.class);
        SeoulCulturalSpaceApiResponse.CulturalSpaceInfo spaceInfo =
                mock(SeoulCulturalSpaceApiResponse.CulturalSpaceInfo.class);
        when(spaceResponse.getCulturalSpaceInfo()).thenReturn(spaceInfo);
        when(spaceInfo.getRow()).thenReturn(List.of(
                mock(SeoulCulturalSpaceApiResponse.CulturalSpaceData.class),
                mock(SeoulCulturalSpaceApiResponse.CulturalSpaceData.class)));
        when(culturalSpaceService.saveCultureSpace(1, 1000)).thenReturn(spaceResponse);
        when(culturalReservationService.saveCulturalReservation(1, 1000)).thenReturn(500);

        when(coolingShelterUseCase.saveCoolingShelter(
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, "", "")))
                .thenReturn(List.of(mock(CoolingCenter.class), mock(CoolingCenter.class)));
        when(libraryUseCase.savePublicLibraryList()).thenReturn(190);
        when(sportsProgramBatchUseCase.processDailyBatch(anyString()))
                .thenReturn(SportsFacilityProgramBatchUseCase.SportsFacilityProgramBatchResult
                        .success("20260720", 80, 70, 10, 0));
        when(restaurantBatchUseCase.processDailyBatch()).thenReturn(700);
        when(searchIndexBatchUseCase.syncAllPublicDataToIndex()).thenReturn(3517);

        assertCount("park", 133);
        assertCount("air-quality", 25);
        assertCount("culture", 2502);
        assertCount("cooling-shelter", 2);
        assertCount("library", 190);
        assertCount("sports-program", 80);
        assertCount("restaurant", 700);
        assertCount("search-index", 3517);

        verify(searchIndexBatchUseCase).syncAllPublicDataToIndex();
    }

    @Test
    void rejectsUnsupportedDataset() {
        assertThatThrownBy(() -> controller.run("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 데이터셋");
    }

    @Test
    void exposesUseCaseFailureAsServerError() {
        when(airQualityBatchUseCase.processRealTimeBatch())
                .thenReturn(AirQualityBatchUseCase.AirQualityBatchResult.failure("upstream failed"));

        assertThatThrownBy(() -> controller.run("air-quality"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("upstream failed");
    }

    private void assertCount(String dataset, int expectedCount) {
        AdminBatchController.BatchRunResponse response = controller.run(dataset);
        assertThat(response.dataset()).isEqualTo(dataset);
        assertThat(response.processedCount()).isEqualTo(expectedCount);
    }
}
