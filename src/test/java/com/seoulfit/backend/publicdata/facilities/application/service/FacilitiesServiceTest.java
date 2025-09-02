package com.seoulfit.backend.publicdata.facilities.application.service;

import com.seoulfit.backend.publicdata.facilities.application.port.in.CommandCoolingShelterUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.out.CommandCoolingShelterPort;
import com.seoulfit.backend.publicdata.facilities.application.port.out.LoadCoolingShelterPort;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FacilitiesService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FacilitiesService 단위 테스트")
class FacilitiesServiceTest {

    @Mock
    private LoadCoolingShelterPort loadCoolingShelterPort;
    
    @Mock
    private CommandCoolingShelterPort commandCoolingShelterPort;
    
    @InjectMocks
    private FacilitiesService facilitiesService;
    
    private List<CoolingCenter> mockCoolingCenters;
    private CoolingCenter coolingCenter1;
    private CoolingCenter coolingCenter2;
    private CoolingCenter coolingCenter3;
    
    @BeforeEach
    void setUp() {
        coolingCenter1 = createCoolingCenter(1L, "중구 무더위쉼터 1", "서울특별시 중구", 37.5665, 126.9780);
        coolingCenter2 = createCoolingCenter(2L, "강남구 무더위쉼터 1", "서울특별시 강남구", 37.5172, 126.9631);
        coolingCenter3 = createCoolingCenter(3L, "서초구 무더위쉼터 1", "서울특별시 서초구", 37.4837, 127.0324);
        
        mockCoolingCenters = Arrays.asList(coolingCenter1, coolingCenter2, coolingCenter3);
    }
    
    @Nested
    @DisplayName("무더위쉼터 저장 테스트")
    class SaveCoolingShelterTest {
        
        @Test
        @DisplayName("무더위쉼터 저장 - 성공")
        void saveCoolingShelter_Success() {
            // given
            CommandCoolingShelterUseCase.GetAmenitiesQuery query = 
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, null, null);
            
