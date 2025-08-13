package com.seoulfit.backend.search.infrastructure.batch;

import com.seoulfit.backend.search.application.port.in.SearchIndexBatchUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PoiIndexSyncBatch {
    
    private static final Logger log = LoggerFactory.getLogger(PoiIndexSyncBatch.class);
    
    private final SearchIndexBatchUseCase searchIndexBatchUseCase;
    
    public PoiIndexSyncBatch(SearchIndexBatchUseCase searchIndexBatchUseCase) {
        this.searchIndexBatchUseCase = searchIndexBatchUseCase;
    }
    
    @Scheduled(cron = "${seoul-fit.scheduler.search.poi-index.cron:0 0 8 * * *}")
    public void syncPoiIndexBatch() {
        log.info("Starting POI index sync batch job");
        
        try {
            searchIndexBatchUseCase.syncAllPublicDataToIndex();
            log.info("POI index sync batch job completed successfully");
        } catch (Exception e) {
            log.error("Error occurred during POI index sync batch job", e);
        }
    }
    
    @Scheduled(cron = "${seoul-fit.scheduler.search.cooling-centers.cron:0 10 8 * * *}")
    public void syncCoolingCentersBatch() {
        log.info("Starting cooling centers sync to POI index");
        
        try {
            searchIndexBatchUseCase.syncCoolingCentersToIndex();
            log.info("Cooling centers sync to POI index completed successfully");
        } catch (Exception e) {
            log.error("Error occurred during cooling centers sync to POI index", e);
        }
    }
    
    @Scheduled(cron = "${seoul-fit.scheduler.search.cultural-events.cron:0 15 8 * * *}")
    public void syncCulturalEventsBatch() {
        log.info("Starting cultural events sync to POI index");
        
        try {
            searchIndexBatchUseCase.syncCulturalEventsToIndex();
            log.info("Cultural events sync to POI index completed successfully");
        } catch (Exception e) {
            log.error("Error occurred during cultural events sync to POI index", e);
        }
    }
    
    @Scheduled(cron = "${seoul-fit.scheduler.search.libraries.cron:0 20 8 * * *}")
    public void syncLibrariesBatch() {
        log.info("Starting libraries sync to POI index");
        
        try {
            searchIndexBatchUseCase.syncLibrariesToIndex();
            log.info("Libraries sync to POI index completed successfully");
        } catch (Exception e) {
            log.error("Error occurred during libraries sync to POI index", e);
        }
    }
    
    @Scheduled(cron = "${seoul-fit.scheduler.search.parks.cron:0 25 8 * * *}")
    public void syncParksBatch() {
        log.info("Starting parks sync to POI index");
        
        try {
            searchIndexBatchUseCase.syncParksToIndex();
            log.info("Parks sync to POI index completed successfully");
        } catch (Exception e) {
            log.error("Error occurred during parks sync to POI index", e);
        }
    }
    
    @Scheduled(cron = "${seoul-fit.scheduler.search.restaurants.cron:0 30 8 * * *}")
    public void syncRestaurantsBatch() {
        log.info("Starting restaurants sync to POI index");
        
        try {
            searchIndexBatchUseCase.syncRestaurantsToIndex();
            log.info("Restaurants sync to POI index completed successfully");
        } catch (Exception e) {
            log.error("Error occurred during restaurants sync to POI index", e);
        }
    }
}