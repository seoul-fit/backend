package com.seoulfit.backend.location.application;

import com.seoulfit.backend.location.domain.SportsFacility;
import com.seoulfit.backend.location.infrastructure.SportsFacilityRepository;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.CoolingCenterRepository;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.LibraryRepository;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.park.adapter.out.persistence.repository.ParkRepository;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence.repository.RestaurantRepository;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * LocationBasedDataService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LocationBasedDataService 테스트")
class LocationBasedDataServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private ParkRepository parkRepository;

    @Mock
    private SportsFacilityRepository sportsFacilityRepository;

    @Mock
    private CoolingCenterRepository coolingCenterRepository;

    @InjectMocks
    private LocationBasedDataService locationBasedDataService;

    private Double testLatitude;
    private Double testLongitude;
    private Double testRadius;

    @BeforeEach
    void setUp() {
        testLatitude = 37.5665;
        testLongitude = 126.9780;
        testRadius = 2.0;
    }

    @Nested
    @DisplayName("맛집 조회 테스트")
    class RestaurantTests {
        
        @Test
        @DisplayName("위치 기반 맛집 조회 - 성공")
        void findNearbyRestaurants_Success() {
            // given
            List<Restaurant> expectedRestaurants = Arrays.asList(
                createRestaurant(1L, "맛집1", testLatitude + 0.001, testLongitude + 0.001),
                createRestaurant(2L, "맛집2", testLatitude + 0.002, testLongitude + 0.002)
            );
            when(restaurantRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(expectedRestaurants);

            // when
            List<Restaurant> result = locationBasedDataService.findNearbyRestaurants(testLatitude, testLongitude, testRadius);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("맛집1");
            assertThat(result.get(1).getName()).isEqualTo("맛집2");
            verify(restaurantRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
        }

        @Test
        @DisplayName("위치 기반 맛집 조회 - 위치 정보 없음")
        void findNearbyRestaurants_NoLocation() {
            // when
            List<Restaurant> result = locationBasedDataService.findNearbyRestaurants(null, null, testRadius);

            // then
            assertThat(result).isEmpty();
            verify(restaurantRepository, never()).findByLocationWithinRadius(anyDouble(), anyDouble(), anyDouble());
        }

        @Test
        @DisplayName("위치 기반 맛집 조회 - 결과 없음")
        void findNearbyRestaurants_NoResults() {
            // given
            when(restaurantRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Collections.emptyList());

            // when
            List<Restaurant> result = locationBasedDataService.findNearbyRestaurants(testLatitude, testLongitude, testRadius);

            // then
            assertThat(result).isEmpty();
            verify(restaurantRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
        }
    }

    @Nested
    @DisplayName("도서관 조회 테스트")
    class LibraryTests {
        
        @Test
        @DisplayName("위치 기반 도서관 조회 - 성공")
        void findNearbyLibraries_Success() {
            // given
            List<Library> expectedLibraries = Arrays.asList(
                createLibrary(1L, "도서관1", testLatitude + 0.001, testLongitude + 0.001),
                createLibrary(2L, "도서관2", testLatitude + 0.002, testLongitude + 0.002)
            );
            when(libraryRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(expectedLibraries);

            // when
            List<Library> result = locationBasedDataService.findNearbyLibraries(testLatitude, testLongitude, testRadius);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getLbrryName()).isEqualTo("도서관1");
            verify(libraryRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
        }

        @Test
        @DisplayName("위치 기반 도서관 조회 - 위도 없음")
        void findNearbyLibraries_NoLatitude() {
            // when
            List<Library> result = locationBasedDataService.findNearbyLibraries(null, testLongitude, testRadius);

            // then
            assertThat(result).isEmpty();
            verify(libraryRepository, never()).findByLocationWithinRadius(anyDouble(), anyDouble(), anyDouble());
        }

        @Test
        @DisplayName("위치 기반 도서관 조회 - 경도 없음")
        void findNearbyLibraries_NoLongitude() {
            // when
            List<Library> result = locationBasedDataService.findNearbyLibraries(testLatitude, null, testRadius);

            // then
            assertThat(result).isEmpty();
            verify(libraryRepository, never()).findByLocationWithinRadius(anyDouble(), anyDouble(), anyDouble());
        }
    }

    @Nested
    @DisplayName("공원 조회 테스트")
    class ParkTests {
        
        @Test
        @DisplayName("위치 기반 공원 조회 - 성공")
        void findNearbyParks_Success() {
            // given
            List<Park> expectedParks = Arrays.asList(
                createPark(1L, "공원1", testLatitude + 0.001, testLongitude + 0.001),
                createPark(2L, "공원2", testLatitude + 0.002, testLongitude + 0.002),
                createPark(3L, "공원3", testLatitude + 0.003, testLongitude + 0.003)
            );
            when(parkRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(expectedParks);

            // when
            List<Park> result = locationBasedDataService.findNearbyParks(testLatitude, testLongitude, testRadius);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getName()).isEqualTo("공원1");
            verify(parkRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
        }

        @Test
        @DisplayName("위치 기반 공원 조회 - 작은 반경")
        void findNearbyParks_SmallRadius() {
            // given
            Double smallRadius = 0.5;
            when(parkRepository.findByLocationWithinRadius(testLatitude, testLongitude, smallRadius))
                    .thenReturn(Collections.emptyList());

            // when
            List<Park> result = locationBasedDataService.findNearbyParks(testLatitude, testLongitude, smallRadius);

            // then
            assertThat(result).isEmpty();
            verify(parkRepository).findByLocationWithinRadius(testLatitude, testLongitude, smallRadius);
        }
    }

    @Nested
    @DisplayName("체육시설 조회 테스트")
    class SportsFacilityTests {
        
        @Test
        @DisplayName("위치 기반 체육시설 조회 - 성공")
        void findNearbySportsFacilities_Success() {
            // given
            List<SportsFacility> expectedFacilities = Arrays.asList(
                createSportsFacility(1L, "체육관1", testLatitude + 0.001, testLongitude + 0.001),
                createSportsFacility(2L, "수영장1", testLatitude + 0.002, testLongitude + 0.002)
            );
            when(sportsFacilityRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(expectedFacilities);

            // when
            List<SportsFacility> result = locationBasedDataService.findNearbySportsFacilities(testLatitude, testLongitude, testRadius);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("체육관1");
            verify(sportsFacilityRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
        }

        @Test
        @DisplayName("위치 기반 체육시설 조회 - 큰 반경")
        void findNearbySportsFacilities_LargeRadius() {
            // given
            Double largeRadius = 10.0;
            List<SportsFacility> manyFacilities = Arrays.asList(
                createSportsFacility(1L, "체육관1", testLatitude + 0.01, testLongitude + 0.01),
                createSportsFacility(2L, "체육관2", testLatitude + 0.02, testLongitude + 0.02),
                createSportsFacility(3L, "체육관3", testLatitude + 0.03, testLongitude + 0.03),
                createSportsFacility(4L, "체육관4", testLatitude + 0.04, testLongitude + 0.04),
                createSportsFacility(5L, "체육관5", testLatitude + 0.05, testLongitude + 0.05)
            );
            when(sportsFacilityRepository.findByLocationWithinRadius(testLatitude, testLongitude, largeRadius))
                    .thenReturn(manyFacilities);

            // when
            List<SportsFacility> result = locationBasedDataService.findNearbySportsFacilities(testLatitude, testLongitude, largeRadius);

            // then
            assertThat(result).hasSize(5);
            verify(sportsFacilityRepository).findByLocationWithinRadius(testLatitude, testLongitude, largeRadius);
        }
    }

    @Nested
    @DisplayName("무더위쉼터 조회 테스트")
    class CoolingCenterTests {
        
        @Test
        @DisplayName("위치 기반 무더위쉼터 조회 - 성공")
        void findNearbyCoolingCenters_Success() {
            // given
            List<CoolingCenter> expectedCenters = Arrays.asList(
                createCoolingCenter(1L, "쉼터1", testLatitude + 0.001, testLongitude + 0.001),
                createCoolingCenter(2L, "쉼터2", testLatitude + 0.002, testLongitude + 0.002)
            );
            when(coolingCenterRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(expectedCenters);

            // when
            List<CoolingCenter> result = locationBasedDataService.findNearbyCoolingCenters(testLatitude, testLongitude, testRadius);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("쉼터1");
            verify(coolingCenterRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
        }

        @Test
        @DisplayName("위치 기반 무더위쉼터 조회 - 위치 정보 없음")
        void findNearbyCoolingCenters_NoLocation() {
            // when
            List<CoolingCenter> result = locationBasedDataService.findNearbyCoolingCenters(null, null, testRadius);

            // then
            assertThat(result).isEmpty();
            verify(coolingCenterRepository, never()).findByLocationWithinRadius(anyDouble(), anyDouble(), anyDouble());
        }
    }

    @Nested
    @DisplayName("통합 조회 테스트")
    class IntegratedSearchTests {
        
        @Test
        @DisplayName("모든 시설 통합 조회 - 성공")
        void findAllNearbyFacilities_Success() {
            // given
            when(restaurantRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Arrays.asList(createRestaurant(1L, "맛집1", testLatitude, testLongitude)));
            when(libraryRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Arrays.asList(createLibrary(1L, "도서관1", testLatitude, testLongitude)));
            when(parkRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Arrays.asList(createPark(1L, "공원1", testLatitude, testLongitude)));
            when(sportsFacilityRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Arrays.asList(createSportsFacility(1L, "체육관1", testLatitude, testLongitude)));
            when(coolingCenterRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Arrays.asList(createCoolingCenter(1L, "쉼터1", testLatitude, testLongitude)));

            // when
            List<Restaurant> restaurants = locationBasedDataService.findNearbyRestaurants(testLatitude, testLongitude, testRadius);
            List<Library> libraries = locationBasedDataService.findNearbyLibraries(testLatitude, testLongitude, testRadius);
            List<Park> parks = locationBasedDataService.findNearbyParks(testLatitude, testLongitude, testRadius);
            List<SportsFacility> sportsFacilities = locationBasedDataService.findNearbySportsFacilities(testLatitude, testLongitude, testRadius);
            List<CoolingCenter> coolingCenters = locationBasedDataService.findNearbyCoolingCenters(testLatitude, testLongitude, testRadius);

            // then
            assertThat(restaurants).hasSize(1);
            assertThat(libraries).hasSize(1);
            assertThat(parks).hasSize(1);
            assertThat(sportsFacilities).hasSize(1);
            assertThat(coolingCenters).hasSize(1);
            
            // 모든 repository 메서드가 호출되었는지 확인
            verify(restaurantRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
            verify(libraryRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
            verify(parkRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
            verify(sportsFacilityRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
            verify(coolingCenterRepository).findByLocationWithinRadius(testLatitude, testLongitude, testRadius);
        }

        @Test
        @DisplayName("모든 시설 통합 조회 - 결과 없음")
        void findAllNearbyFacilities_NoResults() {
            // given
            when(restaurantRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Collections.emptyList());
            when(libraryRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Collections.emptyList());
            when(parkRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Collections.emptyList());
            when(sportsFacilityRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Collections.emptyList());
            when(coolingCenterRepository.findByLocationWithinRadius(testLatitude, testLongitude, testRadius))
                    .thenReturn(Collections.emptyList());

            // when
            List<Restaurant> restaurants = locationBasedDataService.findNearbyRestaurants(testLatitude, testLongitude, testRadius);
            List<Library> libraries = locationBasedDataService.findNearbyLibraries(testLatitude, testLongitude, testRadius);
            List<Park> parks = locationBasedDataService.findNearbyParks(testLatitude, testLongitude, testRadius);
            List<SportsFacility> sportsFacilities = locationBasedDataService.findNearbySportsFacilities(testLatitude, testLongitude, testRadius);
            List<CoolingCenter> coolingCenters = locationBasedDataService.findNearbyCoolingCenters(testLatitude, testLongitude, testRadius);

            // then
            assertThat(restaurants).isEmpty();
            assertThat(libraries).isEmpty();
            assertThat(parks).isEmpty();
            assertThat(sportsFacilities).isEmpty();
            assertThat(coolingCenters).isEmpty();
        }
    }

    // 헬퍼 메서드들
    private Restaurant createRestaurant(Long id, String name, Double latitude, Double longitude) {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.getId()).thenReturn(id);
        when(restaurant.getName()).thenReturn(name);
        when(restaurant.getLatitude()).thenReturn(latitude);
        when(restaurant.getLongitude()).thenReturn(longitude);
        return restaurant;
    }

    private Library createLibrary(Long id, String name, Double latitude, Double longitude) {
        Library library = mock(Library.class);
        when(library.getId()).thenReturn(id);
        when(library.getLbrryName()).thenReturn(name);
        when(library.getLatitude()).thenReturn(latitude);
        when(library.getLongitude()).thenReturn(longitude);
        return library;
    }

    private Park createPark(Long id, String name, Double latitude, Double longitude) {
        Park park = mock(Park.class);
        when(park.getId()).thenReturn(id);
        when(park.getName()).thenReturn(name);
        when(park.getLatitude()).thenReturn(latitude);
        when(park.getLongitude()).thenReturn(longitude);
        return park;
    }

    private SportsFacility createSportsFacility(Long id, String name, Double latitude, Double longitude) {
        SportsFacility facility = mock(SportsFacility.class);
        when(facility.getId()).thenReturn(id);
        when(facility.getName()).thenReturn(name);
        when(facility.getLatitude()).thenReturn(latitude);
        when(facility.getLongitude()).thenReturn(longitude);
        return facility;
    }

    private CoolingCenter createCoolingCenter(Long id, String name, Double latitude, Double longitude) {
        CoolingCenter center = mock(CoolingCenter.class);
        when(center.getId()).thenReturn(id);
        when(center.getName()).thenReturn(name);
        when(center.getLatitude()).thenReturn(latitude);
        when(center.getLongitude()).thenReturn(longitude);
        return center;
    }
}