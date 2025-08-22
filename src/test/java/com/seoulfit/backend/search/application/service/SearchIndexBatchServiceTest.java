package com.seoulfit.backend.search.application.service;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import com.seoulfit.backend.search.application.port.out.PublicDataQueryPort;
import com.seoulfit.backend.search.application.port.out.SearchIndexRepository;
import com.seoulfit.backend.search.domain.PoiSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SearchIndexBatchService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchIndexBatchService 단위 테스트")
class SearchIndexBatchServiceTest {

    @Mock
    private SearchIndexRepository searchIndexRepository;
    
    @Mock
    private PublicDataQueryPort publicDataQueryPort;
    
    @InjectMocks
    private SearchIndexBatchService searchIndexBatchService;
    
    private List<CoolingCenter> mockCoolingCenters;
    private List<CulturalEvent> mockCulturalEvents;
    private List<CulturalReservation> mockCulturalReservations;
    private List<Library> mockLibraries;
    private List<Park> mockParks;
    private List<Restaurant> mockRestaurants;
    
    @BeforeEach
    void setUp() {
        // Mock data setup
        mockCoolingCenters = Arrays.asList(
            createCoolingCenter(1L, "중구 무더위쉼터"),
            createCoolingCenter(2L, "강남구 무더위쉼터")
        );
        
        mockCulturalEvents = Arrays.asList(
            createCulturalEvent(1L, "서울 음악 페스티벌"),
            createCulturalEvent(2L, "한강 불꽃축제")
        );
        
        mockCulturalReservations = Arrays.asList(
            createCulturalReservation(1L, "체육관 예약"),
            createCulturalReservation(2L, "공연장 예약")
        );
        
        mockLibraries = Arrays.asList(
            createLibrary(1L, "서울도서관"),
            createLibrary(2L, "남산도서관")
        );
        
        mockParks = Arrays.asList(
            createPark(1L, "남산공원"),
            createPark(2L, "한강공원")
        );
        
        mockRestaurants = Arrays.asList(
            createRestaurant(1L, "강남 맛집"),
            createRestaurant(2L, "종로 맛집")
        );
    }
    
    @Nested
    @DisplayName("전체 데이터 동기화 테스트")
    class SyncAllPublicDataToIndexTest {
        
