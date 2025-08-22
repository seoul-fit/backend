package com.seoulfit.backend.trigger.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * TriggerUtils 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@DisplayName("TriggerUtils 단위 테스트")
class TriggerUtilsTest {

    @Nested
    @DisplayName("거리 계산 테스트")
    class CalculateDistanceTest {
        
        @Test
        @DisplayName("같은 위치 - 거리 0")
        void calculateDistance_SameLocation() {
            // given
            double lat = 37.5665;
            double lng = 126.9780;
            
            // when
            double distance = TriggerUtils.calculateDistance(lat, lng, lat, lng);
            
            // then
            assertThat(distance).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("서울시청에서 서울역까지 거리")
        void calculateDistance_SeoulCityHallToSeoulStation() {
            // given
            double cityHallLat = 37.5665;
            double cityHallLng = 126.9780;
            double stationLat = 37.5547;
            double stationLng = 126.9707;
            
            // when
            double distance = TriggerUtils.calculateDistance(cityHallLat, cityHallLng, stationLat, stationLng);
            
            // then
            // 실제 거리는 약 1.4km
            assertThat(distance).isCloseTo(1400, within(200.0));
        }
        
        @Test
        @DisplayName("서울에서 부산까지 거리")
        void calculateDistance_SeoulToBusan() {
            // given
            double seoulLat = 37.5665;
            double seoulLng = 126.9780;
            double busanLat = 35.1796;
            double busanLng = 129.0756;
            
            // when
            double distance = TriggerUtils.calculateDistance(seoulLat, seoulLng, busanLat, busanLng);
            
            // then
            // 실제 거리는 약 325km
            assertThat(distance).isCloseTo(325000, within(10000.0));
        }
        
        @Test
        @DisplayName("음수 좌표 처리")
        void calculateDistance_NegativeCoordinates() {
            // given
            double lat1 = -37.5665;
            double lng1 = -126.9780;
            double lat2 = -35.1796;
            double lng2 = -129.0756;
            
            // when
            double distance = TriggerUtils.calculateDistance(lat1, lng1, lat2, lng2);
            
            // then
            assertThat(distance).isGreaterThan(0);
        }
    }
    
    @Nested
    @DisplayName("String 값 추출 테스트")
    class GetStringValueTest {
        
        @Test
        @DisplayName("정상적인 String 값 추출")
        void getStringValue_Success() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("name", "서울시청");
            
            // when
            String value = TriggerUtils.getStringValue(map, "name");
            
            // then
            assertThat(value).isEqualTo("서울시청");
        }
        
        @Test
        @DisplayName("존재하지 않는 키 - null 반환")
        void getStringValue_KeyNotFound() {
            // given
            Map<String, Object> map = new HashMap<>();
            
            // when
            String value = TriggerUtils.getStringValue(map, "notExist");
            
            // then
            assertThat(value).isNull();
        }
        
        @Test
        @DisplayName("null 맵 - null 반환")
        void getStringValue_NullMap() {
            // when
            String value = TriggerUtils.getStringValue(null, "key");
            
            // then
            assertThat(value).isNull();
        }
        
        @Test
        @DisplayName("숫자를 String으로 변환")
        void getStringValue_NumberToString() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("number", 123);
            
            // when
            String value = TriggerUtils.getStringValue(map, "number");
            
            // then
            assertThat(value).isEqualTo("123");
        }
        
