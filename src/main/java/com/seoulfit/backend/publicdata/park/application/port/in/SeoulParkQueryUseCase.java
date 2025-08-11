package com.seoulfit.backend.publicdata.park.application.port.in;

import com.seoulfit.backend.publicdata.park.domain.SeoulPark;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공원 정보 조회 유스케이스
 * 헥사고날 아키텍처의 입력 포트
 */
public interface SeoulParkQueryUseCase {

    /**
     * 특정 날짜의 모든 공원 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 공원 정보 목록
     */
    List<SeoulPark> findParksByDate(String dataDate);

    /**
     * 최신 공원 정보 조회
     * @return 최신 공원 정보 목록
     */
    List<SeoulPark> findLatestParks();

    /**
     * 특정 지역의 공원 정보 조회
     * @param zone 지역명
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 공원 정보 목록
     */
    List<SeoulPark> findParksByZone(String zone, String dataDate);

    /**
     * 공원명으로 검색
     * @param parkName 공원명 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 공원 정보 목록
     */
    List<SeoulPark> searchParksByName(String parkName, String dataDate);

    /**
     * 공원 번호로 특정 공원 정보 조회
     * @param parkIdx 공원 번호
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 공원 정보
     */
    Optional<SeoulPark> findParkByIdx(Long parkIdx, String dataDate);

    /**
     * 좌표 정보가 있는 공원 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD, null이면 최신 데이터)
     * @return 좌표 정보가 있는 공원 정보 목록
     */
    List<SeoulPark> findParksWithCoordinates(String dataDate);

    /**
     * 사용 가능한 데이터 날짜 목록 조회
     * @return 데이터 날짜 목록 (최신순)
     */
    List<String> getAvailableDataDates();

    /**
     * 특정 날짜의 공원 데이터 통계 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 공원 데이터 통계
     */
    ParkDataStatistics getParkStatistics(String dataDate);

    /**
     * 공원 데이터 통계 DTO
     */
    record ParkDataStatistics(
        String dataDate,
        long totalParks,
        long parksWithCoordinates,
        List<String> zones,
        long parksPerZone
    ) {}
}
