package com.seoulfit.backend.trigger.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 트리거 시스템 공통 유틸리티 클래스
 * 
 * 트리거 전략들에서 공통으로 사용하는 로직을 제공합니다.
 * - 거리 계산 (Haversine 공식)
 * - 타입 안전한 데이터 파싱
 * - 좌표 유효성 검증
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class TriggerUtils {
    
    /**
     * 지구 반지름 (km)
     */
    private static final int EARTH_RADIUS = 6371;
    
    /**
     * Haversine 공식을 사용하여 두 지점 간 거리를 계산합니다.
     * 
     * @param lat1 첫 번째 지점의 위도
     * @param lng1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lng2 두 번째 지점의 경도
     * @return 두 지점 간 거리 (미터)
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // 위도와 경도 차이를 라디안으로 변환
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        // Haversine 공식 계산
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // 킬로미터를 미터로 변환하여 반환
        return EARTH_RADIUS * c * 1000;
    }
    
    /**
     * Map에서 String 값을 안전하게 추출합니다.
     * 
     * @param map 데이터 맵
     * @param key 키
     * @return String 값, null이면 null 반환
     */
    public static String getStringValue(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        
        return String.valueOf(value);
    }
    
    /**
     * Map에서 String 값을 안전하게 추출합니다. (기본값 지원)
     * 
     * @param map 데이터 맵
     * @param key 키
     * @param defaultValue 기본값
     * @return String 값, null이면 기본값 반환
     */
    public static String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        String value = getStringValue(map, key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Map에서 Double 값을 안전하게 추출합니다.
     * 
     * @param map 데이터 맵
     * @param key 키
     * @return Double 값, 변환 실패 시 null 반환
     */
    public static Double getDoubleValue(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                String strValue = ((String) value).trim();
                if (strValue.isEmpty()) {
                    return null;
                }
                return Double.parseDouble(strValue);
            }
        } catch (NumberFormatException e) {
            log.warn("숫자 변환 실패: key={}, value={}, error={}", key, value, e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Map에서 Double 값을 안전하게 추출합니다. (기본값 지원)
     * 
     * @param map 데이터 맵
     * @param key 키
     * @param defaultValue 기본값
     * @return Double 값, 변환 실패 시 기본값 반환
     */
    public static Double getDoubleValue(Map<String, Object> map, String key, Double defaultValue) {
        Double value = getDoubleValue(map, key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Map에서 Integer 값을 안전하게 추출합니다.
     * 
     * @param map 데이터 맵
     * @param key 키
     * @return Integer 값, 변환 실패 시 null 반환
     */
    public static Integer getIntValue(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }
        
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                String strValue = ((String) value).trim();
                if (strValue.isEmpty()) {
                    return null;
                }
                return Integer.parseInt(strValue);
            }
        } catch (NumberFormatException e) {
            log.warn("정수 변환 실패: key={}, value={}, error={}", key, value, e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Map에서 Integer 값을 안전하게 추출합니다. (기본값 지원)
     * 
     * @param map 데이터 맵
     * @param key 키
     * @param defaultValue 기본값
     * @return Integer 값, 변환 실패 시 기본값 반환
     */
    public static Integer getIntValue(Map<String, Object> map, String key, Integer defaultValue) {
        Integer value = getIntValue(map, key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * 위도 좌표의 유효성을 검증합니다.
     * 
     * @param latitude 위도
     * @return 유효성 여부
     */
    public static boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90.0 && latitude <= 90.0;
    }
    
    /**
     * 경도 좌표의 유효성을 검증합니다.
     * 
     * @param longitude 경도
     * @return 유효성 여부
     */
    public static boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180.0 && longitude <= 180.0;
    }
    
    /**
     * 좌표의 유효성을 검증합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @return 유효성 여부
     */
    public static boolean isValidCoordinate(Double latitude, Double longitude) {
        return isValidLatitude(latitude) && isValidLongitude(longitude);
    }
    
    /**
     * 문자열이 비어있지 않은지 확인합니다.
     * 
     * @param str 검사할 문자열
     * @return 비어있지 않으면 true
     */
    public static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * 두 문자열을 null 안전하게 비교합니다.
     * 
     * @param str1 첫 번째 문자열
     * @param str2 두 번째 문자열
     * @return 같으면 true
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }
    
    /**
     * 거리를 사람이 읽기 쉬운 형태로 포맷합니다.
     * 
     * @param distanceInMeters 미터 단위 거리
     * @return 포맷된 거리 문자열
     */
    public static String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            return String.format("%.0fm", distanceInMeters);
        } else {
            return String.format("%.1fkm", distanceInMeters / 1000);
        }
    }
    
    /**
     * 반경 내에 있는지 확인합니다.
     * 
     * @param userLat 사용자 위도
     * @param userLng 사용자 경도
     * @param targetLat 대상 위도
     * @param targetLng 대상 경도
     * @param radiusInMeters 반경 (미터)
     * @return 반경 내 여부
     */
    public static boolean isWithinRadius(Double userLat, Double userLng, Double targetLat, Double targetLng, double radiusInMeters) {
        if (!isValidCoordinate(userLat, userLng) || !isValidCoordinate(targetLat, targetLng)) {
            return false;
        }
        
        double distance = calculateDistance(userLat, userLng, targetLat, targetLng);
        return distance <= radiusInMeters;
    }
}