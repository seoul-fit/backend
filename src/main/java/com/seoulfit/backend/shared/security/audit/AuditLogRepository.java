package com.seoulfit.backend.shared.security.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 감사 로그 리포지토리
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * 사용자명과 기간으로 조회
     */
    List<AuditLog> findByUsernameAndTimestampBetween(
        String username, 
        LocalDateTime from, 
        LocalDateTime to
    );

    /**
     * IP 주소와 기간으로 조회
     */
    List<AuditLog> findByIpAddressAndTimestampBetween(
        String ipAddress,
        LocalDateTime from,
        LocalDateTime to
    );

    /**
     * 이벤트 타입으로 조회
     */
    Page<AuditLog> findByEventType(AuditEventType eventType, Pageable pageable);

    /**
     * 실패한 로그인 시도 조회
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.username = :username " +
           "AND a.eventType = 'LOGIN_FAILURE' " +
           "AND a.timestamp > :since")
    long countFailedLoginAttempts(@Param("username") String username, 
                                 @Param("since") LocalDateTime since);

    /**
     * 보안 위반 통계
     */
    @Query("SELECT a.eventType as type, COUNT(a) as count FROM AuditLog a " +
           "WHERE a.eventType IN ('SECURITY_VIOLATION', 'SQL_INJECTION_ATTEMPT', " +
           "'XSS_ATTEMPT', 'RATE_LIMIT_EXCEEDED') " +
           "AND a.timestamp BETWEEN :from AND :to " +
           "GROUP BY a.eventType")
    List<Object[]> getViolationStats(@Param("from") LocalDateTime from, 
                                    @Param("to") LocalDateTime to);

    /**
     * 의심스러운 활동 감지
     */
    @Query("SELECT a.ipAddress, COUNT(DISTINCT a.username) as userCount " +
           "FROM AuditLog a " +
           "WHERE a.timestamp > :since " +
           "GROUP BY a.ipAddress " +
           "HAVING COUNT(DISTINCT a.username) > :threshold")
    List<Object[]> findSuspiciousIpAddresses(@Param("since") LocalDateTime since,
                                            @Param("threshold") long threshold);

    /**
     * 복잡한 검색 조건으로 조회
     */
    default Page<AuditLog> findByCriteria(AuditLogSearchCriteria criteria, Pageable pageable) {
        // 실제 구현은 QueryDSL 또는 Specification 사용
        return Page.empty();
    }

    /**
     * 타입별 보안 위반 통계
     */
    default Map<String, Long> getViolationStatsByType(LocalDateTime from, LocalDateTime to) {
        List<Object[]> stats = getViolationStats(from, to);
        return stats.stream()
            .collect(java.util.stream.Collectors.toMap(
                row -> row[0].toString(),
                row -> (Long) row[1]
            ));
    }
}

/**
 * 감사 로그 검색 조건
 */
@lombok.Data
@lombok.Builder
class AuditLogSearchCriteria {
    private String username;
    private String ipAddress;
    private AuditEventType eventType;
    private AuditResult result;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String resource;
}