package com.seoulfit.backend.publicdata.restaurant.infrastructure.mapper;

import com.seoulfit.backend.publicdata.restaurant.adapter.out.api.dto.TouristRestaurantApiResponse;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import com.seoulfit.backend.publicdata.restaurant.infrastructure.util.GeoCodingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RestaurantMapper {
    private final GeoCodingUtil getCodingUtil;

    public List<Restaurant> mapToEntity(List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList) {
        return restaurantInfoList.stream()
                .map(restaurantInfo -> {
                    String addressToGeocode = restaurantInfo.getNewAddress() != null && !restaurantInfo.getNewAddress().isEmpty()
                            ? restaurantInfo.getNewAddress()
                            : restaurantInfo.getAddress();

                    // Geocoding 과정을 거쳐 위도, 경도를 얻어낸다.
                    Map<String, Double> geoData = getCodingUtil.getGeoData(addressToGeocode);
                    double latitude = geoData.getOrDefault("latitude", 0.0);
                    double longitude = geoData.getOrDefault("longitude", 0.0);

                    // 위도, 경도를 포함하여 Restaurant 객체 생성
                    return Restaurant.builder()
                            .postSn(restaurantInfo.getPostSn())
                            .langCodeId(restaurantInfo.getLangCodeId())
                            .name(restaurantInfo.getPostSj())
                            .postUrl(restaurantInfo.getPostUrl())
                            .address(restaurantInfo.getAddress())
                            .newAddress(restaurantInfo.getNewAddress())
                            .phone(restaurantInfo.getCmmnTelno())
                            .website(restaurantInfo.getCmmnHmpgUrl())
                            .operatingHours(restaurantInfo.getCmmnUseTime())
                            .subwayInfo(restaurantInfo.getSubwayInfo())
                            .homepageLang(restaurantInfo.getCmmnHmpgLang())
                            .representativeMenu(restaurantInfo.getFdReprsntMenu())
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
