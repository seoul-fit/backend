package com.seoulfit.backend.search.application.service;

import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import com.seoulfit.backend.search.adapter.in.web.dto.SearchResultResponse;
import com.seoulfit.backend.search.application.port.out.PublicDataRepository;
import com.seoulfit.backend.search.application.port.out.SearchIndexRepository;
import com.seoulfit.backend.search.domain.PoiSearchIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SearchService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchService 단위 테스트")
class SearchServiceTest {

    @Mock
    private SearchIndexRepository searchIndexRepository;
    
    @Mock
    private PublicDataRepository publicDataRepository;
    
    @InjectMocks
    private SearchService searchService;
    
    private List<PoiSearchIndex> mockIndexList;
    private PoiSearchIndex index1;
    private PoiSearchIndex index2;
    private PoiSearchIndex index3;
    
    @BeforeEach
    void setUp() {
        index1 = createPoiSearchIndex(1L, "서울도서관", "libraries", 100L, "서울특별시 중구", 37.5665, 126.9780);
        index2 = createPoiSearchIndex(2L, "남산공원", "park", 200L, "서울특별시 용산구", 37.5537, 126.9810);
        index3 = createPoiSearchIndex(3L, "강남 맛집", "restaurants", 300L, "서울특별시 강남구", 37.5172, 127.0473);
        
        mockIndexList = Arrays.asList(index1, index2, index3);
    }
    
    @Nested
    @DisplayName("검색 인덱스 조회 테스트")
    class SearchIndexTest {
        
        @Test
        @DisplayName("키워드 검색 - 성공")
        void searchIndex_WithKeyword_Success() {
            // given
            String keyword = "도서관";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            
            List<PoiSearchIndex> filteredList = Arrays.asList(index1);
            Page<PoiSearchIndex> searchPage = new PageImpl<>(filteredList, pageable, 1);
            
            when(searchIndexRepository.searchByKeyword(keyword, pageable)).thenReturn(searchPage);
            
            // when
            SearchResultResponse result = searchService.searchIndex(keyword, page, size);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("서울도서관");
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.isHasNext()).isFalse();
            
            verify(searchIndexRepository).searchByKeyword(keyword, pageable);
            verify(searchIndexRepository, never()).findAll(any(Pageable.class));
        }
        
        @Test
        @DisplayName("전체 조회 - 키워드 없음")
        void searchIndex_WithoutKeyword_ReturnsAll() {
            // given
            String keyword = null;
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            
            Page<PoiSearchIndex> allPage = new PageImpl<>(mockIndexList, pageable, 3);
            
            when(searchIndexRepository.findAll(pageable)).thenReturn(allPage);
            
            // when
            SearchResultResponse result = searchService.searchIndex(keyword, page, size);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(3);
            
            verify(searchIndexRepository).findAll(pageable);
            verify(searchIndexRepository, never()).searchByKeyword(any(), any());
        }
        
        @Test
        @DisplayName("전체 조회 - 빈 키워드")
        void searchIndex_EmptyKeyword_ReturnsAll() {
            // given
            String keyword = "   ";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            
            Page<PoiSearchIndex> allPage = new PageImpl<>(mockIndexList, pageable, 3);
            
            when(searchIndexRepository.findAll(pageable)).thenReturn(allPage);
            
            // when
            SearchResultResponse result = searchService.searchIndex(keyword, page, size);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(3);
            
            verify(searchIndexRepository).findAll(pageable);
            verify(searchIndexRepository, never()).searchByKeyword(any(), any());
        }
        
