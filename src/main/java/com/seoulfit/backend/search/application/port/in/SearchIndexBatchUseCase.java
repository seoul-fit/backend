package com.seoulfit.backend.search.application.port.in;

public interface SearchIndexBatchUseCase {
    
    int syncAllPublicDataToIndex();
    
    void syncCoolingCentersToIndex();
    
    void syncCulturalEventsToIndex();
    
    void syncCulturalReservationsToIndex();
    
    void syncLibrariesToIndex();
    
    void syncParksToIndex();
    
    void syncRestaurantsToIndex();
    
    void clearAllIndexData();
}
