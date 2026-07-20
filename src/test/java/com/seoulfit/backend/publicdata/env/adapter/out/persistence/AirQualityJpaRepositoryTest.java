package com.seoulfit.backend.publicdata.env.adapter.out.persistence;

import com.seoulfit.backend.publicdata.env.domain.AirQuality;
import com.seoulfit.backend.shared.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
@DisplayName("AirQualityJpaRepository 보관 정책 테스트")
class AirQualityJpaRepositoryTest {

    @Autowired
    private AirQualityJpaRepository airQualityJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("보관 경계 안쪽 측정값은 보존하고 초과 측정값만 삭제한다")
    void deleteByMsrDtBeforePreservesRetentionBoundaryAndDeletesExpiredData() {
        LocalDateTime cutoff = LocalDateTime.of(2026, 6, 20, 3, 30);
        AirQuality retained = saveAirQuality("보존 측정소", cutoff.plusSeconds(1));
        AirQuality exactBoundary = saveAirQuality("경계 측정소", cutoff);
        AirQuality expired = saveAirQuality("삭제 측정소", cutoff.minusSeconds(1));
        entityManager.flush();
        entityManager.clear();

        int deletedCount = airQualityJpaRepository.deleteByMsrDtBefore(cutoff);
        entityManager.flush();
        entityManager.clear();

        assertThat(deletedCount).isEqualTo(1);
        assertThat(airQualityJpaRepository.findById(retained.getId())).isPresent();
        assertThat(airQualityJpaRepository.findById(exactBoundary.getId())).isPresent();
        assertThat(airQualityJpaRepository.findById(expired.getId())).isEmpty();
    }

    private AirQuality saveAirQuality(String stationName, LocalDateTime measuredAt) {
        return airQualityJpaRepository.save(AirQuality.builder()
                .msrDt(measuredAt)
                .msrRgnNm("중구")
                .msrSteNm(stationName)
                .pm10Value(30)
                .pm25Value(15)
                .khaiValue(50)
                .khaiGrade("좋음")
                .build());
    }
}
