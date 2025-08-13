package com.seoulfit.backend.search.application.port.out;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;

import java.util.List;

public interface PublicDataQueryPort {
    
    List<CoolingCenter> findAllCoolingCenters();
    
    List<CulturalEvent> findAllCulturalEvents();
    
    List<CulturalReservation> findAllCulturalReservations();
    
    List<Library> findAllLibraries();
    
    List<Park> findAllParks();
    
    List<Restaurant> findAllRestaurants();
}