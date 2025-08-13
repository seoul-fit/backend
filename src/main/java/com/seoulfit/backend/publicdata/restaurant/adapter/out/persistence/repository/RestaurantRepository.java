package com.seoulfit.backend.publicdata.restaurant.adapter.out.persistence.repository;

import com.seoulfit.backend.publicdata.restaurant.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 맛집 레포지토리
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * 위치 기반 맛집 조회 (반경 내)
     */
    @Query(value = """
        SELECT * FROM restaurants r
        WHERE r.latitude IS NOT NULL 
        AND r.longitude IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) 
            * cos(radians(r.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(r.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) 
            * cos(radians(r.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(r.latitude))))
        """, nativeQuery = true)
    List<Restaurant> findByLocationWithinRadius(@Param("latitude") Double latitude, 
                                              @Param("longitude") Double longitude, 
                                              @Param("radiusKm") Double radiusKm);

    /**
     * 맛집명으로 검색
     */
    List<Restaurant> findByNameContaining(String name);

    /**
     * 대표메뉴로 검색
     */
    List<Restaurant> findByRepresentativeMenuContaining(String menu);

    /**
     * 바운딩 박스 기반 맛집 조회 (성능 최적화)
     */
    @Query("SELECT r FROM Restaurant r WHERE r.latitude BETWEEN :minLat AND :maxLat " +
           "AND r.longitude BETWEEN :minLng AND :maxLng " +
           "AND r.latitude IS NOT NULL AND r.longitude IS NOT NULL")
    List<Restaurant> findByBoundingBox(@Param("minLat") Double minLat, @Param("maxLat") Double maxLat,
                                      @Param("minLng") Double minLng, @Param("maxLng") Double maxLng);

    /**
     * 위치 정보가 있는 맛집만 조회
     */
    @Query("SELECT r FROM Restaurant r WHERE r.latitude IS NOT NULL AND r.longitude IS NOT NULL")
    List<Restaurant> findAllWithLocation();

    /**
     * 언어별 맛집 조회
     */
    List<Restaurant> findByLangCodeId(String langCodeId);
}
