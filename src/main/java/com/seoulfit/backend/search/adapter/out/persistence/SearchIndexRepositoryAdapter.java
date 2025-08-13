package com.seoulfit.backend.search.adapter.out.persistence;

import com.seoulfit.backend.search.application.port.out.SearchIndexRepository;
import com.seoulfit.backend.search.domain.PoiSearchIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SearchIndexRepositoryAdapter implements SearchIndexRepository {
    
    private final SearchIndexJpaRepository jpaRepository;
    
    public SearchIndexRepositoryAdapter(SearchIndexJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Page<PoiSearchIndex> searchByKeyword(String keyword, Pageable pageable) {
        return jpaRepository.findByKeyword(keyword, pageable);
    }
    
    @Override
    public Page<PoiSearchIndex> findAll(Pageable pageable) {
        return jpaRepository.findAllWithPaging(pageable);
    }
    
    @Override
    public Optional<PoiSearchIndex> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Optional<PoiSearchIndex> findByRefTableAndRefId(String refTable, Long refId) {
        return jpaRepository.findByRefTableAndRefId(refTable, refId);
    }
    
    @Override
    public PoiSearchIndex save(PoiSearchIndex searchIndex) {
        return jpaRepository.save(searchIndex);
    }
    
    @Override
    public void deleteByRefTableAndRefId(String refTable, Long refId) {
        jpaRepository.findByRefTableAndRefId(refTable, refId)
                     .ifPresent(jpaRepository::delete);
    }
    
    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}