        @Test
        @DisplayName("페이지네이션 - 두 번째 페이지")
        void searchIndex_Pagination_SecondPage() {
            // given
            String keyword = "서울";
            int page = 1;
            int size = 2;
            Pageable pageable = PageRequest.of(page, size);
            
            List<PoiSearchIndex> secondPageList = Arrays.asList(index3);
            Page<PoiSearchIndex> searchPage = new PageImpl<>(secondPageList, pageable, 3);
            
            when(searchIndexRepository.searchByKeyword(keyword, pageable)).thenReturn(searchPage);
            
            // when
            SearchResultResponse result = searchService.searchIndex(keyword, page, size);
            
            // then
            assertThat(result.getPage()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(2);
            assertThat(result.isHasPrevious()).isTrue();
            assertThat(result.isHasNext()).isFalse();
            
            verify(searchIndexRepository).searchByKeyword(keyword, pageable);
        }
        
        @Test
        @DisplayName("검색 결과 없음")
        void searchIndex_NoResults() {
            // given
            String keyword = "없는키워드";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            
            Page<PoiSearchIndex> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            
            when(searchIndexRepository.searchByKeyword(keyword, pageable)).thenReturn(emptyPage);
            
            // when
            SearchResultResponse result = searchService.searchIndex(keyword, page, size);
            
            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(0);
            assertThat(result.isHasNext()).isFalse();
            assertThat(result.isHasPrevious()).isFalse();
            
            verify(searchIndexRepository).searchByKeyword(keyword, pageable);
        }
        
        @Test
        @DisplayName("키워드 트리밍 처리")
        void searchIndex_KeywordTrimming() {
            // given
            String keyword = "  도서관  ";
            int page = 0;
            int size = 10;
            Pageable pageable = PageRequest.of(page, size);
            
            List<PoiSearchIndex> filteredList = Arrays.asList(index1);
            Page<PoiSearchIndex> searchPage = new PageImpl<>(filteredList, pageable, 1);
            
            when(searchIndexRepository.searchByKeyword("도서관", pageable)).thenReturn(searchPage);
            
            // when
            SearchResultResponse result = searchService.searchIndex(keyword, page, size);
            
            // then
            assertThat(result.getContent()).hasSize(1);
            verify(searchIndexRepository).searchByKeyword("도서관", pageable);
        }
    }
    
    @Nested
    @DisplayName("인덱스별 공공데이터 조회 테스트")
    class GetPublicDataByIndexTest {
        
        @Test
        @DisplayName("무더위쉼터 데이터 조회")
        void getPublicDataByIndex_CoolingCenter() {
            // given
            Long indexId = 1L;
            PoiSearchIndex coolingIndex = createPoiSearchIndex(indexId, "무더위쉼터", "cooling_centers", 100L, "서울시", 37.5, 127.0);
            CoolingCenter coolingCenter = mock(CoolingCenter.class);
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.of(coolingIndex));
            when(publicDataRepository.findCoolingCenterById(100L)).thenReturn(Optional.of(coolingCenter));
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(CoolingCenter.class);
            
            verify(searchIndexRepository).findById(indexId);
            verify(publicDataRepository).findCoolingCenterById(100L);
        }
        
        @Test
        @DisplayName("문화행사 데이터 조회")
        void getPublicDataByIndex_CulturalEvent() {
            // given
            Long indexId = 2L;
            PoiSearchIndex eventIndex = createPoiSearchIndex(indexId, "문화행사", "cultural_events", 200L, "서울시", 37.5, 127.0);
            CulturalEvent culturalEvent = mock(CulturalEvent.class);
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.of(eventIndex));
            when(publicDataRepository.findCulturalEventById(200L)).thenReturn(Optional.of(culturalEvent));
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(CulturalEvent.class);
            
