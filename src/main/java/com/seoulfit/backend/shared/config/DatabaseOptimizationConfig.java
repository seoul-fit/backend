package com.seoulfit.backend.shared.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 데이터베이스 성능 최적화를 위한 설정 클래스
 * 
 * HikariCP 연결 풀 최적화, 배치 처리 최적화, 캐시 설정 등을 포함합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Configuration
public class DatabaseOptimizationConfig {

    /**
     * 프로덕션 환경용 최적화된 HikariCP 데이터소스를 구성합니다.
     * 
     * 주요 최적화 사항:
     * - Connection Pool 크기 최적화
     * - MySQL/PostgreSQL 배치 처리 최적화
     * - PreparedStatement 캐시 최적화
     * - Connection 타임아웃 최적화
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "production")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource optimizedDataSource() {
        HikariConfig config = new HikariConfig();
        
        // 기본 연결 정보는 application.yml에서 주입
        // Connection Pool 최적화
        config.setMaximumPoolSize(20);              // 최대 연결 수
        config.setMinimumIdle(5);                   // 최소 유휴 연결 수
        config.setConnectionTimeout(30000);         // 30초
        config.setIdleTimeout(600000);              // 10분
        config.setMaxLifetime(1800000);             // 30분
        config.setLeakDetectionThreshold(60000);    // 1분
        
        // MySQL/PostgreSQL 성능 최적화
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        return new HikariDataSource(config);
    }

    /**
     * 개발 환경용 디버깅이 용이한 데이터소스를 구성합니다.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "development", matchIfMissing = true)
    public DataSource developmentDataSource() {
        HikariConfig config = new HikariConfig();
        
        // 개발 환경에서는 연결 수를 제한하고 디버깅 정보 활성화
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        config.setLeakDetectionThreshold(30000);    // 30초 (개발용으로 짧게)
        
        return new HikariDataSource(config);
    }
}