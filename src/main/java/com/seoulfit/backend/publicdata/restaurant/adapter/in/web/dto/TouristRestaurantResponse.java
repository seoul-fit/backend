package com.seoulfit.backend.publicdata.restaurant.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.restaurant.domain.TouristRestaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 관광 음식점 정보 API 응답 DTO
 */
@Getter
@Builder
@Schema(description = "관광 음식점 정보 응답")
public class TouristRestaurantResponse {

    @Schema(description = "음식점 ID", example = "1")
    private Long id;

    @Schema(description = "고유번호", example = "POST001")
    private String postSn;

    @Schema(description = "언어코드", example = "ko")
    private String langCodeId;

    @Schema(description = "상호명", example = "서울 한식당")
    private String restaurantName;

    @Schema(description = "콘텐츠URL", example = "https://example.com/restaurant")
    private String postUrl;

    @Schema(description = "주소", example = "서울특별시 중구 명동길 123")
    private String address;

    @Schema(description = "신주소", example = "서울특별시 중구 명동길 123")
    private String newAddress;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String phoneNumber;

    @Schema(description = "웹사이트", example = "https://restaurant.com")
    private String websiteUrl;

    @Schema(description = "운영시간", example = "11:00~22:00")
    private String operatingHours;

    @Schema(description = "교통정보", example = "지하철 2호선 을지로입구역 3번 출구")
    private String subwayInfo;

    @Schema(description = "홈페이지 언어", example = "한국어")
    private String homepageLanguage;

    @Schema(description = "대표메뉴", example = "김치찌개, 불고기")
    private String representativeMenu;

    @Schema(description = "데이터 수집 날짜", example = "20250812")
    private String dataDate;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * TouristRestaurant 도메인 객체를 TouristRestaurantResponse DTO로 변환
     */
    public static TouristRestaurantResponse from(TouristRestaurant restaurant) {
        return TouristRestaurantResponse.builder()
                .id(restaurant.getId())
                .postSn(restaurant.getPostSn())
                .langCodeId(restaurant.getLangCodeId())
                .restaurantName(restaurant.getRestaurantName())
                .postUrl(restaurant.getPostUrl())
                .address(restaurant.getAddress())
                .newAddress(restaurant.getNewAddress())
                .phoneNumber(restaurant.getPhoneNumber())
                .websiteUrl(restaurant.getWebsiteUrl())
                .operatingHours(restaurant.getOperatingHours())
                .subwayInfo(restaurant.getSubwayInfo())
                .homepageLanguage(restaurant.getHomepageLanguage())
                .representativeMenu(restaurant.getRepresentativeMenu())
                .dataDate(restaurant.getDataDate())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }

    /**
     * TouristRestaurant 리스트를 TouristRestaurantResponse 리스트로 변환
     */
    public static List<TouristRestaurantResponse> from(List<TouristRestaurant> restaurants) {
        return restaurants.stream()
                .map(TouristRestaurantResponse::from)
                .collect(Collectors.toList());
    }
}
