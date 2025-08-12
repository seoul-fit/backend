package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository;

import com.seoulfit.backend.publicdata.facilities.domain.SportsFacilityProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 서울시 공공체육시설 프로그램 정보 JPA Repository
 */
public interface SportsFacilityProgramJpaRepository extends JpaRepository<SportsFacilityProgram, Long> {

    /**
     * 특정 날짜의 모든 프로그램 정보 조회
     */
    List<SportsFacilityProgram> findByDataDate(String dataDate);

    /**
     * 특정 시설의 프로그램 정보 조회
     */
    List<SportsFacilityProgram> findByCenterNameAndDataDate(String centerName, String dataDate);

    /**
     * 특정 종목의 프로그램 정보 조회
     */
    List<SportsFacilityProgram> findBySubjectNameAndDataDate(String subjectName, String dataDate);

    /**
     * 프로그램명으로 검색 (부분 검색)
     */
    List<SportsFacilityProgram> findByProgramNameContainingAndDataDate(String programName, String dataDate);

    /**
     * 사용 여부로 조회
     */
    List<SportsFacilityProgram> findByUseYnAndDataDate(String useYn, String dataDate);

    /**
     * 무료 여부로 조회
     */
    List<SportsFacilityProgram> findByFeeFreeAndDataDate(String feeFree, String dataDate);

    /**
     * 특정 날짜 이전의 데이터 삭제
     */
    @Modifying
    @Query("DELETE FROM SportsFacilityProgram sfp WHERE sfp.dataDate < :beforeDate")
    int deleteByDataDateBefore(@Param("beforeDate") String beforeDate);

    /**
     * 특정 날짜의 데이터 개수 조회
     */
    long countByDataDate(String dataDate);

    /**
     * 모든 데이터 날짜 목록 조회 (최신순)
     */
    @Query("SELECT DISTINCT sfp.dataDate FROM SportsFacilityProgram sfp ORDER BY sfp.dataDate DESC")
    List<String> findAllDataDatesOrderByDesc();

    /**
     * 시설별 프로그램 개수 조회
     */
    @Query("SELECT sfp.centerName, COUNT(sfp) FROM SportsFacilityProgram sfp WHERE sfp.dataDate = :dataDate GROUP BY sfp.centerName")
    List<Object[]> countByCenterNameAndDataDate(@Param("dataDate") String dataDate);

    /**
     * 종목별 프로그램 개수 조회
     */
    @Query("SELECT sfp.subjectName, COUNT(sfp) FROM SportsFacilityProgram sfp WHERE sfp.dataDate = :dataDate GROUP BY sfp.subjectName")
    List<Object[]> countBySubjectNameAndDataDate(@Param("dataDate") String dataDate);
}