        @Test
        @DisplayName("전체 데이터 동기화 - 성공")
        void syncAllPublicDataToIndex_Success() {
            // given
            when(publicDataQueryPort.findAllCoolingCenters()).thenReturn(mockCoolingCenters);
            when(publicDataQueryPort.findAllCulturalEvents()).thenReturn(mockCulturalEvents);
            when(publicDataQueryPort.findAllCulturalReservations()).thenReturn(mockCulturalReservations);
            when(publicDataQueryPort.findAllLibraries()).thenReturn(mockLibraries);
            when(publicDataQueryPort.findAllParks()).thenReturn(mockParks);
            when(publicDataQueryPort.findAllRestaurants()).thenReturn(mockRestaurants);
            
            when(searchIndexRepository.save(any(PoiSearchIndex.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            searchIndexBatchService.syncAllPublicDataToIndex();
            
            // then
            verify(searchIndexRepository).deleteAll();
            verify(publicDataQueryPort).findAllCoolingCenters();
            verify(publicDataQueryPort).findAllCulturalEvents();
            verify(publicDataQueryPort).findAllCulturalReservations();
            verify(publicDataQueryPort).findAllLibraries();
            verify(publicDataQueryPort).findAllParks();
            verify(publicDataQueryPort).findAllRestaurants();
            
            // 각 타입별로 2개씩, 총 12개 저장
            verify(searchIndexRepository, times(12)).save(any(PoiSearchIndex.class));
        }
        
        @Test
        @DisplayName("전체 데이터 동기화 - 빈 데이터")
        void syncAllPublicDataToIndex_EmptyData() {
            // given
            when(publicDataQueryPort.findAllCoolingCenters()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllCulturalEvents()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllCulturalReservations()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllLibraries()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllParks()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllRestaurants()).thenReturn(Collections.emptyList());
            
            // when
            searchIndexBatchService.syncAllPublicDataToIndex();
            
            // then
            verify(searchIndexRepository).deleteAll();
            verify(searchIndexRepository, never()).save(any(PoiSearchIndex.class));
        }
    }
    
    @Nested
    @DisplayName("무더위쉼터 동기화 테스트")
    class SyncCoolingCentersToIndexTest {
        
        @Test
        @DisplayName("무더위쉼터 동기화 - 성공")
        void syncCoolingCentersToIndex_Success() {
            // given
            when(publicDataQueryPort.findAllCoolingCenters()).thenReturn(mockCoolingCenters);
            when(searchIndexRepository.save(any(PoiSearchIndex.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            searchIndexBatchService.syncCoolingCentersToIndex();
            
            // then
            verify(publicDataQueryPort).findAllCoolingCenters();
            
            ArgumentCaptor<PoiSearchIndex> captor = ArgumentCaptor.forClass(PoiSearchIndex.class);
            verify(searchIndexRepository, times(2)).save(captor.capture());
            
            List<PoiSearchIndex> savedIndexes = captor.getAllValues();
            assertThat(savedIndexes).hasSize(2);
            assertThat(savedIndexes.get(0).getName()).isEqualTo("중구 무더위쉼터");
            assertThat(savedIndexes.get(0).getRefTable()).isEqualTo("cooling_centers");
            assertThat(savedIndexes.get(1).getName()).isEqualTo("강남구 무더위쉼터");
        }
        
        @Test
        @DisplayName("무더위쉼터 동기화 - 빈 데이터")
        void syncCoolingCentersToIndex_EmptyData() {
            // given
            when(publicDataQueryPort.findAllCoolingCenters()).thenReturn(Collections.emptyList());
            
            // when
            searchIndexBatchService.syncCoolingCentersToIndex();
            
            // then
            verify(publicDataQueryPort).findAllCoolingCenters();
            verify(searchIndexRepository, never()).save(any(PoiSearchIndex.class));
        }
    }
    
    @Nested
    @DisplayName("문화행사 동기화 테스트")
    class SyncCulturalEventsToIndexTest {
        
        @Test
        @DisplayName("문화행사 동기화 - 성공")
        void syncCulturalEventsToIndex_Success() {
            // given
            when(publicDataQueryPort.findAllCulturalEvents()).thenReturn(mockCulturalEvents);
            when(searchIndexRepository.save(any(PoiSearchIndex.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            searchIndexBatchService.syncCulturalEventsToIndex();
            
            // then
            verify(publicDataQueryPort).findAllCulturalEvents();
            verify(searchIndexRepository, times(2)).save(any(PoiSearchIndex.class));
        }
        
        @Test
        @DisplayName("문화행사 동기화 - 대용량 데이터")
        void syncCulturalEventsToIndex_LargeData() {
            // given
            List<CulturalEvent> largeList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                largeList.add(createCulturalEvent((long) i, "이벤트 " + i));
            }
            
            when(publicDataQueryPort.findAllCulturalEvents()).thenReturn(largeList);
            when(searchIndexRepository.save(any(PoiSearchIndex.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            searchIndexBatchService.syncCulturalEventsToIndex();
            
            // then
            verify(publicDataQueryPort).findAllCulturalEvents();
            verify(searchIndexRepository, times(1000)).save(any(PoiSearchIndex.class));
        }
    }
    
    @Nested
    @DisplayName("문화예약 동기화 테스트")
    class SyncCulturalReservationsToIndexTest {
        
        @Test
        @DisplayName("문화예약 동기화 - 성공")
        void syncCulturalReservationsToIndex_Success() {
            // given
            when(publicDataQueryPort.findAllCulturalReservations()).thenReturn(mockCulturalReservations);
            when(searchIndexRepository.save(any(PoiSearchIndex.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            searchIndexBatchService.syncCulturalReservationsToIndex();
            
            // then
            verify(publicDataQueryPort).findAllCulturalReservations();
            verify(searchIndexRepository, times(2)).save(any(PoiSearchIndex.class));
        }
    }
    
    @Nested
    @DisplayName("도서관 동기화 테스트")
    class SyncLibrariesToIndexTest {
        
        @Test
        @DisplayName("도서관 동기화 - 성공")
        void syncLibrariesToIndex_Success() {
            // given
            when(publicDataQueryPort.findAllLibraries()).thenReturn(mockLibraries);
            when(searchIndexRepository.save(any(PoiSearchIndex.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            searchIndexBatchService.syncLibrariesToIndex();
            
            // then
            verify(publicDataQueryPort).findAllLibraries();
            
            ArgumentCaptor<PoiSearchIndex> captor = ArgumentCaptor.forClass(PoiSearchIndex.class);
            verify(searchIndexRepository, times(2)).save(captor.capture());
            
            List<PoiSearchIndex> savedIndexes = captor.getAllValues();
            assertThat(savedIndexes.get(0).getName()).isEqualTo("서울도서관");
            assertThat(savedIndexes.get(0).getRefTable()).isEqualTo("libraries");
        }
    }
    
    @Nested
    @DisplayName("공원 동기화 테스트")
    class SyncParksToIndexTest {
        
        @Test
        @DisplayName("공원 동기화 - 성공")
        void syncParksToIndex_Success() {
            // given
            when(publicDataQueryPort.findAllParks()).thenReturn(mockParks);
            when(searchIndexRepository.save(any(PoiSearchIndex.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            searchIndexBatchService.syncParksToIndex();
            
            // then
            verify(publicDataQueryPort).findAllParks();
            verify(searchIndexRepository, times(2)).save(any(PoiSearchIndex.class));
        }
    }
    
    @Nested
    @DisplayName("맛집 동기화 테스트")
    class SyncRestaurantsToIndexTest {
        
        @Test
        @DisplayName("맛집 동기화 - 성공")
        void syncRestaurantsToIndex_Success() {
            // given
            when(publicDataQueryPort.findAllRestaurants()).thenReturn(mockRestaurants);
            when(searchIndexRepository.save(any(PoiSearchIndex.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            // when
            searchIndexBatchService.syncRestaurantsToIndex();
            
            // then
            verify(publicDataQueryPort).findAllRestaurants();
            
            ArgumentCaptor<PoiSearchIndex> captor = ArgumentCaptor.forClass(PoiSearchIndex.class);
            verify(searchIndexRepository, times(2)).save(captor.capture());
            
            List<PoiSearchIndex> savedIndexes = captor.getAllValues();
            assertThat(savedIndexes.get(0).getName()).isEqualTo("강남 맛집");
            assertThat(savedIndexes.get(0).getRefTable()).isEqualTo("restaurants");
        }
    }
    
    @Nested
    @DisplayName("인덱스 데이터 삭제 테스트")
    class ClearAllIndexDataTest {
        
        @Test
        @DisplayName("전체 인덱스 데이터 삭제 - 성공")
        void clearAllIndexData_Success() {
            // given
            doNothing().when(searchIndexRepository).deleteAll();
            
            // when
            searchIndexBatchService.clearAllIndexData();
            
            // then
            verify(searchIndexRepository).deleteAll();
        }
        
        @Test
        @DisplayName("전체 동기화 시 초기 삭제 확인")
        void syncAllPublicDataToIndex_ClearsDataFirst() {
            // given
            when(publicDataQueryPort.findAllCoolingCenters()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllCulturalEvents()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllCulturalReservations()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllLibraries()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllParks()).thenReturn(Collections.emptyList());
            when(publicDataQueryPort.findAllRestaurants()).thenReturn(Collections.emptyList());
            
            // when
            searchIndexBatchService.syncAllPublicDataToIndex();
            
            // then
            // deleteAll이 다른 작업보다 먼저 호출되는지 확인
            inOrder(searchIndexRepository, publicDataQueryPort);
            verify(searchIndexRepository).deleteAll();
            verify(publicDataQueryPort).findAllCoolingCenters();
        }
    }
    
    // Helper methods
    private CoolingCenter createCoolingCenter(Long id, String name) {
        CoolingCenter center = CoolingCenter.builder()
                .name(name)
                .roadAddress("서울시 주소")
                .lotAddress("서울시 주소")
                .latitude(37.5)
                .longitude(127.0)
                .facilityType1("공공시설")
                .facilityType2("쉼터")
                .build();
        setId(center, id);
        return center;
    }
    
    private CulturalEvent createCulturalEvent(Long id, String title) {
        CulturalEvent event = CulturalEvent.builder()
                .externalId("EVT" + id)
                .title(title)
                .place("서울 광장")
                .build();
        setId(event, id);
        return event;
    }
    
    private CulturalReservation createCulturalReservation(Long id, String name) {
        CulturalReservation reservation = CulturalReservation.builder()
                .svcId("SVC" + id)
                .svcNm(name)
                .placeNm("서울시설")
                .build();
        setId(reservation, id);
        return reservation;
    }
    
    private Library createLibrary(Long id, String name) {
        Library library = Library.builder()
                .lbrryName(name)
                .adres("서울시 주소")
                .xcnts(37.5)
                .ydnts(127.0)
                .build();
        setId(library, id);
        return library;
    }
    
    private Park createPark(Long id, String name) {
        Park park = Park.builder()
                .name(name)
                .address("서울시 주소")
                .latitude(37.5)
                .longitude(127.0)
                .build();
        setId(park, id);
        return park;
    }
    
    private Restaurant createRestaurant(Long id, String name) {
        Restaurant restaurant = Restaurant.builder()
                .name(name)
                .address("서울시 주소")
                .latitude(37.5)
                .longitude(127.0)
                .build();
        setId(restaurant, id);
        return restaurant;
    }
    
    private void setId(Object entity, Long id) {
        try {
            java.lang.reflect.Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            // Ignore in test
        }
    }
}