package com.seoulfit.backend.publicdata.facilities.application.service;

import com.seoulfit.backend.publicdata.facilities.application.port.in.SportsFacilityProgramQueryUseCase;
import com.seoulfit.backend.publicdata.facilities.application.port.out.SportsFacilityProgramRepository;
import com.seoulfit.backend.publicdata.facilities.domain.SportsFacilityProgram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 서울시 공공체육시설 프로그램 정보 조회 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SportsFacilityProgramQueryService implements SportsFacilityProgramQueryUseCase {

    private final SportsFacilityProgramRepository repository;

    @Override
    public List<SportsFacilityProgram> findProgramsByDate(String dataDate) {
        log.debug("특정 날짜 프로그램 정보 조회 - 날짜: {}", dataDate);
        return repository.findByDataDate(dataDate);
    }

    @Override
    public List<SportsFacilityProgram> findLatestPrograms() {
        log.debug("최신 프로그램 정보 조회");
        List<String> availableDates = repository.findAllDataDatesOrderByDesc();
        
        if (availableDates.isEmpty()) {
            log.warn("사용 가능한 프로그램 데이터가 없습니다");
            return List.of();
        }
        
        String latestDate = availableDates.get(0);
        log.debug("최신 데이터 날짜: {}", latestDate);
        
        return repository.findByDataDate(latestDate);
    }

    @Override
    public List<SportsFacilityProgram> findProgramsByCenter(String centerName, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 프로그램 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("시설별 프로그램 정보 조회 - 시설: {}, 날짜: {}", centerName, targetDate);
        return repository.findByCenterNameAndDataDate(centerName, targetDate);
    }

    @Override
    public List<SportsFacilityProgram> findProgramsBySubject(String subjectName, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 프로그램 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("종목별 프로그램 정보 조회 - 종목: {}, 날짜: {}", subjectName, targetDate);
        return repository.findBySubjectNameAndDataDate(subjectName, targetDate);
    }

    @Override
    public List<SportsFacilityProgram> searchProgramsByName(String programName, String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 프로그램 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("프로그램명 검색 - 검색어: {}, 날짜: {}", programName, targetDate);
        return repository.findByProgramNameContainingAndDataDate(programName, targetDate);
    }

    @Override
    public Optional<SportsFacilityProgram> findProgramById(Long id) {
        log.debug("프로그램 ID로 조회 - ID: {}", id);
        return repository.findById(id);
    }

    @Override
    public List<SportsFacilityProgram> findActivePrograms(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 프로그램 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("사용 중인 프로그램 조회 - 날짜: {}", targetDate);
        return repository.findByUseYnAndDataDate("Y", targetDate);
    }

    @Override
    public List<SportsFacilityProgram> findFreePrograms(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 프로그램 데이터가 없습니다");
            return List.of();
        }
        
        log.debug("무료 프로그램 조회 - 날짜: {}", targetDate);
        return repository.findByFeeFreeAndDataDate("Y", targetDate);
    }

    @Override
    public List<String> getAvailableDataDates() {
        log.debug("사용 가능한 데이터 날짜 목록 조회");
        return repository.findAllDataDatesOrderByDesc();
    }

    @Override
    public ProgramDataStatistics getProgramStatistics(String dataDate) {
        String targetDate = dataDate != null ? dataDate : getLatestDataDate();
        
        if (targetDate == null) {
            log.warn("사용 가능한 프로그램 데이터가 없습니다");
            return new ProgramDataStatistics(targetDate, 0, 0, 0, List.of(), List.of(), 0, 0);
        }
        
        log.debug("프로그램 데이터 통계 조회 - 날짜: {}", targetDate);
        
        List<SportsFacilityProgram> allPrograms = repository.findByDataDate(targetDate);
        long totalPrograms = allPrograms.size();
        
        long activePrograms = allPrograms.stream()
            .filter(SportsFacilityProgram::isActive)
            .count();
        
        long freePrograms = allPrograms.stream()
            .filter(SportsFacilityProgram::isFree)
            .count();
        
        List<String> centers = allPrograms.stream()
            .map(SportsFacilityProgram::getCenterName)
            .filter(center -> center != null && !center.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        List<String> subjects = allPrograms.stream()
            .map(SportsFacilityProgram::getSubjectName)
            .filter(subject -> subject != null && !subject.trim().isEmpty())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        long programsPerCenter = centers.isEmpty() ? 0 : totalPrograms / centers.size();
        long programsPerSubject = subjects.isEmpty() ? 0 : totalPrograms / subjects.size();
        
        return new ProgramDataStatistics(targetDate, totalPrograms, activePrograms, freePrograms, 
            centers, subjects, programsPerCenter, programsPerSubject);
    }

    /**
     * 최신 데이터 날짜 조회
     */
    private String getLatestDataDate() {
        List<String> availableDates = repository.findAllDataDatesOrderByDesc();
        return availableDates.isEmpty() ? null : availableDates.get(0);
    }
}
