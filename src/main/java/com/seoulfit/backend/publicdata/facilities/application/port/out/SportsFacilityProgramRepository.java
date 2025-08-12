package com.seoulfit.backend.publicdata.facilities.application.port.out;

import com.seoulfit.backend.publicdata.facilities.domain.SportsFacilityProgram;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공공체육시설 프로그램 정보 저장소 포트
 * 헥사고날 아키텍처의 출력 포트로, 도메인 계층에서 인프라스트럭처 계층으로의 의존성을 역전시킴
 */
public interface SportsFacilityProgramRepository {

    /**
     * 체육시설 프로그램 정보 저장
     * @param program 저장할 프로그램 정보
     * @return 저장된 프로그램 정보
     */
    SportsFacilityProgram save(SportsFacilityProgram program);

    /**
     * 체육시설 프로그램 정보 목록 저장
     * @param programs 저장할 프로그램 정보 목록
     * @return 저장된 프로그램 정보 목록
     */
    List<SportsFacilityProgram> saveAll(List<SportsFacilityProgram> programs);

    /**
     * 프로그램 ID로 조회
     * @param id 프로그램 ID
     * @return 프로그램 정보
     */
    Optional<SportsFacilityProgram> findById(Long id);

    /**
     * 특정 날짜의 모든 프로그램 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findByDataDate(String dataDate);

    /**
     * 특정 시설의 프로그램 정보 조회
     * @param centerName 시설명
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findByCenterNameAndDataDate(String centerName, String dataDate);

    /**
     * 특정 종목의 프로그램 정보 조회
     * @param subjectName 종목명
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findBySubjectNameAndDataDate(String subjectName, String dataDate);

    /**
     * 프로그램명으로 검색
     * @param programName 프로그램명 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findByProgramNameContainingAndDataDate(String programName, String dataDate);

    /**
     * 사용 중인 프로그램만 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 사용 중인 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findByUseYnAndDataDate(String useYn, String dataDate);

    /**
     * 무료 프로그램 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 무료 프로그램 정보 목록
     */
    List<SportsFacilityProgram> findByFeeFreeAndDataDate(String feeFree, String dataDate);

    /**
     * 특정 날짜 이전의 데이터 삭제
     * @param beforeDate 삭제 기준 날짜 (YYYYMMDD)
     * @return 삭제된 레코드 수
     */
    int deleteByDataDateBefore(String beforeDate);

    /**
     * 특정 날짜의 데이터 개수 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 데이터 개수
     */
    long countByDataDate(String dataDate);

    /**
     * 모든 데이터 날짜 목록 조회 (최신순)
     * @return 데이터 날짜 목록
     */
    List<String> findAllDataDatesOrderByDesc();

    /**
     * 시설별 프로그램 개수 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 시설별 프로그램 개수 목록
     */
    List<Object[]> countByCenterNameAndDataDate(String dataDate);

    /**
     * 종목별 프로그램 개수 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 종목별 프로그램 개수 목록
     */
    List<Object[]> countBySubjectNameAndDataDate(String dataDate);
}
