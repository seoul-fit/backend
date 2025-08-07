package com.seoulfit.backend.park.application.port.out;

import com.seoulfit.backend.park.domain.SeoulPark;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공원 정보 저장소 포트
 * 헥사고날 아키텍처의 출력 포트로, 도메인 계층에서 인프라스트럭처 계층으로의 의존성을 역전시킴
 */
public interface SeoulParkRepository {

    /**
     * 공원 정보 저장
     * @param seoulPark 저장할 공원 정보
     * @return 저장된 공원 정보
     */
    SeoulPark save(SeoulPark seoulPark);

    /**
     * 공원 정보 목록 저장
     * @param seoulParks 저장할 공원 정보 목록
     * @return 저장된 공원 정보 목록
     */
    List<SeoulPark> saveAll(List<SeoulPark> seoulParks);

    /**
     * 공원 번호와 데이터 날짜로 공원 정보 조회
     * @param parkIdx 공원 번호
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 공원 정보
     */
    Optional<SeoulPark> findByParkIdxAndDataDate(Long parkIdx, String dataDate);

    /**
     * 특정 날짜의 모든 공원 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 공원 정보 목록
     */
    List<SeoulPark> findByDataDate(String dataDate);

    /**
     * 특정 지역의 공원 정보 조회
     * @param zone 지역명
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 공원 정보 목록
     */
    List<SeoulPark> findByZoneAndDataDate(String zone, String dataDate);

    /**
     * 공원명으로 검색
     * @param parkName 공원명 (부분 검색 가능)
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 공원 정보 목록
     */
    List<SeoulPark> findByParkNameContainingAndDataDate(String parkName, String dataDate);

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
     * 좌표 정보가 있는 공원 정보 조회
     * @param dataDate 데이터 날짜 (YYYYMMDD)
     * @return 좌표 정보가 있는 공원 정보 목록
     */
    List<SeoulPark> findByDataDateAndWgs84LongitudeIsNotNullAndWgs84LatitudeIsNotNull(String dataDate);
}
