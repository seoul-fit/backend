package com.seoulfit.backend.search.adapter.out.persistence;

import com.seoulfit.backend.publicdata.culture.adapter.out.custom.CulturalEventRepository;
import com.seoulfit.backend.publicdata.culture.adapter.out.repository.CulturalReservationRepository;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.CoolingCenterRepository;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.LibraryRepository;
import com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import com.seoulfit.backend.publicdata.park.adapter.out.persistence.repository.ParkRepository;
import com.seoulfit.backend.publicdata.park.domain.Park;
import com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence.repository.RestaurantRepository;
import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import com.seoulfit.backend.search.application.port.out.PublicDataQueryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublicDataQueryAdapter implements PublicDataQueryPort {
    
    private final CoolingCenterRepository coolingCenterRepository;
    private final CulturalEventRepository culturalEventRepository;
    private final CulturalReservationRepository culturalReservationRepository;
    private final LibraryRepository libraryRepository;
    private final ParkRepository parkRepository;
    private final RestaurantRepository restaurantRepository;
    
    public PublicDataQueryAdapter(
            CoolingCenterRepository coolingCenterRepository,
            CulturalEventRepository culturalEventRepository,
            CulturalReservationRepository culturalReservationRepository,
            LibraryRepository libraryRepository,
            ParkRepository parkRepository,
            RestaurantRepository restaurantRepository) {
        this.coolingCenterRepository = coolingCenterRepository;
        this.culturalEventRepository = culturalEventRepository;
        this.culturalReservationRepository = culturalReservationRepository;
        this.libraryRepository = libraryRepository;
        this.parkRepository = parkRepository;
        this.restaurantRepository = restaurantRepository;
    }
    
    @Override
    public List<CoolingCenter> findAllCoolingCenters() {
        return coolingCenterRepository.findAll();
    }
    
    @Override
    public List<CulturalEvent> findAllCulturalEvents() {
        return culturalEventRepository.findAll();
    }
    
    @Override
    public List<CulturalReservation> findAllCulturalReservations() {
        return culturalReservationRepository.findAll();
    }
    
    @Override
    public List<Library> findAllLibraries() {
        return libraryRepository.findAll();
    }
    
    @Override
    public List<Park> findAllParks() {
        return parkRepository.findAll();
    }
    
    @Override
    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }
}