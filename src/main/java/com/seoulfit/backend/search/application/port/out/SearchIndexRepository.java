package com.seoulfit.backend.search.application.port.out;

import com.seoulfit.backend.search.domain.PoiSearchIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SearchIndexRepository {
    
    Page<PoiSearchIndex> searchByKeyword(String keyword, Pageable pageable);
    
    Page<PoiSearchIndex> findAll(Pageable pageable);
    
    Optional<PoiSearchIndex> findById(Long id);
    
    Optional<PoiSearchIndex> findByRefTableAndRefId(String refTable, Long refId);
    
    PoiSearchIndex save(PoiSearchIndex searchIndex);
    
    void deleteByRefTableAndRefId(String refTable, Long refId);
    
    void deleteAll();
}