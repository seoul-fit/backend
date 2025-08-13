package com.seoulfit.backend.search.infrastructure.mapper;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import com.seoulfit.backend.search.domain.PoiSearchIndex;

public class PoiIndexMapper {
    
    public static PoiSearchIndex fromCoolingCenter(CoolingCenter coolingCenter) {
        String address = joinWithComma(coolingCenter.getLotAddress(), coolingCenter.getRoadAddress());
        String remark = joinWithComma(coolingCenter.getFacilityType1(), coolingCenter.getFacilityType2());
        
        return new PoiSearchIndex(
            coolingCenter.getName(),
            address,
            remark,
            null,
            "cooling_centers",
            coolingCenter.getId()
        );
    }
    
    public static PoiSearchIndex fromCulturalEvent(CulturalEvent event) {
        return new PoiSearchIndex(
            event.getTitle(),
            null,
            event.getPlace(),
            null,
            "cultural_events",
            event.getId()
        );
    }
    
    public static PoiSearchIndex fromCulturalReservation(CulturalReservation reservation) {
        return new PoiSearchIndex(
            reservation.getSvcNm(),
            null,
            reservation.getPlaceNm(),
            null,
            "cultural_reservation",
            reservation.getId()
        );
    }
    
    public static PoiSearchIndex fromLibrary(Library library) {
        return new PoiSearchIndex(
            library.getLbrryName(),
            library.getAdres(),
            null,
            null,
            "libraries",
            library.getId()
        );
    }
    
    public static PoiSearchIndex fromPark(Park park) {
        return new PoiSearchIndex(
            park.getName(),
            park.getAddress(),
            null,
            null,
            "park",
            park.getId()
        );
    }
    
    public static PoiSearchIndex fromRestaurant(Restaurant restaurant) {
        String address = joinWithComma(restaurant.getAddress(), restaurant.getNewAddress());
        
        return new PoiSearchIndex(
            restaurant.getName(),
            address,
            null,
            null,
            "restaurants",
            restaurant.getId()
        );
    }
    
    private static String joinWithComma(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return null;
        }
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        if (str1.equals(str2)) {
            return str1;
        }
        return str1 + "," + str2;
    }
}