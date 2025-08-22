package com.seoulfit.backend.search.infrastructure.mapper;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import com.seoulfit.backend.search.domain.PoiSearchIndex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PoiIndexMapper 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@DisplayName("PoiIndexMapper 단위 테스트")
class PoiIndexMapperTest {

    @Nested
    @DisplayName("CoolingCenter 매핑 테스트")
    class FromCoolingCenterTest {
        
        @Test
        @DisplayName("모든 필드가 있는 경우")
        void fromCoolingCenter_AllFields() {
            // given
            CoolingCenter coolingCenter = CoolingCenter.builder()
                    .name("중구 무더위쉼터")
                    .lotAddress("서울시 중구 을지로 1가")
                    .roadAddress("서울시 중구 을지로 100")
                    .facilityType1("공공시설")
                    .facilityType2("커뮤니티센터")
                    .build();
            setId(coolingCenter, 1L);
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCoolingCenter(coolingCenter);
            
            // then
            assertThat(index.getName()).isEqualTo("중구 무더위쉼터");
            assertThat(index.getAddress()).isEqualTo("서울시 중구 을지로 1가,서울시 중구 을지로 100");
            assertThat(index.getRemark()).isEqualTo("공공시설,커뮤니티센터");
            assertThat(index.getRefTable()).isEqualTo("cooling_centers");
            assertThat(index.getRefId()).isEqualTo(1L);
        }
        
        @Test
        @DisplayName("일부 필드가 null인 경우")
        void fromCoolingCenter_PartialFields() {
            // given
            CoolingCenter coolingCenter = CoolingCenter.builder()
                    .name("무더위쉼터")
                    .lotAddress("서울시 중구")
                    .roadAddress(null)
                    .facilityType1("공공시설")
                    .facilityType2(null)
                    .build();
            setId(coolingCenter, 2L);
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCoolingCenter(coolingCenter);
            
            // then
            assertThat(index.getName()).isEqualTo("무더위쉼터");
            assertThat(index.getAddress()).isEqualTo("서울시 중구");
            assertThat(index.getRemark()).isEqualTo("공공시설");
            assertThat(index.getRefTable()).isEqualTo("cooling_centers");
            assertThat(index.getRefId()).isEqualTo(2L);
        }
        
        @Test
        @DisplayName("같은 주소인 경우 중복 제거")
        void fromCoolingCenter_SameAddress() {
            // given
            CoolingCenter coolingCenter = CoolingCenter.builder()
                    .name("쉼터")
                    .lotAddress("동일한 주소")
                    .roadAddress("동일한 주소")
                    .build();
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCoolingCenter(coolingCenter);
            
            // then
            assertThat(index.getAddress()).isEqualTo("동일한 주소");
        }
    }
    
    @Nested
    @DisplayName("CulturalEvent 매핑 테스트")
    class FromCulturalEventTest {
        
        @Test
        @DisplayName("문화행사 매핑")
        void fromCulturalEvent() {
            // given
            CulturalEvent event = CulturalEvent.builder()
                    .title("서울 음악 페스티벌")
                    .place("서울 광장")
                    .build();
            setId(event, 10L);
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCulturalEvent(event);
            
            // then
            assertThat(index.getName()).isEqualTo("서울 음악 페스티벌");
            assertThat(index.getAddress()).isNull();
            assertThat(index.getRemark()).isEqualTo("서울 광장");
            assertThat(index.getRefTable()).isEqualTo("cultural_events");
            assertThat(index.getRefId()).isEqualTo(10L);
        }
    }
    
    @Nested
    @DisplayName("CulturalReservation 매핑 테스트")
    class FromCulturalReservationTest {
        
        @Test
        @DisplayName("문화예약 매핑")
        void fromCulturalReservation() {
            // given
            CulturalReservation reservation = CulturalReservation.builder()
                    .svcNm("체육관 대관")
                    .placeNm("올림픽 체육관")
                    .build();
            setId(reservation, 20L);
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCulturalReservation(reservation);
            
            // then
            assertThat(index.getName()).isEqualTo("체육관 대관");
            assertThat(index.getAddress()).isNull();
            assertThat(index.getRemark()).isEqualTo("올림픽 체육관");
            assertThat(index.getRefTable()).isEqualTo("cultural_reservation");
            assertThat(index.getRefId()).isEqualTo(20L);
        }
    }
    
    @Nested
    @DisplayName("Library 매핑 테스트")
    class FromLibraryTest {
        
        @Test
        @DisplayName("도서관 매핑")
        void fromLibrary() {
            // given
            Library library = Library.builder()
                    .lbrryName("서울도서관")
                    .adres("서울시 중구 세종대로 110")
                    .build();
            setId(library, 30L);
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromLibrary(library);
            
            // then
            assertThat(index.getName()).isEqualTo("서울도서관");
            assertThat(index.getAddress()).isEqualTo("서울시 중구 세종대로 110");
            assertThat(index.getRemark()).isNull();
            assertThat(index.getRefTable()).isEqualTo("libraries");
            assertThat(index.getRefId()).isEqualTo(30L);
        }
    }
    
    @Nested
    @DisplayName("Park 매핑 테스트")
    class FromParkTest {
        
