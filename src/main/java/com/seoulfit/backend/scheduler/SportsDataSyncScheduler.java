package com.seoulfit.backend.scheduler;

import com.seoulfit.backend.publicdata.sports.application.SportsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 체육시설 데이터 동기화 스케줄러
 * 
 * 서울시 체육시설 정보를 주기적으로 동기화하고 지오코딩을 처리하는 스케줄러입니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.sports.enabled", havingValue = "true", matchIfMissing = true)
public class SportsDataSyncScheduler {

    private final SportsService sportsService;

    /**
     * 체육시설 데이터 동기화 (매일 새벽 2시)
     * 
     * 서울시 API에서 최신 체육시설 정보를 가져와 데이터베이스에 동기화합니다.
     */
    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시
    public void syncSportsData() {
        log.info("=== 체육시설 데이터 동기화 스케줄러 시작 ===");
        
        try {
            // 1. 체육시설 데이터 동기화
            int syncedCount = sportsService.syncSportsData();
            log.info("체육시설 데이터 동기화 완료: {} 건", syncedCount);
            
            // 2. 지오코딩 처리 (새로 추가된 데이터 또는 위경도 누락 데이터)
            int geoCodedCount = sportsService.processGeoCoding();
            log.info("체육시설 지오코딩 처리 완료: {} 건", geoCodedCount);
            
            // 3. 통계 정보 로깅
            SportsService.SportsStats stats = sportsService.getSportsStats();
            log.info("체육시설 현황 - 전체: {} 건, 위경도 있음: {} 건, 위경도 없음: {} 건", 
                    stats.getTotalCount(), stats.getWithLocationCount(), stats.getWithoutLocationCount());
            
        } catch (Exception e) {
            log.error("체육시설 데이터 동기화 중 오류 발생", e);
        }
        
        log.info("=== 체육시설 데이터 동기화 스케줄러 종료 ===");
    }

    /**
     * 체육시설 지오코딩 처리 (매주 일요일 새벽 3시)
     * 
     * 위경도 정보가 누락된 체육시설들의 지오코딩을 재처리합니다.
     * API 호출 제한을 고려하여 주 1회 실행합니다.
     */
    @Scheduled(cron = "0 0 3 * * SUN") // 매주 일요일 새벽 3시
    public void processGeoCoding() {
        log.info("=== 체육시설 지오코딩 처리 스케줄러 시작 ===");
        
        try {
            int processedCount = sportsService.processGeoCoding();
            log.info("체육시설 지오코딩 처리 완료: {} 건", processedCount);
            
            // 처리 후 통계 정보
            SportsService.SportsStats stats = sportsService.getSportsStats();
            double locationRate = stats.getTotalCount() > 0 ? 
                    (double) stats.getWithLocationCount() / stats.getTotalCount() * 100 : 0;
            
            log.info("체육시설 위경도 정보 완성률: {:.2f}% ({}/{})", 
                    locationRate, stats.getWithLocationCount(), stats.getTotalCount());
            
        } catch (Exception e) {
            log.error("체육시설 지오코딩 처리 중 오류 발생", e);
        }
        
        log.info("=== 체육시설 지오코딩 처리 스케줄러 종료 ===");
    }

    /**
     * 체육시설 데이터 상태 모니터링 (매시간)
     * 
     * 체육시설 데이터의 상태를 모니터링하고 이상 상황을 감지합니다.
     */
    @Scheduled(cron = "0 0 * * * *") // 매시간
    public void monitorSportsData() {
        try {
            SportsService.SportsStats stats = sportsService.getSportsStats();
            
            // 데이터가 없는 경우 경고
            if (stats.getTotalCount() == 0) {
                log.warn("체육시설 데이터가 없습니다. 데이터 동기화가 필요합니다.");
                return;
            }
            
            // 위경도 정보 완성률이 낮은 경우 경고
            double locationRate = (double) stats.getWithLocationCount() / stats.getTotalCount() * 100;
            if (locationRate < 80.0) {
                log.warn("체육시설 위경도 정보 완성률이 낮습니다: {:.2f}% - 지오코딩 처리가 필요합니다.", locationRate);
            }
            
            log.debug("체육시설 데이터 상태 - 전체: {} 건, 위경도 완성률: {:.2f}%", 
                    stats.getTotalCount(), locationRate);
            
        } catch (Exception e) {
            log.error("체육시설 데이터 모니터링 중 오류 발생", e);
        }
    }
}
