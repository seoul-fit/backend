package com.seoulfit.backend.search.adapter.out.persistence;

import com.seoulfit.backend.search.domain.PoiSearchIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchIndexJpaRepository extends JpaRepository<PoiSearchIndex, Long> {
    
    @Query("SELECT p FROM PoiSearchIndex p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.remark) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.aliases) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<PoiSearchIndex> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    Optional<PoiSearchIndex> findByRefTableAndRefId(String refTable, Long refId);
    
    @Query("SELECT p FROM PoiSearchIndex p")
    Page<PoiSearchIndex> findAllWithPaging(Pageable pageable);
}