package com.seoulfit.backend.search.application.service;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import com.seoulfit.backend.publicdata.culture.domain.CulturalReservation;
import com.seoulfit.backend.search.application.port.in.SearchIndexBatchUseCase;
import com.seoulfit.backend.search.application.port.out.PublicDataQueryPort;
import com.seoulfit.backend.search.application.port.out.SearchIndexRepository;
import com.seoulfit.backend.search.domain.PoiSearchIndex;
import com.seoulfit.backend.search.infrastructure.mapper.PoiIndexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchIndexBatchService implements SearchIndexBatchUseCase {
    
    private static final Logger log = LoggerFactory.getLogger(SearchIndexBatchService.class);
    
    private final SearchIndexRepository searchIndexRepository;
    private final PublicDataQueryPort publicDataQueryPort;
    
    public SearchIndexBatchService(SearchIndexRepository searchIndexRepository, PublicDataQueryPort publicDataQueryPort) {
        this.searchIndexRepository = searchIndexRepository;
        this.publicDataQueryPort = publicDataQueryPort;
    }
    
    @Override
    @Transactional
    public void syncAllPublicDataToIndex() {
        log.info("Starting full POI index synchronization");
        
        // Clear existing data first
        clearAllIndexDataInternal();
        
        // Sync each data type
        syncCoolingCentersInternal();
        syncCulturalEventsInternal();
        syncCulturalReservationsInternal();
        syncLibrariesInternal();
        syncParksInternal();
        syncRestaurantsInternal();
        
        log.info("Completed full POI index synchronization");
    }
    
    @Override
    @Transactional
    public void syncCoolingCentersToIndex() {
        syncCoolingCentersInternal();
    }
    
    private void syncCoolingCentersInternal() {
        log.info("Syncing cooling centers to POI index");
        
        List<com.seoulfit.backend.publicdata.facilities.domain.CoolingCenter> coolingCenters = publicDataQueryPort.findAllCoolingCenters();
        log.info("Found {} cooling centers in database", coolingCenters.size());
        
        List<PoiSearchIndex> indexList = coolingCenters
                .stream()
                .map(PoiIndexMapper::fromCoolingCenter)
                .collect(Collectors.toList());
        
        log.info("Mapped {} cooling centers to index entries", indexList.size());
        indexList.forEach(index -> {
            log.debug("Saving index entry: name={}, refTable={}, refId={}", 
                     index.getName(), index.getRefTable(), index.getRefId());
            searchIndexRepository.save(index);
        });
        log.info("Synced {} cooling centers to POI index", indexList.size());
    }
    
    @Override
    @Transactional
    public void syncCulturalEventsToIndex() {
        syncCulturalEventsInternal();
    }
    
    private void syncCulturalEventsInternal() {
        log.info("Syncing cultural events to POI index");
        
        List<CulturalEvent> culturalEvents = publicDataQueryPort.findAllCulturalEvents();
        log.info("Found {} cultural events in database", culturalEvents.size());
        
        List<PoiSearchIndex> indexList = culturalEvents
                .stream()
                .map(PoiIndexMapper::fromCulturalEvent)
                .collect(Collectors.toList());
        
        log.info("Mapped {} cultural events to index entries", indexList.size());
        indexList.forEach(searchIndexRepository::save);
        log.info("Synced {} cultural events to POI index", indexList.size());
    }
    
    @Override
    @Transactional
    public void syncCulturalReservationsToIndex() {
        syncCulturalReservationsInternal();
    }
    
    private void syncCulturalReservationsInternal() {
        log.info("Syncing cultural reservations to POI index");
        
        List<CulturalReservation> culturalReservations = publicDataQueryPort.findAllCulturalReservations();
        log.info("Found {} cultural reservations in database", culturalReservations.size());
        
        List<PoiSearchIndex> indexList = culturalReservations
                .stream()
                .map(PoiIndexMapper::fromCulturalReservation)
                .collect(Collectors.toList());
        
        log.info("Mapped {} cultural reservations to index entries", indexList.size());
        indexList.forEach(searchIndexRepository::save);
        log.info("Synced {} cultural reservations to POI index", indexList.size());
    }
    
    @Override
    @Transactional
    public void syncLibrariesToIndex() {
        syncLibrariesInternal();
    }
    
    private void syncLibrariesInternal() {
        log.info("Syncing libraries to POI index");
        
        List<com.seoulfit.backend.publicdata.facilities.domain.Library> libraries = publicDataQueryPort.findAllLibraries();
        log.info("Found {} libraries in database", libraries.size());
        
        List<PoiSearchIndex> indexList = libraries
                .stream()
                .map(PoiIndexMapper::fromLibrary)
                .collect(Collectors.toList());
        
        log.info("Mapped {} libraries to index entries", indexList.size());
        indexList.forEach(searchIndexRepository::save);
        log.info("Synced {} libraries to POI index", indexList.size());
    }
    
    @Override
    @Transactional
    public void syncParksToIndex() {
        syncParksInternal();
    }
    
    private void syncParksInternal() {
        log.info("Syncing parks to POI index");
        
        List<com.seoulfit.backend.publicdata.park.domain.Park> parks = publicDataQueryPort.findAllParks();
        log.info("Found {} parks in database", parks.size());
        
        List<PoiSearchIndex> indexList = parks
                .stream()
                .map(PoiIndexMapper::fromPark)
                .collect(Collectors.toList());
        
        log.info("Mapped {} parks to index entries", indexList.size());
        indexList.forEach(searchIndexRepository::save);
        log.info("Synced {} parks to POI index", indexList.size());
    }
    
    @Override
    @Transactional
    public void syncRestaurantsToIndex() {
        syncRestaurantsInternal();
    }
    
    private void syncRestaurantsInternal() {
        log.info("Syncing restaurants to POI index");
        
        List<com.seoulfit.backend.publicdata.restaurant.domain.Restaurant> restaurants = publicDataQueryPort.findAllRestaurants();
        log.info("Found {} restaurants in database", restaurants.size());
        
        List<PoiSearchIndex> indexList = restaurants
                .stream()
                .map(PoiIndexMapper::fromRestaurant)
                .collect(Collectors.toList());
        
        log.info("Mapped {} restaurants to index entries", indexList.size());
        indexList.forEach(searchIndexRepository::save);
        log.info("Synced {} restaurants to POI index", indexList.size());
    }
    
    @Override
    @Transactional
    public void clearAllIndexData() {
        clearAllIndexDataInternal();
    }
    
    private void clearAllIndexDataInternal() {
        log.info("Clearing all POI index data");
        
        searchIndexRepository.deleteAll();
        
        log.info("Cleared all POI index data entries");
    }
}