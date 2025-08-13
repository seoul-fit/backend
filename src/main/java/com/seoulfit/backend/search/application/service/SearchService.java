package com.seoulfit.backend.search.application.service;

import com.seoulfit.backend.search.adapter.in.web.dto.SearchIndexResponse;
import com.seoulfit.backend.search.adapter.in.web.dto.SearchResultResponse;
import com.seoulfit.backend.search.application.port.in.SearchUseCase;
import com.seoulfit.backend.search.application.port.out.PublicDataRepository;
import com.seoulfit.backend.search.application.port.out.SearchIndexRepository;
import com.seoulfit.backend.search.domain.PoiSearchIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchService implements SearchUseCase {
    
    private final SearchIndexRepository searchIndexRepository;
    private final PublicDataRepository publicDataRepository;
    
    public SearchService(SearchIndexRepository searchIndexRepository, PublicDataRepository publicDataRepository) {
        this.searchIndexRepository = searchIndexRepository;
        this.publicDataRepository = publicDataRepository;
    }
    
    @Override
    public SearchResultResponse searchIndex(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PoiSearchIndex> searchResults;
        
        if (keyword == null || keyword.trim().isEmpty()) {
            searchResults = searchIndexRepository.findAll(pageable);
        } else {
            searchResults = searchIndexRepository.searchByKeyword(keyword.trim(), pageable);
        }
        
        List<SearchIndexResponse> content = searchResults.getContent()
                .stream()
                .map(SearchIndexResponse::new)
                .collect(Collectors.toList());
        
        return new SearchResultResponse(
                content,
                searchResults.getNumber(),
                searchResults.getSize(),
                searchResults.getTotalElements(),
                searchResults.getTotalPages(),
                searchResults.hasNext(),
                searchResults.hasPrevious()
        );
    }
    
    @Override
    public Object getPublicDataByIndex(Long indexId) {
        PoiSearchIndex searchIndex = searchIndexRepository.findById(indexId)
                .orElse(null);
        
        if (searchIndex == null) {
            return null;
        }
        
        String refTable = searchIndex.getRefTable();
        Long refId = searchIndex.getRefId();
        
        switch (refTable.toLowerCase()) {
            case "cooling_centers":
                return publicDataRepository.findCoolingCenterById(refId).orElse(null);
            case "cultural_events":
                return publicDataRepository.findCulturalEventById(refId).orElse(null);
            case "cultural_reservation":
                return publicDataRepository.findCulturalReservationById(refId).orElse(null);
            case "libraries":
                return publicDataRepository.findLibraryById(refId).orElse(null);
            case "park":
                return publicDataRepository.findParkById(refId).orElse(null);
            case "restaurants":
                return publicDataRepository.findRestaurantById(refId).orElse(null);
            default:
                return null;
        }
    }
}