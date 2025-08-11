package com.seoulfit.backend.publicdata.park.adapter.out.persistence;

import com.seoulfit.backend.publicdata.park.application.port.out.SeoulParkRepository;
import com.seoulfit.backend.publicdata.park.domain.SeoulPark;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공원 정보 Repository 어댑터
 * 헥사고날 아키텍처의 출력 어댑터
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SeoulParkRepositoryAdapter implements SeoulParkRepository {

    private final SeoulParkJpaRepository jpaRepository;

    @Override
    public SeoulPark save(SeoulPark seoulPark) {
        log.debug("공원 정보 저장 - 공원번호: {}, 공원명: {}", 
            seoulPark.getParkIdx(), seoulPark.getParkName());
        return jpaRepository.save(seoulPark);
    }

    @Override
    public List<SeoulPark> saveAll(List<SeoulPark> seoulParks) {
        log.debug("공원 정보 일괄 저장 - 개수: {}", seoulParks.size());
        return jpaRepository.saveAll(seoulParks);
    }

    @Override
    public Optional<SeoulPark> findByParkIdxAndDataDate(Long parkIdx, String dataDate) {
        log.debug("공원 정보 조회 - 공원번호: {}, 날짜: {}", parkIdx, dataDate);
        return jpaRepository.findByParkIdxAndDataDate(parkIdx, dataDate);
    }

    @Override
    public List<SeoulPark> findByDataDate(String dataDate) {
        log.debug("날짜별 공원 정보 조회 - 날짜: {}", dataDate);
        return jpaRepository.findByDataDate(dataDate);
    }

    @Override
    public List<SeoulPark> findByZoneAndDataDate(String zone, String dataDate) {
        log.debug("지역별 공원 정보 조회 - 지역: {}, 날짜: {}", zone, dataDate);
        return jpaRepository.findByZoneAndDataDate(zone, dataDate);
    }

    @Override
    public List<SeoulPark> findByParkNameContainingAndDataDate(String parkName, String dataDate) {
        log.debug("공원명 검색 - 검색어: {}, 날짜: {}", parkName, dataDate);
        return jpaRepository.findByParkNameContainingAndDataDate(parkName, dataDate);
    }

    @Override
    public int deleteByDataDateBefore(String beforeDate) {
        log.debug("이전 데이터 삭제 - 기준 날짜: {}", beforeDate);
        return jpaRepository.deleteByDataDateBefore(beforeDate);
    }

    @Override
    public long countByDataDate(String dataDate) {
        log.debug("날짜별 데이터 개수 조회 - 날짜: {}", dataDate);
        return jpaRepository.countByDataDate(dataDate);
    }

    @Override
    public List<String> findAllDataDatesOrderByDesc() {
        log.debug("사용 가능한 데이터 날짜 목록 조회");
        return jpaRepository.findAllDataDatesOrderByDesc();
    }

    @Override
    public List<SeoulPark> findByDataDateAndWgs84LongitudeIsNotNullAndWgs84LatitudeIsNotNull(String dataDate) {
        log.debug("좌표 정보가 있는 공원 조회 - 날짜: {}", dataDate);
        return jpaRepository.findByDataDateAndWgs84LongitudeIsNotNullAndWgs84LatitudeIsNotNull(dataDate);
    }
}
