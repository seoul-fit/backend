package com.seoulfit.backend.publicdata.facilities.adapter.out.persistence;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.repository.SportsFacilityProgramJpaRepository;
import com.seoulfit.backend.publicdata.facilities.application.port.out.SportsFacilityProgramRepository;
import com.seoulfit.backend.publicdata.facilities.domain.SportsFacilityProgram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 서울시 공공체육시설 프로그램 정보 Repository 어댑터
 * 헥사고날 아키텍처의 출력 어댑터
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SportsFacilityProgramRepositoryAdapter implements SportsFacilityProgramRepository {

    private final SportsFacilityProgramJpaRepository jpaRepository;

    @Override
    public SportsFacilityProgram save(SportsFacilityProgram program) {
        log.debug("프로그램 정보 저장 - 프로그램명: {}, 시설명: {}", 
            program.getProgramName(), program.getCenterName());
        return jpaRepository.save(program);
    }

    @Override
    public List<SportsFacilityProgram> saveAll(List<SportsFacilityProgram> programs) {
        log.debug("프로그램 정보 일괄 저장 - 개수: {}", programs.size());
        return jpaRepository.saveAll(programs);
    }

    @Override
    public Optional<SportsFacilityProgram> findById(Long id) {
        log.debug("프로그램 정보 조회 - ID: {}", id);
        return jpaRepository.findById(id);
    }

    @Override
    public List<SportsFacilityProgram> findByDataDate(String dataDate) {
        log.debug("날짜별 프로그램 정보 조회 - 날짜: {}", dataDate);
        return jpaRepository.findByDataDate(dataDate);
    }

    @Override
    public List<SportsFacilityProgram> findByCenterNameAndDataDate(String centerName, String dataDate) {
        log.debug("시설별 프로그램 정보 조회 - 시설: {}, 날짜: {}", centerName, dataDate);
        return jpaRepository.findByCenterNameAndDataDate(centerName, dataDate);
    }

    @Override
    public List<SportsFacilityProgram> findBySubjectNameAndDataDate(String subjectName, String dataDate) {
        log.debug("종목별 프로그램 정보 조회 - 종목: {}, 날짜: {}", subjectName, dataDate);
        return jpaRepository.findBySubjectNameAndDataDate(subjectName, dataDate);
    }

    @Override
    public List<SportsFacilityProgram> findByProgramNameContainingAndDataDate(String programName, String dataDate) {
        log.debug("프로그램명 검색 - 검색어: {}, 날짜: {}", programName, dataDate);
        return jpaRepository.findByProgramNameContainingAndDataDate(programName, dataDate);
    }

    @Override
    public List<SportsFacilityProgram> findByUseYnAndDataDate(String useYn, String dataDate) {
        log.debug("사용 여부별 프로그램 조회 - 사용여부: {}, 날짜: {}", useYn, dataDate);
        return jpaRepository.findByUseYnAndDataDate(useYn, dataDate);
    }

    @Override
    public List<SportsFacilityProgram> findByFeeFreeAndDataDate(String feeFree, String dataDate) {
        log.debug("무료 여부별 프로그램 조회 - 무료여부: {}, 날짜: {}", feeFree, dataDate);
        return jpaRepository.findByFeeFreeAndDataDate(feeFree, dataDate);
    }

    @Override
    public int deleteByDataDateBefore(String beforeDate) {
        log.debug("이전 데이터 삭제 - 기준 날짜: {} (테스트를 위해 삭제 비활성화)", beforeDate);
        // 테스트를 위해 삭제 기능 임시 비활성화
        return 0;
        // return jpaRepository.deleteByDataDateBefore(beforeDate);
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
    public List<Object[]> countByCenterNameAndDataDate(String dataDate) {
        log.debug("시설별 프로그램 개수 조회 - 날짜: {}", dataDate);
        return jpaRepository.countByCenterNameAndDataDate(dataDate);
    }

    @Override
    public List<Object[]> countBySubjectNameAndDataDate(String dataDate) {
        log.debug("종목별 프로그램 개수 조회 - 날짜: {}", dataDate);
        return jpaRepository.countBySubjectNameAndDataDate(dataDate);
    }
}
