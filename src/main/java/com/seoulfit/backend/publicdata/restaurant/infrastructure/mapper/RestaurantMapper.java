package com.seoulfit.backend.publicdata.restaurant.infrastructure.mapper;

import com.seoulfit.backend.publicdata.restaurant.adapter.out.api.dto.TouristRestaurantApiResponse;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RestaurantMapper {

    public List<Restaurant> mapToEntity(List<TouristRestaurantApiResponse.RestaurantInfo> restaurantInfoList) {
        return restaurantInfoList.stream()
                .map(restaurantInfo -> {
                    // 지오코딩 없이 기본값 0.0 사용
                    // 추후 다른 방식으로 위경도 정보를 받아오거나
                    // API에서 직접 제공하는 경우 해당 값을 사용
                    double latitude = 0.0;
                    double longitude = 0.0;

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
