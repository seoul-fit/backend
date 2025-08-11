package com.seoulfit.backend.env.application.service;

import com.seoulfit.backend.env.application.port.in.AirQualityBatchUseCase;
import com.seoulfit.backend.env.application.port.out.AirQualityApiClient;
import com.seoulfit.backend.env.application.port.out.AirQualityRepository;
import com.seoulfit.backend.env.application.port.out.dto.AirQualityApiResponse;
import com.seoulfit.backend.env.domain.AirQuality;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 대기질 정보 배치 처리 서비스
 * 헥사고날 아키텍처의 애플리케이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AirQualityBatchService implements AirQualityBatchUseCase {

    private final AirQualityApiClient apiClient;
    private final AirQualityRepository repository;

    @Override
    public AirQualityBatchResult processRealTimeBatch() {
        log.info("실시간 대기질 정보 배치 처리 시작");

        try {
            // 1. API에서 실시간 대기질 정보 조회
            AirQualityApiResponse apiResponse = apiClient.fetchRealTimeAirQuality();
            
            if (!apiResponse.isSuccess()) {
                String errorMessage = "API 호출 실패: " + apiResponse.getErrorMessage();
                log.error(errorMessage);
                return AirQualityBatchResult.failure(errorMessage);
            }

            List<AirQualityApiResponse.AirQualityData> apiDataList = apiResponse.getData();
            if (apiDataList == null || apiDataList.isEmpty()) {
                log.warn("API에서 조회된 대기질 데이터가 없습니다.");
                return AirQualityBatchResult.success(0, 0, 0, 0);
            }

            // 2. 데이터 변환 및 저장/업데이트 처리
            int totalFetched = apiDataList.size();
            int totalSaved = 0;
            int totalUpdated = 0;
            int totalSkipped = 0;

            List<AirQuality> airQualitiesToSave = new ArrayList<>();

            for (AirQualityApiResponse.AirQualityData apiData : apiDataList) {
                try {
                    // 기존 데이터 확인
                    LocalDateTime measureTime = apiData.parseMsrDt();
                    Optional<AirQuality> existingData = repository.findByStationAndDateTime(
                        apiData.getMsrSteNm(), measureTime);

                    if (existingData.isPresent()) {
                        // 기존 데이터 업데이트
                        AirQuality airQuality = existingData.get();
                        airQuality.updateData(
                            apiData.getPm10ValueAsInteger(),
                            apiData.getPm25ValueAsInteger(),
                            apiData.getO3ValueAsDouble(),
                            apiData.getNo2ValueAsDouble(),
                            apiData.getCoValueAsDouble(),
                            apiData.getSo2ValueAsDouble(),
                            apiData.getKhaiValueAsInteger(),
                            apiData.getKhaiGrade(),
                            apiData.getPm1024hAvgAsInteger(),
                            apiData.getPm2524hAvgAsInteger()
                        );
                        airQualitiesToSave.add(airQuality);
                        totalUpdated++;
                    } else {
                        // 새로운 데이터 생성
                        AirQuality newAirQuality = AirQuality.builder()
                            .msrDt(measureTime)
                            .msrRgnNm(apiData.getMsrRgnNm())
                            .msrSteNm(apiData.getMsrSteNm())
                            .pm10Value(apiData.getPm10ValueAsInteger())
                            .pm25Value(apiData.getPm25ValueAsInteger())
                            .o3Value(apiData.getO3ValueAsDouble())
                            .no2Value(apiData.getNo2ValueAsDouble())
                            .coValue(apiData.getCoValueAsDouble())
                            .so2Value(apiData.getSo2ValueAsDouble())
                            .khaiValue(apiData.getKhaiValueAsInteger())
                            .khaiGrade(apiData.getKhaiGrade())
                            .pm1024hAvg(apiData.getPm1024hAvgAsInteger())
                            .pm2524hAvg(apiData.getPm2524hAvgAsInteger())
                            .build();
                        
                        airQualitiesToSave.add(newAirQuality);
                        totalSaved++;
                    }
                } catch (Exception e) {
                    log.warn("대기질 데이터 처리 중 오류 발생: {}, 데이터: {}", e.getMessage(), apiData.getMsrSteNm());
                    totalSkipped++;
                }
            }

            // 3. 일괄 저장
            if (!airQualitiesToSave.isEmpty()) {
                repository.saveAll(airQualitiesToSave);
            }

            log.info("실시간 대기질 정보 배치 처리 완료 - 조회: {}, 저장: {}, 업데이트: {}, 스킵: {}", 
                totalFetched, totalSaved, totalUpdated, totalSkipped);

            return AirQualityBatchResult.success(totalFetched, totalSaved, totalUpdated, totalSkipped);

        } catch (Exception e) {
            String errorMessage = "실시간 대기질 정보 배치 처리 중 예외 발생: " + e.getMessage();
            log.error(errorMessage, e);
            return AirQualityBatchResult.failure(errorMessage);
        }
    }

    @Override
    public AirQualityBatchResult processDailyBatch(String dataDate) {
        log.info("일일 대기질 정보 배치 처리 시작 - 날짜: {}", dataDate);

        try {
            // 일일 배치는 전체 데이터를 조회하여 처리
            AirQualityApiResponse apiResponse = apiClient.fetchAllAirQuality();
            
            if (!apiResponse.isSuccess()) {
                String errorMessage = "API 호출 실패: " + apiResponse.getErrorMessage();
                log.error(errorMessage);
                return AirQualityBatchResult.failure(errorMessage);
            }

            List<AirQualityApiResponse.AirQualityData> apiDataList = apiResponse.getData();
            if (apiDataList == null || apiDataList.isEmpty()) {
                log.warn("API에서 조회된 대기질 데이터가 없습니다.");
                return AirQualityBatchResult.success(0, 0, 0, 0);
            }

            return processAirQualityData(apiDataList);

        } catch (Exception e) {
            String errorMessage = "일일 대기질 정보 배치 처리 중 예외 발생: " + e.getMessage();
            log.error(errorMessage, e);
            return AirQualityBatchResult.failure(errorMessage);
        }
    }

    @Override
    public AirQualityBatchResult processTimeBatch(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("특정 시간대 대기질 정보 배치 처리 시작 - 시작: {}, 종료: {}", startTime, endTime);

        try {
            // 특정 시간대 배치는 실시간 API를 사용
            AirQualityApiResponse apiResponse = apiClient.fetchRealTimeAirQuality();
            
            if (!apiResponse.isSuccess()) {
                String errorMessage = "API 호출 실패: " + apiResponse.getErrorMessage();
                log.error(errorMessage);
                return AirQualityBatchResult.failure(errorMessage);
            }

            List<AirQualityApiResponse.AirQualityData> apiDataList = apiResponse.getData();
            if (apiDataList == null || apiDataList.isEmpty()) {
                log.warn("API에서 조회된 대기질 데이터가 없습니다.");
                return AirQualityBatchResult.success(0, 0, 0, 0);
            }

            // 시간 범위 필터링
            List<AirQualityApiResponse.AirQualityData> filteredData = apiDataList.stream()
                .filter(data -> {
                    LocalDateTime measureTime = data.parseMsrDt();
                    return !measureTime.isBefore(startTime) && !measureTime.isAfter(endTime);
                })
                .toList();

            return processAirQualityData(filteredData);

        } catch (Exception e) {
            String errorMessage = "특정 시간대 대기질 정보 배치 처리 중 예외 발생: " + e.getMessage();
            log.error(errorMessage, e);
            return AirQualityBatchResult.failure(errorMessage);
        }
    }

    /**
     * 대기질 데이터 처리 공통 로직
     */
    private AirQualityBatchResult processAirQualityData(List<AirQualityApiResponse.AirQualityData> apiDataList) {
        int totalFetched = apiDataList.size();
        int totalSaved = 0;
        int totalUpdated = 0;
        int totalSkipped = 0;

        List<AirQuality> airQualitiesToSave = new ArrayList<>();

        for (AirQualityApiResponse.AirQualityData apiData : apiDataList) {
            try {
                // 기존 데이터 확인
                LocalDateTime measureTime = apiData.parseMsrDt();
                Optional<AirQuality> existingData = repository.findByStationAndDateTime(
                    apiData.getMsrSteNm(), measureTime);

                if (existingData.isPresent()) {
                    // 기존 데이터 업데이트
                    AirQuality airQuality = existingData.get();
                    airQuality.updateData(
                        apiData.getPm10ValueAsInteger(),
                        apiData.getPm25ValueAsInteger(),
                        apiData.getO3ValueAsDouble(),
                        apiData.getNo2ValueAsDouble(),
                        apiData.getCoValueAsDouble(),
                        apiData.getSo2ValueAsDouble(),
                        apiData.getKhaiValueAsInteger(),
                        apiData.getKhaiGrade(),
                        apiData.getPm1024hAvgAsInteger(),
                        apiData.getPm2524hAvgAsInteger()
                    );
                    airQualitiesToSave.add(airQuality);
                    totalUpdated++;
                } else {
                    // 새로운 데이터 생성
                    AirQuality newAirQuality = AirQuality.builder()
                        .msrDt(measureTime)
                        .msrRgnNm(apiData.getMsrRgnNm())
                        .msrSteNm(apiData.getMsrSteNm())
                        .pm10Value(apiData.getPm10ValueAsInteger())
                        .pm25Value(apiData.getPm25ValueAsInteger())
                        .o3Value(apiData.getO3ValueAsDouble())
                        .no2Value(apiData.getNo2ValueAsDouble())
                        .coValue(apiData.getCoValueAsDouble())
                        .so2Value(apiData.getSo2ValueAsDouble())
                        .khaiValue(apiData.getKhaiValueAsInteger())
                        .khaiGrade(apiData.getKhaiGrade())
                        .pm1024hAvg(apiData.getPm1024hAvgAsInteger())
                        .pm2524hAvg(apiData.getPm2524hAvgAsInteger())
                        .build();
                    
                    airQualitiesToSave.add(newAirQuality);
                    totalSaved++;
                }
            } catch (Exception e) {
                log.warn("대기질 데이터 처리 중 오류 발생: {}, 데이터: {}", e.getMessage(), apiData.getMsrSteNm());
                totalSkipped++;
            }
        }

        // 일괄 저장
        if (!airQualitiesToSave.isEmpty()) {
            repository.saveAll(airQualitiesToSave);
        }

        log.info("대기질 정보 배치 처리 완료 - 조회: {}, 저장: {}, 업데이트: {}, 스킵: {}", 
            totalFetched, totalSaved, totalUpdated, totalSkipped);

        return AirQualityBatchResult.success(totalFetched, totalSaved, totalUpdated, totalSkipped);
    }
}
