package com.seoulfit.backend.publicdata.facilities.application.port.in;

import com.seoulfit.backend.publicdata.facilities.domain.SportsFacilityProgram;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공공체육시설 프로그램 정보 조회 유스케이스
 * 헥사고날 아키텍처의 입력 포트
 */
public interface SportsFacilityProgramQueryUseCase {

    /**
     * 특정 날짜의 모든 프로그램 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findProgramsByDate(String dataDate);

    /**
     * 최신 프로그램 정보 조회
     * @return 최신 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findLatestPrograms();

    /**
     * 특정 시설의 프로그램 정보 조회
     * @param centerName 시설명
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findProgramsByCenter(String centerName, String dataDate);

    /**
     * 특정 종목의 프로그램 정보 조회
     * @param subjectName 종목명
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findProgramsBySubject(String subjectName, String dataDate);

    /**
     * 프로그램명으로 검색
     * @param programName 프로그램명 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 프로그램 정보 목록
     */
    List<SportsFacilityProgram> searchProgramsByName(String programName, String dataDate);

    /**
     * 프로그램 ID로 특정 프로그램 정보 조회
     * @param id 프로그램 ID
     * @return 프로그램 정보
     */
    Optional<SportsFacilityProgram> findProgramById(Long id);

    /**
     * 사용 중인 프로그램만 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 사용 중인 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findActivePrograms(String dataDate);

    /**
     * 무료 프로그램 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 무료 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findFreePrograms(String dataDate);

    /**
     * 사용 가능한 데이터 날짜 목록 조회
     * @return 데이터 날짜 목록 (최신순)
     */
    List<String> getAvailableDataDates();

    /**
     * 특정 날짜의 프로그램 데이터 통계 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 프로그램 데이터 통계
     */
    ProgramDataStatistics getProgramStatistics(String dataDate);

    /**
     * 프로그램 데이터 통계 DTO
     */
    record ProgramDataStatistics(
        String dataDate,
        long totalPrograms,
        long activePrograms,
        long freePrograms,
        List<String> centers,
        List<String> subjects,
        long programsPerCenter,
        long programsPerSubject
    ) {}
}
