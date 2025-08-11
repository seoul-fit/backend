package com.seoulfit.backend.common;

import com.seoulfit.backend.notification.domain.NotificationHistory;
import com.seoulfit.backend.notification.domain.NotificationType;
import com.seoulfit.backend.trigger.domain.TriggerCondition;
import com.seoulfit.backend.trigger.dto.TriggerContext;
import com.seoulfit.backend.trigger.dto.TriggerResult;
import com.seoulfit.backend.user.domain.AuthProvider;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 테스트용 데이터 팩토리 클래스입니다.
 * 
 * <p>테스트에서 사용할 다양한 도메인 객체들을 생성하는 유틸리티 메서드를 제공합니다.
 * 테스트 코드의 가독성을 높이고 중복을 줄이기 위해 사용됩니다.</p>
 * 
 * @author Seoul Fit
 * @since 2025-01-01
 */
public class TestDataFactory {

    // ===== User 관련 팩토리 메서드 =====

    /**
     * 기본 일반 사용자를 생성합니다.
     * 
     * @return 생성된 일반 사용자
     */
    public static User createDefaultLocalUser() {
        return User.createLocalUser(
                "test@example.com",
                "encodedPassword123",
                "테스트사용자"
        );
    }

    /**
     * 커스텀 정보로 일반 사용자를 생성합니다.
     * 
     * @param email 이메일
     * @param nickname 닉네임
     * @return 생성된 일반 사용자
     */
    public static User createLocalUser(String email, String nickname) {
        return User.createLocalUser(email, "encodedPassword123", nickname);
    }

    /**
     * 기본 OAuth 사용자를 생성합니다.
     * 
     * @return 생성된 OAuth 사용자
     */
    public static User createDefaultOAuthUser() {
        return User.createOAuthUser(
                AuthProvider.KAKAO,
                "kakao123456",
                "카카오사용자",
                "kakao@example.com",
                "https://example.com/profile.jpg"
        );
    }

    /**
     * 커스텀 정보로 OAuth 사용자를 생성합니다.
     * 
     * @param provider OAuth 제공자
     * @param oauthUserId OAuth 사용자 ID
     * @param nickname 닉네임
     * @return 생성된 OAuth 사용자
     */
    public static User createOAuthUser(AuthProvider provider, String oauthUserId, String nickname) {
        return User.createOAuthUser(
                provider,
                oauthUserId,
                nickname,
                provider.name().toLowerCase() + "@example.com",
                "https://example.com/profile.jpg"
        );
    }

    /**
     * 관심사가 있는 사용자를 생성합니다.
     * 
     * @param interests 관심사 목록
     * @return 관심사가 설정된 사용자
     */
    public static User createUserWithInterests(InterestCategory... interests) {
        User user = createDefaultLocalUser();
        for (InterestCategory interest : interests) {
            user.addInterest(interest);
        }
        return user;
    }

    /**
     * 위치 정보가 있는 사용자를 생성합니다.
     * 
     * @param latitude 위도
     * @param longitude 경도
     * @param address 주소
     * @return 위치 정보가 설정된 사용자
     */
    public static User createUserWithLocation(Double latitude, Double longitude, String address) {
        User user = createDefaultLocalUser();
        user.updateLocation(latitude, longitude, address);
        return user;
    }

    // ===== NotificationHistory 관련 팩토리 메서드 =====

    /**
     * 기본 알림 히스토리를 생성합니다.
     * 
     * @param userId 사용자 ID
     * @return 생성된 알림 히스토리
     */
    public static NotificationHistory createDefaultNotificationHistory(Long userId) {
        return NotificationHistory.create(
                userId,
                NotificationType.WEATHER,
                "폭염 주의보",
                "현재 기온이 35도를 넘었습니다. 외출 시 주의하세요.",
                TriggerCondition.TEMPERATURE_HIGH,
                "서울시 중구"
        );
    }

    /**
     * 커스텀 정보로 알림 히스토리를 생성합니다.
     * 
     * @param userId 사용자 ID
     * @param type 알림 타입
     * @param title 제목
     * @param message 메시지
     * @param condition 트리거 조건
     * @return 생성된 알림 히스토리
     */
    public static NotificationHistory createNotificationHistory(
            Long userId, NotificationType type, String title, String message, TriggerCondition condition) {
        return NotificationHistory.create(userId, type, title, message, condition, "서울시");
    }

    /**
     * 읽은 상태의 알림 히스토리를 생성합니다.
     * 
     * @param userId 사용자 ID
     * @return 읽은 상태의 알림 히스토리
     */
    public static NotificationHistory createReadNotificationHistory(Long userId) {
        NotificationHistory history = createDefaultNotificationHistory(userId);
        history.markAsRead();
        return history;
    }

    // ===== TriggerContext 관련 팩토리 메서드 =====

    /**
     * 기본 트리거 컨텍스트를 생성합니다.
     * 
     * @param user 사용자
     * @return 생성된 트리거 컨텍스트
     */
    public static TriggerContext createDefaultTriggerContext(User user) {
        return TriggerContext.builder()
                .user(user)
                .userInterests(List.of(InterestCategory.WEATHER))
                .userLatitude(37.5665)
                .userLongitude(126.9780)
                .publicApiData(createDefaultPublicApiData())
                .build();
    }

    /**
     * 커스텀 공공 API 데이터로 트리거 컨텍스트를 생성합니다.
     * 
     * @param user 사용자
     * @param publicApiData 공공 API 데이터
     * @return 생성된 트리거 컨텍스트
     */
    public static TriggerContext createTriggerContext(User user, Map<String, Object> publicApiData) {
        return TriggerContext.builder()
                .user(user)
                .userInterests(user.getInterestCategories())
                .userLatitude(37.5665)
                .userLongitude(126.9780)
                .publicApiData(publicApiData)
                .build();
    }