        @Test
        @DisplayName("기본값 반환")
        void getStringValue_WithDefault() {
            // given
            Map<String, Object> map = new HashMap<>();
            
            // when
            String value = TriggerUtils.getStringValue(map, "notExist", "기본값");
            
            // then
            assertThat(value).isEqualTo("기본값");
        }
    }
    
    @Nested
    @DisplayName("Double 값 추출 테스트")
    class GetDoubleValueTest {
        
        @Test
        @DisplayName("Double 타입 추출")
        void getDoubleValue_DoubleType() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("value", 37.5665);
            
            // when
            Double value = TriggerUtils.getDoubleValue(map, "value");
            
            // then
            assertThat(value).isEqualTo(37.5665);
        }
        
        @Test
        @DisplayName("Integer를 Double로 변환")
        void getDoubleValue_IntegerToDouble() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("value", 100);
            
            // when
            Double value = TriggerUtils.getDoubleValue(map, "value");
            
            // then
            assertThat(value).isEqualTo(100.0);
        }
        
        @Test
        @DisplayName("String을 Double로 변환")
        void getDoubleValue_StringToDouble() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("value", "37.5665");
            
            // when
            Double value = TriggerUtils.getDoubleValue(map, "value");
            
            // then
            assertThat(value).isEqualTo(37.5665);
        }
        
        @Test
        @DisplayName("빈 문자열 - null 반환")
        void getDoubleValue_EmptyString() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("value", "   ");
            
            // when
            Double value = TriggerUtils.getDoubleValue(map, "value");
            
            // then
            assertThat(value).isNull();
        }
        
        @Test
        @DisplayName("잘못된 형식 - null 반환")
        void getDoubleValue_InvalidFormat() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("value", "not-a-number");
            
            // when
            Double value = TriggerUtils.getDoubleValue(map, "value");
            
            // then
            assertThat(value).isNull();
        }
        
        @Test
        @DisplayName("기본값 반환")
        void getDoubleValue_WithDefault() {
            // given
            Map<String, Object> map = new HashMap<>();
            
            // when
            Double value = TriggerUtils.getDoubleValue(map, "notExist", 0.0);
            
            // then
            assertThat(value).isEqualTo(0.0);
        }
    }
    
    @Nested
    @DisplayName("Integer 값 추출 테스트")
    class GetIntValueTest {
        
        @Test
        @DisplayName("Integer 타입 추출")
        void getIntValue_IntegerType() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("value", 100);
            
            // when
            Integer value = TriggerUtils.getIntValue(map, "value");
            
            // then
            assertThat(value).isEqualTo(100);
        }
        
        @Test
        @DisplayName("Double을 Integer로 변환")
        void getIntValue_DoubleToInteger() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("value", 100.5);
            
            // when
            Integer value = TriggerUtils.getIntValue(map, "value");
            
            // then
            assertThat(value).isEqualTo(100);  // 소수점 버림
        }
        
        @Test
        @DisplayName("String을 Integer로 변환")
        void getIntValue_StringToInteger() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("value", "123");
            
            // when
            Integer value = TriggerUtils.getIntValue(map, "value");
            
            // then
            assertThat(value).isEqualTo(123);
        }
        
        @Test
        @DisplayName("기본값 반환")
        void getIntValue_WithDefault() {
            // given
            Map<String, Object> map = new HashMap<>();
            
            // when
            Integer value = TriggerUtils.getIntValue(map, "notExist", -1);
            
            // then
            assertThat(value).isEqualTo(-1);
        }
    }
    
    @Nested
    @DisplayName("좌표 유효성 검증 테스트")
    class CoordinateValidationTest {
        
        @Test
        @DisplayName("유효한 위도")
        void isValidLatitude_Valid() {
            assertThat(TriggerUtils.isValidLatitude(0.0)).isTrue();
            assertThat(TriggerUtils.isValidLatitude(37.5665)).isTrue();
            assertThat(TriggerUtils.isValidLatitude(-37.5665)).isTrue();
            assertThat(TriggerUtils.isValidLatitude(90.0)).isTrue();
            assertThat(TriggerUtils.isValidLatitude(-90.0)).isTrue();
        }
        
        @Test
        @DisplayName("유효하지 않은 위도")
        void isValidLatitude_Invalid() {
            assertThat(TriggerUtils.isValidLatitude(null)).isFalse();
            assertThat(TriggerUtils.isValidLatitude(90.1)).isFalse();
            assertThat(TriggerUtils.isValidLatitude(-90.1)).isFalse();
            assertThat(TriggerUtils.isValidLatitude(180.0)).isFalse();
        }
        
        @Test
        @DisplayName("유효한 경도")
        void isValidLongitude_Valid() {
            assertThat(TriggerUtils.isValidLongitude(0.0)).isTrue();
            assertThat(TriggerUtils.isValidLongitude(126.9780)).isTrue();
            assertThat(TriggerUtils.isValidLongitude(-126.9780)).isTrue();
            assertThat(TriggerUtils.isValidLongitude(180.0)).isTrue();
            assertThat(TriggerUtils.isValidLongitude(-180.0)).isTrue();
        }
        
        @Test
        @DisplayName("유효하지 않은 경도")
        void isValidLongitude_Invalid() {
            assertThat(TriggerUtils.isValidLongitude(null)).isFalse();
            assertThat(TriggerUtils.isValidLongitude(180.1)).isFalse();
            assertThat(TriggerUtils.isValidLongitude(-180.1)).isFalse();
            assertThat(TriggerUtils.isValidLongitude(360.0)).isFalse();
        }
        
        @Test
        @DisplayName("유효한 좌표 쌍")
        void isValidCoordinate_Valid() {
            assertThat(TriggerUtils.isValidCoordinate(37.5665, 126.9780)).isTrue();
            assertThat(TriggerUtils.isValidCoordinate(0.0, 0.0)).isTrue();
            assertThat(TriggerUtils.isValidCoordinate(-90.0, -180.0)).isTrue();
        }
        
        @Test
        @DisplayName("유효하지 않은 좌표 쌍")
        void isValidCoordinate_Invalid() {
            assertThat(TriggerUtils.isValidCoordinate(null, 126.9780)).isFalse();
            assertThat(TriggerUtils.isValidCoordinate(37.5665, null)).isFalse();
            assertThat(TriggerUtils.isValidCoordinate(91.0, 126.9780)).isFalse();
            assertThat(TriggerUtils.isValidCoordinate(37.5665, 181.0)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("문자열 유틸리티 테스트")
    class StringUtilityTest {
        
        @Test
        @DisplayName("텍스트 존재 확인")
        void hasText() {
            assertThat(TriggerUtils.hasText("text")).isTrue();
            assertThat(TriggerUtils.hasText("  text  ")).isTrue();
            assertThat(TriggerUtils.hasText(null)).isFalse();
            assertThat(TriggerUtils.hasText("")).isFalse();
            assertThat(TriggerUtils.hasText("   ")).isFalse();
        }
        
        @Test
        @DisplayName("문자열 동등 비교")
        void equals() {
            assertThat(TriggerUtils.equals("test", "test")).isTrue();
            assertThat(TriggerUtils.equals(null, null)).isTrue();
            assertThat(TriggerUtils.equals("test", "Test")).isFalse();
            assertThat(TriggerUtils.equals("test", null)).isFalse();
            assertThat(TriggerUtils.equals(null, "test")).isFalse();
        }
    }
    
    @Nested
    @DisplayName("거리 포맷 테스트")
    class FormatDistanceTest {
        
        @Test
        @DisplayName("미터 단위 포맷")
        void formatDistance_Meters() {
            assertThat(TriggerUtils.formatDistance(0)).isEqualTo("0m");
            assertThat(TriggerUtils.formatDistance(100)).isEqualTo("100m");
            assertThat(TriggerUtils.formatDistance(999)).isEqualTo("999m");
        }
        
        @Test
        @DisplayName("킬로미터 단위 포맷")
        void formatDistance_Kilometers() {
            assertThat(TriggerUtils.formatDistance(1000)).isEqualTo("1.0km");
            assertThat(TriggerUtils.formatDistance(1500)).isEqualTo("1.5km");
            assertThat(TriggerUtils.formatDistance(10000)).isEqualTo("10.0km");
        }
    }
    
    @Nested
    @DisplayName("반경 내 확인 테스트")
    class IsWithinRadiusTest {
        
        @Test
        @DisplayName("반경 내 위치")
        void isWithinRadius_Inside() {
            // given
            double userLat = 37.5665;
            double userLng = 126.9780;
            double targetLat = 37.5660;
            double targetLng = 126.9775;
            double radius = 100; // 100미터
            
            // when
            boolean result = TriggerUtils.isWithinRadius(userLat, userLng, targetLat, targetLng, radius);
            
            // then
            assertThat(result).isTrue();
        }
        
        @Test
        @DisplayName("반경 밖 위치")
        void isWithinRadius_Outside() {
            // given
            double userLat = 37.5665;
            double userLng = 126.9780;
            double targetLat = 37.5547; // 서울역
            double targetLng = 126.9707;
            double radius = 1000; // 1km
            
            // when
            boolean result = TriggerUtils.isWithinRadius(userLat, userLng, targetLat, targetLng, radius);
            
            // then
            assertThat(result).isFalse();
        }
        
        @Test
        @DisplayName("정확히 반경 경계")
        void isWithinRadius_ExactBoundary() {
            // given
            double userLat = 37.5665;
            double userLng = 126.9780;
            double targetLat = 37.5665;
            double targetLng = 126.9780;
            double radius = 0;
            
            // when
            boolean result = TriggerUtils.isWithinRadius(userLat, userLng, targetLat, targetLng, radius);
            
            // then
            assertThat(result).isTrue();
        }
        
        @Test
        @DisplayName("유효하지 않은 좌표")
        void isWithinRadius_InvalidCoordinates() {
            // given
            double radius = 1000;
            
            // when & then
            assertThat(TriggerUtils.isWithinRadius(null, 126.9780, 37.5665, 126.9780, radius)).isFalse();
            assertThat(TriggerUtils.isWithinRadius(37.5665, null, 37.5665, 126.9780, radius)).isFalse();
            assertThat(TriggerUtils.isWithinRadius(91.0, 126.9780, 37.5665, 126.9780, radius)).isFalse();
        }
    }
}