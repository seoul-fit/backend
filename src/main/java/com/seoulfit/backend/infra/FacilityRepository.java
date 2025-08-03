package com.seoulfit.backend.infra;

import com.seoulfit.backend.domain.Facility;
import com.seoulfit.backend.user.domain.InterestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    @Query("SELECT f FROM Facility f WHERE f.category IN :categories")
    List<Facility> findByCategoryIn(@Param("categories") List<InterestCategory> categories);

    @Query("""
        SELECT f FROM Facility f 
        WHERE f.category IN :categories
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(f.latitude)) 
            * cos(radians(f.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(f.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(f.latitude)) 
            * cos(radians(f.longitude) - radians(:longitude)) 
            + sin(radians(:latitude)) * sin(radians(f.latitude))))
        """)
    List<Facility> findNearbyFacilities(@Param("latitude") BigDecimal latitude,
                                       @Param("longitude") BigDecimal longitude,
                                       @Param("radiusKm") double radiusKm,
                                       @Param("categories") List<InterestCategory> categories);

    @Query("SELECT f FROM Facility f JOIN FETCH f.amenities WHERE f.id = :facilityId")
    Optional<Facility> findByIdWithAmenities(@Param("facilityId") Long facilityId);

    List<Facility> findByDistrict(String district);

    Optional<Facility> findByExternalIdAndDataSource(String externalId, String dataSource);
}