    // ===== TriggerResult 관련 팩토리 메서드 =====

    /**
     * 발동된 트리거 결과를 생성합니다.
     * 
     * @param type 알림 타입
     * @param condition 트리거 조건
     * @param title 제목
     * @param message 메시지
     * @return 생성된 트리거 결과
     */
    public static TriggerResult createTriggeredResult(
            NotificationType type, TriggerCondition condition, String title, String message) {
        return TriggerResult.builder()
                .triggered(true)
                .notificationType(type)
                .triggerCondition(condition)
                .title(title)
                .message(message)
                .locationInfo("서울시")
                .priority(10)
                .build();
    }

    /**
     * 발동되지 않은 트리거 결과를 생성합니다.
     * 
     * @return 발동되지 않은 트리거 결과
     */
    public static TriggerResult createNotTriggeredResult() {
        return TriggerResult.notTriggered();
    }

    /**
     * 고온 트리거 결과를 생성합니다.
     * 
     * @param temperature 온도
     * @return 고온 트리거 결과
     */
    public static TriggerResult createHighTemperatureTriggerResult(double temperature) {
        return createTriggeredResult(
                NotificationType.WEATHER,
                TriggerCondition.TEMPERATURE_HIGH,
                "폭염 주의보",
                String.format("현재 기온이 %.1f°C입니다. 외출 시 충분한 수분 섭취와 그늘에서 휴식을 취하세요.", temperature)
        );
    }

    // ===== 공공 API 데이터 관련 팩토리 메서드 =====

    /**
     * 기본 공공 API 데이터를 생성합니다.
     * 
     * @return 생성된 공공 API 데이터
     */
    public static Map<String, Object> createDefaultPublicApiData() {
        return Map.of(
                "WEATHER_STTS", List.of(
                        Map.of(
                                "TEMP", "25.0",
                                "HUMIDITY", "60",
                                "WIND_SPD", "2.5"
                        )
                ),
                "AIR_QUALITY", List.of(
                        Map.of(
                                "PM10", "30",
                                "PM25", "15",
                                "O3", "0.05"
                        )
                )
        );
    }

    /**
     * 고온 날씨 데이터를 생성합니다.
     * 
     * @param temperature 온도
     * @return 고온 날씨 데이터
     */
    public static Map<String, Object> createHighTemperatureData(double temperature) {
        return Map.of(
                "WEATHER_STTS", List.of(
                        Map.of(
                                "TEMP", String.valueOf(temperature),
                                "HUMIDITY", "70",
                                "WIND_SPD", "1.0"
                        )
                )
        );
    }

    /**
     * 저온 날씨 데이터를 생성합니다.
     * 
     * @param temperature 온도
     * @return 저온 날씨 데이터
     */
    public static Map<String, Object> createLowTemperatureData(double temperature) {
        return Map.of(
                "WEATHER_STTS", List.of(
                        Map.of(
                                "TEMP", String.valueOf(temperature),
                                "HUMIDITY", "40",
                                "WIND_SPD", "3.0"
                        )
                )
        );
    }

    /**
     * 나쁜 대기질 데이터를 생성합니다.
     * 
     * @param pm10 미세먼지 농도
     * @param pm25 초미세먼지 농도
     * @return 나쁜 대기질 데이터
     */
    public static Map<String, Object> createBadAirQualityData(int pm10, int pm25) {
        return Map.of(
                "RealtimeCityAir", Map.of(
                        "row", List.of(
                                Map.of(
                                        "PM10", String.valueOf(pm10),
                                        "PM25", String.valueOf(pm25),
                                        "MSRRGN_NM", "중구"
                                )
                        )
                )
        );
    }

    /**
     * 따릉이 데이터를 생성합니다.
     * 
     * @param availableBikes 이용 가능한 자전거 수
     * @param totalRacks 총 거치대 수
     * @return 따릉이 데이터
     */
    public static Map<String, Object> createBikeShareData(int availableBikes, int totalRacks) {
        return Map.of(
                "rentBikeStatus", Map.of(
                        "row", List.of(
                                Map.of(
                                        "stationName", "102. 여의도공원",
                                        "parkingBikeTotCnt", String.valueOf(availableBikes),
                                        "rackTotCnt", String.valueOf(totalRacks),
                                        "shared", String.valueOf(totalRacks - availableBikes)
                                )
                        )
                )
        );
    }

    // ===== 시간 관련 유틸리티 메서드 =====

    /**
     * 현재 시간을 기준으로 과거 시간을 생성합니다.
     * 
     * @param hoursAgo 몇 시간 전
     * @return 과거 시간
     */
    public static LocalDateTime hoursAgo(int hoursAgo) {
        return LocalDateTime.now().minusHours(hoursAgo);
    }

    /**
     * 현재 시간을 기준으로 미래 시간을 생성합니다.
     * 
     * @param hoursLater 몇 시간 후
     * @return 미래 시간
     */
    public static LocalDateTime hoursLater(int hoursLater) {
        return LocalDateTime.now().plusHours(hoursLater);
    }

    // ===== 위치 관련 상수 =====

    /**
     * 서울 시청 위도
     */
    public static final double SEOUL_CITY_HALL_LATITUDE = 37.5665;

    /**
     * 서울 시청 경도
     */
    public static final double SEOUL_CITY_HALL_LONGITUDE = 126.9780;

    /**
     * 광화문 위도
     */
    public static final double GWANGHWAMUN_LATITUDE = 37.5701416811;

    /**
     * 광화문 경도
     */
    public static final double GWANGHWAMUN_LONGITUDE = 126.9763534416;
}
