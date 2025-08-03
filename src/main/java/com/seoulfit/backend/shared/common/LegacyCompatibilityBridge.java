package com.seoulfit.backend.shared.common;

import org.springframework.stereotype.Component;

/**
 * 기존 코드와 새로운 헥사고날 아키텍처 간의 호환성을 위한 브릿지 클래스
 * <p>
 * 점진적 마이그레이션을 위해 기존 코드와 새로운 구조 간의 연결점 역할
 *
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Component
public class LegacyCompatibilityBridge {

    /**
     * 기존 코드에서 새로운 헥사고날 구조로의 점진적 마이그레이션을 지원
     * 
     * 현재는 기존 구조를 유지하면서 새로운 구조를 병행 개발
     * 향후 단계적으로 기존 코드를 새로운 구조로 마이그레이션 예정
     */
    public void enableGradualMigration() {
        // 점진적 마이그레이션 로직
    }
}
