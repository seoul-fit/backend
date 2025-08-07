package com.seoulfit.backend.sports.application.service;

import com.seoulfit.backend.sports.application.port.in.SportsFacilityProgramBatchUseCase;
import com.seoulfit.backend.sports.application.port.out.SportsFacilityProgramApiClient;
import com.seoulfit.backend.sports.application.port.out.SportsFacilityProgramRepository;
import com.seoulfit.backend.sports.application.port.out.dto.SportsFacilityProgramApiResponse;
import com.seoulfit.backend.sports.domain.SportsFacilityProgram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 서울시 공공체육시설 프로그램 정보 배치 처리 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SportsFacilityProgramBatchService implements SportsFacilityProgramBatchUseCase {

    private final SportsFacilityProgramApiClient apiClient;
    private final SportsFacilityProgramRepository repository;

    @Override
    public SportsFacilityProgramBatchResult processDailyBatch(String dataDate) {
        log.info("서울시 공공체육시설 프로그램 정보 일일 배치 처리 시작 - 날짜: {}", dataDate);

        try {
            // 1. API에서 프로그램 정보 조회
            SportsFacilityProgramApiResponse apiResponse = apiClient.fetchAllProgramInfo();
            
            if (!apiResponse.isSuccess()) {
                String errorMessage = "API 호출 실패: " + 
                    (apiResponse.getListProgramByPublicSportsFacilitiesService() != null && 
                     apiResponse.getListProgramByPublicSportsFacilitiesService().getResult() != null ? 
                     apiResponse.getListProgramByPublicSportsFacilitiesService().getResult().getMessage() : "Unknown error");
                log.error(errorMessage);
                return SportsFacilityProgramBatchResult.failure(dataDate, errorMessage);
            }

            List<SportsFacilityProgramApiResponse.ProgramInfo> programInfoList = apiResponse.getProgramInfoList();
            log.info("API에서 {} 개의 프로그램 정보 조회 완료", programInfoList.size());

            // 2. 데이터 변환 및 저장 (처음 10개만 테스트)
            int savedCount = 0;
            int updatedCount = 0;
            int maxTestCount = Math.min(10, programInfoList.size()); // 테스트를 위해 10개만 처리

            for (int i = 0; i < maxTestCount; i++) {
                SportsFacilityProgramApiResponse.ProgramInfo programInfo = programInfoList.get(i);
                try {
                    SportsFacilityProgram program = convertToEntity(programInfo, dataDate);
                    
                    // 새 데이터 저장 (중복 체크 없이 매번 새로 저장)
                    repository.save(program);
                    savedCount++;
                    
                } catch (Exception e) {
                    log.warn("프로그램 정보 저장 실패 - 프로그램명: {}, 오류: {}", 
                        programInfo.getProgramName(), e.getMessage());
                }
            }

            // 3. 이전 데이터 정리 (3일 이전 데이터 삭제)
            int deletedCount = cleanupOldData(3);

            log.info("서울시 공공체육시설 프로그램 정보 배치 처리 완료 - 조회: {}, 저장: {}, 업데이트: {}, 삭제: {}", 
                programInfoList.size(), savedCount, updatedCount, deletedCount);

            return SportsFacilityProgramBatchResult.success(dataDate, programInfoList.size(), 
                savedCount, updatedCount, deletedCount);

        } catch (Exception e) {
            log.error("서울시 공공체육시설 프로그램 정보 배치 처리 실패", e);
            return SportsFacilityProgramBatchResult.failure(dataDate, e.getMessage());
        }
    }

    @Override
    public SportsFacilityProgramBatchResult processBatchBySubject(String dataDate, String subjectName) {
        log.info("서울시 공공체육시설 프로그램 정보 종목별 배치 처리 시작 - 날짜: {}, 종목: {}", dataDate, subjectName);

        try {
            // 1. API에서 특정 종목 프로그램 정보 조회
            SportsFacilityProgramApiResponse apiResponse = apiClient.fetchProgramInfoBySubject(subjectName);
            
            if (!apiResponse.isSuccess()) {
                String errorMessage = "API 호출 실패: " + 
                    (apiResponse.getListProgramByPublicSportsFacilitiesService() != null && 
                     apiResponse.getListProgramByPublicSportsFacilitiesService().getResult() != null ? 
                     apiResponse.getListProgramByPublicSportsFacilitiesService().getResult().getMessage() : "Unknown error");
                log.error(errorMessage);
                return SportsFacilityProgramBatchResult.failure(dataDate, errorMessage);
            }

            List<SportsFacilityProgramApiResponse.ProgramInfo> programInfoList = apiResponse.getProgramInfoList();
            log.info("API에서 {} 개의 {} 종목 프로그램 정보 조회 완료", programInfoList.size(), subjectName);

            // 2. 데이터 변환 및 저장 (처음 10개만 테스트)
            int savedCount = 0;
            int maxTestCount = Math.min(10, programInfoList.size()); // 테스트를 위해 10개만 처리

            for (int i = 0; i < maxTestCount; i++) {
                SportsFacilityProgramApiResponse.ProgramInfo programInfo = programInfoList.get(i);
                try {
                    SportsFacilityProgram program = convertToEntity(programInfo, dataDate);
                    repository.save(program);
                    savedCount++;
                } catch (Exception e) {
                    log.warn("프로그램 정보 저장 실패 - 프로그램명: {}, 오류: {}", 
                        programInfo.getProgramName(), e.getMessage());
                }
            }

            log.info("서울시 공공체육시설 프로그램 정보 종목별 배치 처리 완료 - 조회: {}, 저장: {}", 
                programInfoList.size(), savedCount);

            return SportsFacilityProgramBatchResult.success(dataDate, programInfoList.size(), 
                savedCount, 0, 0);

        } catch (Exception e) {
            log.error("서울시 공공체육시설 프로그램 정보 종목별 배치 처리 실패", e);
            return SportsFacilityProgramBatchResult.failure(dataDate, e.getMessage());
        }
    }

    @Override
    public int cleanupOldData(int retentionDays) {
        String cutoffDate = LocalDate.now()
            .minusDays(retentionDays)
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        int deletedCount = repository.deleteByDataDateBefore(cutoffDate);
        log.info("{}일 이전 프로그램 데이터 {} 건 삭제 완료", retentionDays, deletedCount);
        
        return deletedCount;
    }

    /**
     * API 응답 데이터를 엔티티로 변환
     */
    private SportsFacilityProgram convertToEntity(SportsFacilityProgramApiResponse.ProgramInfo programInfo, String dataDate) {
        return SportsFacilityProgram.builder()
            .centerName(programInfo.getCenterName())
            .groundName(programInfo.getGroundName())
            .programName(programInfo.getProgramName())
            .subjectName(programInfo.getSubjectName())
            .place(programInfo.getPlace())
            .address(programInfo.getAddress())
            .homepage(programInfo.getHomepage())
            .parkingSide(programInfo.getParkingSide())
            .tel(programInfo.getTel())
            .fax(programInfo.getFax())
            .email(programInfo.getEmail())
            .className(programInfo.getClassName())
            .pLevel(programInfo.getPLevel())
            .target(programInfo.getTarget())
            .term(programInfo.getTerm())
            .week(programInfo.getWeek())
            .classTime(programInfo.getClassTime())
            .fee(programInfo.getFee())
            .intro(programInfo.getIntro())
            .capacity(programInfo.getCapacity())
            .enterWay(programInfo.getEnterWay())
            .enterTerm(programInfo.getEnterTerm())
            .selectWay(programInfo.getSelectWay())
            .onlineLink(programInfo.getOnlineLink())
            .useYn(programInfo.getUseYn())
            .classStartDate(programInfo.getClassS())
            .classEndDate(programInfo.getClassE())
            .feeFree(programInfo.getFeeFree())
            .dataDate(dataDate)
            .build();
    }
}
