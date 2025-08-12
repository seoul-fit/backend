package com.seoulfit.backend.publicdata.park.adapter.out.persistence.repository;

import com.seoulfit.backend.publicdata.park.domain.SeoulPark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공원 정보 JPA Repository
 */
public interface SeoulParkJpaRepository extends JpaRepository<SeoulPark, Long> {

    /**
     * 공원 번호와 데이터 날짜로 조회
     */
    Optional<SeoulPark> findByParkIdxAndDataDate(Long parkIdx, String dataDate);

    /**
     * 특정 날짜의 모든 공원 정보 조회
     */
    List<SeoulPark> findByDataDate(String dataDate);

    /**
     * 특정 지역의 공원 정보 조회
     */
    List<SeoulPark> findByZoneAndDataDate(String zone, String dataDate);

    /**
     * 공원명으로 검색 (부분 검색)
     */
    List<SeoulPark> findByParkNameContainingAndDataDate(String parkName, String dataDate);

    /**
     * 특정 날짜 이전의 데이터 삭제
     */
    @Modifying
    @Query("DELETE FROM SeoulPark sp WHERE sp.dataDate < :beforeDate")
    int deleteByDataDateBefore(@Param("beforeDate") String beforeDate);

    /**
     * 특정 날짜의 데이터 개수 조회
     */
    long countByDataDate(String dataDate);

    /**
     * 모든 데이터 날짜 목록 조회 (최신순)
     */
    @Query("SELECT DISTINCT sp.dataDate FROM SeoulPark sp ORDER BY sp.dataDate DESC")
    List<String> findAllDataDatesOrderByDesc();

    /**
     * 좌표 정보가 있는 공원 정보 조회
     */
    List<SeoulPark> findByDataDateAndWgs84LongitudeIsNotNullAndWgs84LatitudeIsNotNull(String dataDate);

    /**
     * 지역별 공원 개수 조회
     */
    @Query("SELECT sp.zone, COUNT(sp) FROM SeoulPark sp WHERE sp.dataDate = :dataDate GROUP BY sp.zone")
    List<Object[]> countByZoneAndDataDate(@Param("dataDate") String dataDate);
}
