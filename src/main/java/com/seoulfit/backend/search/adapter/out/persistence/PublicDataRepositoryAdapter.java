package com.seoulfit.backend.search.adapter.out.persistence;

import com.seoulfit.backend.publicdata.culture.adapter.out.repository.CulturalReservationRepository;
import com.seoulfit.backend.publicdata.culture.adapter.out.repository.CulturalSpaceRepository;
import com.seoulfit.backend.publicdata.culture.adapter.out.custom.CulturalEventRepository;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.CoolingCenterRepository;
import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.LibraryRepository;
import com.seoulfit.backend.publicdata.park.adapter.out.persistence.repository.ParkRepository;
import com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence.repository.RestaurantRepository;
import com.seoulfit.backend.search.application.port.out.PublicDataRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PublicDataRepositoryAdapter implements PublicDataRepository {
    
    private final CoolingCenterRepository coolingCenterRepository;
    private final CulturalEventRepository culturalEventRepository;
    private final CulturalReservationRepository culturalReservationRepository;
    private final LibraryRepository libraryRepository;
    private final ParkRepository parkRepository;
    private final RestaurantRepository restaurantRepository;
    
    public PublicDataRepositoryAdapter(
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
    public Optional<Object> findCoolingCenterById(Long id) {
        return coolingCenterRepository.findById(id).map(Object.class::cast);
    }
    
    @Override
    public Optional<Object> findCulturalEventById(Long id) {
        return culturalEventRepository.findById(id).map(Object.class::cast);
    }
    
    @Override
    public Optional<Object> findCulturalReservationById(Long id) {
        return culturalReservationRepository.findById(id).map(Object.class::cast);
    }
    
    @Override
    public Optional<Object> findLibraryById(Long id) {
        return libraryRepository.findById(id).map(Object.class::cast);
    }
    
    @Override
    public Optional<Object> findParkById(Long id) {
        return parkRepository.findById(id).map(Object.class::cast);
    }
    
    @Override
    public Optional<Object> findRestaurantById(Long id) {
        return restaurantRepository.findById(id).map(Object.class::cast);
    }
}