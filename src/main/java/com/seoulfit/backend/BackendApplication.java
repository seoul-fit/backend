package com.seoulfit.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * UrbanPing 백엔드 애플리케이션의 메인 클래스입니다.
 * 
 * <p>서울시 공공 데이터를 활용한 실시간 도시 정보 알림 시스템의 Spring Boot 애플리케이션을 시작합니다.
 * JPA Auditing과 스케줄링 기능을 활성화하여 데이터 추적 및 정기적인 작업 실행을 지원합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>날씨 기반 알림 (폭염, 한파, 대기질 악화)</li>
 *   <li>따릉이 현황 모니터링</li>
 *   <li>문화행사 정보 제공</li>
 *   <li>인구 혼잡도 정보</li>
 *   <li>위치 기반 맞춤 서비스</li>
 * </ul>
 * 
 * @author Seoul Fit
 * @version 1.0.0
 * @since 2025-01-01
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class BackendApplication {

    /**
     * 애플리케이션의 진입점입니다.
     * 
     * @param args 명령행 인수
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
