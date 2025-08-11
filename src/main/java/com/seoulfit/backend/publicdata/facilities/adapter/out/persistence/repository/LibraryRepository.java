package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository;

import com.seoulfit.backend.publicdata.facilities.domain.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 도서관 레포지토리
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {

    /**
     * 위치 기반 도서관 조회 (반경 내)
     */
    @Query(value = """
        SELECT * FROM libraries l
        WHERE l.xcnts IS NOT NULL 
        AND l.ydnts IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(l.xcnts)) 
            * cos(radians(l.ydnts) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(l.xcnts)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(l.xcnts)) 
            * cos(radians(l.ydnts) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(l.xcnts))))
        """, nativeQuery = true)
    List<Library> findByLocationWithinRadius(@Param("latitude") Double latitude, 
                                            @Param("longitude") Double longitude, 
                                            @Param("radiusKm") Double radiusKm);

    /**
     * 구별 도서관 조회
     */
    List<Library> findByCodeValueContaining(String codeValue);

    /**
     * 도서관명으로 검색
     */
    List<Library> findByLbrryNameContaining(String lbrryName);

    /**
     * 도서관 구분별 조회
     */
    List<Library> findByLbrrySeName(String lbrrySeName);

    /**
     * 바운딩 박스 기반 도서관 조회 (성능 최적화)
     */
    @Query("SELECT l FROM Library l WHERE l.xcnts BETWEEN :minLat AND :maxLat " +
           "AND l.ydnts BETWEEN :minLng AND :maxLng " +
           "AND l.xcnts IS NOT NULL AND l.ydnts IS NOT NULL")
    List<Library> findByBoundingBox(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                   @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

    /**
     * 위치 정보가 있는 도서관만 조회
     */
    @Query("SELECT l FROM Library l WHERE l.xcnts IS NOT NULL AND l.ydnts IS NOT NULL")
    List<Library> findAllWithLocation();
}
