package com.seoulfit.backend.search.application.port.out;

import java.util.Optional;

public interface PublicDataRepository {
    
    Optional<Object> findCoolingCenterById(Long id);
    
    Optional<Object> findCulturalEventById(Long id);
    
    Optional<Object> findCulturalReservationById(Long id);
    
    Optional<Object> findLibraryById(Long id);
    
    Optional<Object> findParkById(Long id);
    
    Optional<Object> findRestaurantById(Long id);
}