            when(loadCoolingShelterPort.loadAmenities(1, 1000)).thenReturn(mockCoolingCenters);
            when(loadCoolingShelterPort.loadAmenities(1001, 2000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(2001, 3000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(3001, 4000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(4001, 5000)).thenReturn(Collections.emptyList());
            
            doNothing().when(commandCoolingShelterPort).truncate();
            doNothing().when(commandCoolingShelterPort).save(anyList());
            
            // when
            List<CoolingCenter> result = facilitiesService.saveCoolingShelter(query);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly(coolingCenter1, coolingCenter2, coolingCenter3);
            
            verify(commandCoolingShelterPort).truncate();
            verify(loadCoolingShelterPort).loadAmenities(1, 1000);
            verify(loadCoolingShelterPort).loadAmenities(1001, 2000);
            verify(loadCoolingShelterPort).loadAmenities(2001, 3000);
            verify(loadCoolingShelterPort).loadAmenities(3001, 4000);
            verify(loadCoolingShelterPort).loadAmenities(4001, 5000);
            verify(commandCoolingShelterPort, times(5)).save(anyList());
        }
        
        @Test
        @DisplayName("무더위쉼터 저장 - 다중 페이지 데이터")
        void saveCoolingShelter_MultiplePages() {
            // given
            CommandCoolingShelterUseCase.GetAmenitiesQuery query = 
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, null, null);
            
            List<CoolingCenter> page1 = Arrays.asList(coolingCenter1);
            List<CoolingCenter> page2 = Arrays.asList(coolingCenter2);
            List<CoolingCenter> page3 = Arrays.asList(coolingCenter3);
            
            when(loadCoolingShelterPort.loadAmenities(1, 1000)).thenReturn(page1);
            when(loadCoolingShelterPort.loadAmenities(1001, 2000)).thenReturn(page2);
            when(loadCoolingShelterPort.loadAmenities(2001, 3000)).thenReturn(page3);
            when(loadCoolingShelterPort.loadAmenities(3001, 4000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(4001, 5000)).thenReturn(Collections.emptyList());
            
            doNothing().when(commandCoolingShelterPort).truncate();
            doNothing().when(commandCoolingShelterPort).save(anyList());
            
            // when
            List<CoolingCenter> result = facilitiesService.saveCoolingShelter(query);
            
            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(coolingCenter1);
            
            verify(commandCoolingShelterPort).truncate();
            verify(commandCoolingShelterPort).save(page1);
            verify(commandCoolingShelterPort).save(page2);
            verify(commandCoolingShelterPort).save(page3);
        }
        
        @Test
        @DisplayName("무더위쉼터 저장 - 빈 데이터")
        void saveCoolingShelter_EmptyData() {
            // given
            CommandCoolingShelterUseCase.GetAmenitiesQuery query = 
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, null, null);
            
            when(loadCoolingShelterPort.loadAmenities(anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
            
            doNothing().when(commandCoolingShelterPort).truncate();
            doNothing().when(commandCoolingShelterPort).save(anyList());
            
            // when
            List<CoolingCenter> result = facilitiesService.saveCoolingShelter(query);
            
            // then
            assertThat(result).isEmpty();
            
            verify(commandCoolingShelterPort).truncate();
            verify(loadCoolingShelterPort, times(5)).loadAmenities(anyInt(), anyInt());
            verify(commandCoolingShelterPort, times(5)).save(Collections.emptyList());
        }
        
        @Test
        @DisplayName("무더위쉼터 저장 - truncate 실패")
        void saveCoolingShelter_TruncateFailure() {
            // given
            CommandCoolingShelterUseCase.GetAmenitiesQuery query = 
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, null, null);
            
            doThrow(new RuntimeException("DB truncate 실패"))
                .when(commandCoolingShelterPort).truncate();
            
            // when & then
            assertThatThrownBy(() -> facilitiesService.saveCoolingShelter(query))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch Cooling-Shelter data")
                .hasMessageContaining("DB truncate 실패");
            
            verify(commandCoolingShelterPort).truncate();
            verify(loadCoolingShelterPort, never()).loadAmenities(anyInt(), anyInt());
            verify(commandCoolingShelterPort, never()).save(anyList());
        }
        
        @Test
        @DisplayName("무더위쉼터 저장 - API 호출 실패")
        void saveCoolingShelter_ApiFailure() {
            // given
            CommandCoolingShelterUseCase.GetAmenitiesQuery query = 
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, null, null);
            
            doNothing().when(commandCoolingShelterPort).truncate();
            when(loadCoolingShelterPort.loadAmenities(1, 1000))
                .thenThrow(new RuntimeException("API 호출 실패"));
            
            // when & then
            assertThatThrownBy(() -> facilitiesService.saveCoolingShelter(query))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch Cooling-Shelter data")
                .hasMessageContaining("API 호출 실패");
            
            verify(commandCoolingShelterPort).truncate();
            verify(loadCoolingShelterPort).loadAmenities(1, 1000);
            verify(commandCoolingShelterPort, never()).save(anyList());
        }
        
        @Test
        @DisplayName("무더위쉼터 저장 - 저장 실패")
        void saveCoolingShelter_SaveFailure() {
            // given
            CommandCoolingShelterUseCase.GetAmenitiesQuery query = 
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, null, null);
            
            when(loadCoolingShelterPort.loadAmenities(1, 1000)).thenReturn(mockCoolingCenters);
            when(loadCoolingShelterPort.loadAmenities(1001, 2000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(2001, 3000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(3001, 4000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(4001, 5000)).thenReturn(Collections.emptyList());
            
            doNothing().when(commandCoolingShelterPort).truncate();
            doThrow(new RuntimeException("DB 저장 실패"))
                .when(commandCoolingShelterPort).save(mockCoolingCenters);
            
            // when & then
            assertThatThrownBy(() -> facilitiesService.saveCoolingShelter(query))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch Cooling-Shelter data")
                .hasMessageContaining("DB 저장 실패");
            
            verify(commandCoolingShelterPort).truncate();
            verify(commandCoolingShelterPort).save(mockCoolingCenters);
        }
        
        @Test
        @DisplayName("무더위쉼터 저장 - 부분 실패 처리")
        void saveCoolingShelter_PartialFailure() {
            // given
            CommandCoolingShelterUseCase.GetAmenitiesQuery query = 
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, null, null);
            
            when(loadCoolingShelterPort.loadAmenities(1, 1000)).thenReturn(mockCoolingCenters);
            when(loadCoolingShelterPort.loadAmenities(1001, 2000))
                .thenThrow(new RuntimeException("두 번째 페이지 로드 실패"));
            
            doNothing().when(commandCoolingShelterPort).truncate();
            doNothing().when(commandCoolingShelterPort).save(anyList());
            
            // when & then
            assertThatThrownBy(() -> facilitiesService.saveCoolingShelter(query))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch Cooling-Shelter data")
                .hasMessageContaining("두 번째 페이지 로드 실패");
            
            verify(commandCoolingShelterPort).truncate();
            verify(loadCoolingShelterPort).loadAmenities(1, 1000);
            verify(loadCoolingShelterPort).loadAmenities(1001, 2000);
            // 예외 발생으로 save는 호출되지 않음
        }
        
        @Test
        @DisplayName("무더위쉼터 저장 - 대용량 데이터")
        void saveCoolingShelter_LargeData() {
            // given
            CommandCoolingShelterUseCase.GetAmenitiesQuery query = 
                new CommandCoolingShelterUseCase.GetAmenitiesQuery(1, 1000, null, null);
            
            List<CoolingCenter> largeList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                largeList.add(createCoolingCenter((long) i, "쉼터 " + i, "서울시", 37.5 + (i * 0.001), 127.0 + (i * 0.001)));
            }
            
            when(loadCoolingShelterPort.loadAmenities(1, 1000)).thenReturn(largeList);
            when(loadCoolingShelterPort.loadAmenities(1001, 2000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(2001, 3000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(3001, 4000)).thenReturn(Collections.emptyList());
            when(loadCoolingShelterPort.loadAmenities(4001, 5000)).thenReturn(Collections.emptyList());
            
            doNothing().when(commandCoolingShelterPort).truncate();
            doNothing().when(commandCoolingShelterPort).save(anyList());
            
            // when
            List<CoolingCenter> result = facilitiesService.saveCoolingShelter(query);
            
            // then
            assertThat(result).hasSize(1000);
            // 5번의 save 호출 검증
            verify(commandCoolingShelterPort, times(5)).save(anyList());
        }
    }
    
    @Nested
    @DisplayName("주변 편의시설 조회 테스트")
    class GetAmenitiesNearbyTest {
        
        @Test
        @DisplayName("주변 편의시설 조회 - 현재 구현되지 않음")
        void getAmenitiesNearby_NotImplemented() {
            // given
            BigDecimal latitude = new BigDecimal("37.5665");
            BigDecimal longitude = new BigDecimal("126.9780");
            double radiusKm = 2.0;
            
            // when
            List<CoolingCenter> result = facilitiesService.getAmenitiesNearby(latitude, longitude, radiusKm);
            
            // then
            assertThat(result).isNull();
            verifyNoInteractions(loadCoolingShelterPort, commandCoolingShelterPort);
        }
        
        @Test
        @DisplayName("주변 편의시설 조회 - null 좌표 처리")
        void getAmenitiesNearby_NullCoordinates() {
            // given
            BigDecimal latitude = null;
            BigDecimal longitude = null;
            double radiusKm = 2.0;
            
            // when
            List<CoolingCenter> result = facilitiesService.getAmenitiesNearby(latitude, longitude, radiusKm);
            
            // then
            assertThat(result).isNull();
            verifyNoInteractions(loadCoolingShelterPort, commandCoolingShelterPort);
        }
        
        @Test
        @DisplayName("주변 편의시설 조회 - 음수 반경")
        void getAmenitiesNearby_NegativeRadius() {
            // given
            BigDecimal latitude = new BigDecimal("37.5665");
            BigDecimal longitude = new BigDecimal("126.9780");
            double radiusKm = -1.0;
            
            // when
            List<CoolingCenter> result = facilitiesService.getAmenitiesNearby(latitude, longitude, radiusKm);
            
            // then
            assertThat(result).isNull();
            verifyNoInteractions(loadCoolingShelterPort, commandCoolingShelterPort);
        }
    }
    
    // Helper methods
    private CoolingCenter createCoolingCenter(Long id, String name, String address, 
                                             double latitude, double longitude) {
        CoolingCenter center = CoolingCenter.builder()
                .name(name)
                .roadAddress(address)
                .lotAddress(address)
                .facilityType1("공공시설")
                .facilityType2("쉼터")
                .capacity(50)
                .areaSize("100㎡")
                .areaCode("11010")
                .build();
        
        // Set ID using reflection
        try {
            java.lang.reflect.Field idField = CoolingCenter.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(center, id);
        } catch (Exception e) {
            // Ignore in test
        }
        
        return center;
    }
}