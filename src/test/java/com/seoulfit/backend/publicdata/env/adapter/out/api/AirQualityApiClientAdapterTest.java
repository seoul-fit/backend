package com.seoulfit.backend.publicdata.env.adapter.out.api;

import com.seoulfit.backend.publicdata.env.adapter.out.api.dto.AirQualityApiResponse;
import com.seoulfit.backend.shared.utils.RestClientUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirQualityApiClientAdapterTest {

    @Mock
    private RestClientUtils<AirQualityApiResponse> restClientUtils;

    @Test
    void appendsServiceAndRangeToPreAuthenticatedBaseUrlWithoutDuplicatingApiKey() {
        AirQualityApiClientAdapter adapter = new AirQualityApiClientAdapter(restClientUtils);
        ReflectionTestUtils.setField(adapter, "baseUrl", "http://api.test/key/json");
        ReflectionTestUtils.setField(adapter, "serviceName", "RealtimeCityAir");

        AirQualityApiResponse response = mock(AirQualityApiResponse.class);
        AirQualityApiResponse.RealtimeCityAir realtime = mock(AirQualityApiResponse.RealtimeCityAir.class);
        AirQualityApiResponse.Result result = mock(AirQualityApiResponse.Result.class);
        when(response.getRealtimeCityAir()).thenReturn(realtime);
        when(realtime.getResult()).thenReturn(result);
        when(result.getCode()).thenReturn("INFO-000");
        when(realtime.getRow()).thenReturn(List.of());
        when(restClientUtils.callGetApi(
                "http://api.test/key/json/RealtimeCityAir/1/1000/", AirQualityApiResponse.class))
                .thenReturn(response);

        assertThat(adapter.fetchRealTimeAirQuality()).isSameAs(response);
        verify(restClientUtils).callGetApi(
                "http://api.test/key/json/RealtimeCityAir/1/1000/", AirQualityApiResponse.class);
    }
}