        @Test
        @DisplayName("공원 매핑")
        void fromPark() {
            // given
            Park park = Park.builder()
                    .name("남산공원")
                    .address("서울시 용산구 남산공원길")
                    .build();
            setId(park, 40L);
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromPark(park);
            
            // then
            assertThat(index.getName()).isEqualTo("남산공원");
            assertThat(index.getAddress()).isEqualTo("서울시 용산구 남산공원길");
            assertThat(index.getRemark()).isNull();
            assertThat(index.getRefTable()).isEqualTo("park");
            assertThat(index.getRefId()).isEqualTo(40L);
        }
    }
    
    @Nested
    @DisplayName("Restaurant 매핑 테스트")
    class FromRestaurantTest {
        
        @Test
        @DisplayName("맛집 매핑 - 모든 주소")
        void fromRestaurant_AllAddresses() {
            // given
            Restaurant restaurant = Restaurant.builder()
                    .name("맛있는 식당")
                    .address("서울시 강남구 테헤란로 1")
                    .newAddress("서울시 강남구 테헤란로 100")
                    .build();
            setId(restaurant, 50L);
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromRestaurant(restaurant);
            
            // then
            assertThat(index.getName()).isEqualTo("맛있는 식당");
            assertThat(index.getAddress()).isEqualTo("서울시 강남구 테헤란로 1,서울시 강남구 테헤란로 100");
            assertThat(index.getRefTable()).isEqualTo("restaurants");
            assertThat(index.getRefId()).isEqualTo(50L);
        }
        
        @Test
        @DisplayName("맛집 매핑 - 구주소만")
        void fromRestaurant_OldAddressOnly() {
            // given
            Restaurant restaurant = Restaurant.builder()
                    .name("전통 식당")
                    .address("서울시 종로구 삼일대로")
                    .newAddress(null)
                    .build();
            setId(restaurant, 51L);
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromRestaurant(restaurant);
            
            // then
            assertThat(index.getName()).isEqualTo("전통 식당");
            assertThat(index.getAddress()).isEqualTo("서울시 종로구 삼일대로");
        }
        
        @Test
        @DisplayName("맛집 매핑 - 신주소만")
        void fromRestaurant_NewAddressOnly() {
            // given
            Restaurant restaurant = Restaurant.builder()
                    .name("모던 레스토랑")
                    .address(null)
                    .newAddress("서울시 송파구 올림픽로")
                    .build();
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromRestaurant(restaurant);
            
            // then
            assertThat(index.getAddress()).isEqualTo("서울시 송파구 올림픽로");
        }
        
        @Test
        @DisplayName("맛집 매핑 - 동일한 주소")
        void fromRestaurant_SameAddress() {
            // given
            Restaurant restaurant = Restaurant.builder()
                    .name("카페")
                    .address("같은 주소")
                    .newAddress("같은 주소")
                    .build();
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromRestaurant(restaurant);
            
            // then
            assertThat(index.getAddress()).isEqualTo("같은 주소");
        }
    }
    
    @Nested
    @DisplayName("joinWithComma 헬퍼 메서드 테스트")
    class JoinWithCommaTest {
        
        @Test
        @DisplayName("두 문자열 모두 null")
        void joinWithComma_BothNull() {
            // given
            CoolingCenter coolingCenter = CoolingCenter.builder()
                    .name("테스트")
                    .lotAddress(null)
                    .roadAddress(null)
                    .build();
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCoolingCenter(coolingCenter);
            
            // then
            assertThat(index.getAddress()).isNull();
        }
        
        @Test
        @DisplayName("첫 번째만 null")
        void joinWithComma_FirstNull() {
            // given
            CoolingCenter coolingCenter = CoolingCenter.builder()
                    .name("테스트")
                    .lotAddress(null)
                    .roadAddress("두 번째 주소")
                    .build();
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCoolingCenter(coolingCenter);
            
            // then
            assertThat(index.getAddress()).isEqualTo("두 번째 주소");
        }
        
        @Test
        @DisplayName("두 번째만 null")
        void joinWithComma_SecondNull() {
            // given
            CoolingCenter coolingCenter = CoolingCenter.builder()
                    .name("테스트")
                    .lotAddress("첫 번째 주소")
                    .roadAddress(null)
                    .build();
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCoolingCenter(coolingCenter);
            
            // then
            assertThat(index.getAddress()).isEqualTo("첫 번째 주소");
        }
        
        @Test
        @DisplayName("두 문자열 모두 존재하고 다름")
        void joinWithComma_BothDifferent() {
            // given
            CoolingCenter coolingCenter = CoolingCenter.builder()
                    .name("테스트")
                    .lotAddress("첫 번째")
                    .roadAddress("두 번째")
                    .build();
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCoolingCenter(coolingCenter);
            
            // then
            assertThat(index.getAddress()).isEqualTo("첫 번째,두 번째");
        }
        
        @Test
        @DisplayName("두 문자열이 같음")
        void joinWithComma_BothSame() {
            // given
            CoolingCenter coolingCenter = CoolingCenter.builder()
                    .name("테스트")
                    .lotAddress("동일한 값")
                    .roadAddress("동일한 값")
                    .build();
            
            // when
            PoiSearchIndex index = PoiIndexMapper.fromCoolingCenter(coolingCenter);
            
            // then
            assertThat(index.getAddress()).isEqualTo("동일한 값");
        }
    }
    
    // Helper method
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