            verify(publicDataRepository).findCulturalEventById(200L);
        }
        
        @Test
        @DisplayName("도서관 데이터 조회")
        void getPublicDataByIndex_Library() {
            // given
            Long indexId = 3L;
            PoiSearchIndex libraryIndex = createPoiSearchIndex(indexId, "도서관", "libraries", 300L, "서울시", 37.5, 127.0);
            Library library = mock(Library.class);
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.of(libraryIndex));
            when(publicDataRepository.findLibraryById(300L)).thenReturn(Optional.of(library));
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(Library.class);
            
            verify(publicDataRepository).findLibraryById(300L);
        }
        
        @Test
        @DisplayName("공원 데이터 조회")
        void getPublicDataByIndex_Park() {
            // given
            Long indexId = 4L;
            PoiSearchIndex parkIndex = createPoiSearchIndex(indexId, "공원", "park", 400L, "서울시", 37.5, 127.0);
            Park park = mock(Park.class);
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.of(parkIndex));
            when(publicDataRepository.findParkById(400L)).thenReturn(Optional.of(park));
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(Park.class);
            
            verify(publicDataRepository).findParkById(400L);
        }
        
        @Test
        @DisplayName("맛집 데이터 조회")
        void getPublicDataByIndex_Restaurant() {
            // given
            Long indexId = 5L;
            PoiSearchIndex restaurantIndex = createPoiSearchIndex(indexId, "맛집", "restaurants", 500L, "서울시", 37.5, 127.0);
            Restaurant restaurant = mock(Restaurant.class);
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.of(restaurantIndex));
            when(publicDataRepository.findRestaurantById(500L)).thenReturn(Optional.of(restaurant));
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(Restaurant.class);
            
            verify(publicDataRepository).findRestaurantById(500L);
        }
        
        @Test
        @DisplayName("문화예약 데이터 조회")
        void getPublicDataByIndex_CulturalReservation() {
            // given
            Long indexId = 6L;
            PoiSearchIndex reservationIndex = createPoiSearchIndex(indexId, "문화예약", "cultural_reservation", 600L, "서울시", 37.5, 127.0);
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.of(reservationIndex));
            when(publicDataRepository.findCulturalReservationById(600L)).thenReturn(Optional.empty());
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNull();
            
            verify(publicDataRepository).findCulturalReservationById(600L);
        }
        
        @Test
        @DisplayName("인덱스 없음")
        void getPublicDataByIndex_IndexNotFound() {
            // given
            Long indexId = 999L;
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.empty());
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNull();
            
            verify(searchIndexRepository).findById(indexId);
            verifyNoInteractions(publicDataRepository);
        }
        
        @Test
        @DisplayName("알 수 없는 테이블 타입")
        void getPublicDataByIndex_UnknownTableType() {
            // given
            Long indexId = 7L;
            PoiSearchIndex unknownIndex = createPoiSearchIndex(indexId, "알 수 없음", "unknown_table", 700L, "서울시", 37.5, 127.0);
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.of(unknownIndex));
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNull();
            
            verify(searchIndexRepository).findById(indexId);
            verifyNoInteractions(publicDataRepository);
        }
        
        @Test
        @DisplayName("대소문자 무관 테이블명 처리")
        void getPublicDataByIndex_CaseInsensitiveTableName() {
            // given
            Long indexId = 8L;
            PoiSearchIndex mixedCaseIndex = createPoiSearchIndex(indexId, "도서관", "LIBRARIES", 800L, "서울시", 37.5, 127.0);
            Library library = mock(Library.class);
            
            when(searchIndexRepository.findById(indexId)).thenReturn(Optional.of(mixedCaseIndex));
            when(publicDataRepository.findLibraryById(800L)).thenReturn(Optional.of(library));
            
            // when
            Object result = searchService.getPublicDataByIndex(indexId);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(Library.class);
            
            verify(publicDataRepository).findLibraryById(800L);
        }
    }
    
    // Helper methods
    private PoiSearchIndex createPoiSearchIndex(Long id, String name, String refTable, Long refId, 
                                               String address, Double latitude, Double longitude) {
        PoiSearchIndex index = new PoiSearchIndex(
                name,
                address,
                name + " 설명",
                name + " 키워드",
                refTable,
                refId
        );
        
        // Set ID using reflection
        try {
            java.lang.reflect.Field idField = PoiSearchIndex.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(index, id);
        } catch (Exception e) {
            // Ignore in test
        }
        
        return index;
    }
}