package com.seoulfit.backend.geo.infrastructure;

import com.seoulfit.backend.geo.application.port.out.ShapefileRepository;
import com.seoulfit.backend.geo.domain.AdministrativeDistrict;
import com.seoulfit.backend.geo.domain.Coordinate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CSV 기반 행정구역 조회 구현체
 * 간소화된 경계 박스(Bounding Box) 기반 조회
 */
@Slf4j
@Repository
@ConditionalOnProperty(name = "urbanping.geo.use-csv", havingValue = "true", matchIfMissing = true)
public class CsvShapefileRepositoryImpl implements ShapefileRepository {
    
    private final ResourceLoader resourceLoader;
    private List<RegionBoundary> regionBoundaries = new ArrayList<>();
    
    @Value("${urbanping.geo.csv.path:classpath:geo/seoul_comprehensive_regions.csv}")
    private String csvPath;
    
    public CsvShapefileRepositoryImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @PostConstruct
    @Override
    public void initialize() {
        try {
            log.info("CSV 기반 행정구역 데이터 초기화 시작: {}", csvPath);
            loadRegionBoundaries();
            log.info("행정구역 데이터 초기화 완료. 총 {} 개 지역", regionBoundaries.size());
        } catch (Exception e) {
            log.error("행정구역 데이터 초기화 실패", e);
            throw new RuntimeException("Failed to initialize region boundaries", e);
        }
    }
    
    @Override
    public Optional<AdministrativeDistrict> findByCoordinate(Coordinate coordinate) {
        try {
            double lat = coordinate.getLatitude();
            double lng = coordinate.getLongitude();
            
            // 각 지역의 경계 박스를 확인하여 포함되는 지역 찾기
            for (RegionBoundary boundary : regionBoundaries) {
                if (boundary.contains(lat, lng)) {
                    return Optional.of(AdministrativeDistrict.builder()
                            .adminCode(boundary.adminCode)
                            .adminName(boundary.emdName)
                            .sidoCode(boundary.sidoCode)
                            .sidoName(boundary.sidoName)
                            .sigunguCode(boundary.sigunguCode)
                            .sigunguName(boundary.sigunguName)
                            .emdCode(boundary.emdCode)
                            .emdName(boundary.emdName)
                            .build());
                }
            }
            
            // 정확히 매칭되지 않는 경우, 가장 가까운 지역 찾기
            return findNearestRegion(coordinate);
            
        } catch (Exception e) {
            log.error("좌표로 행정구역 조회 실패: {}", coordinate, e);
            return Optional.empty();
        }
    }
    
    @PreDestroy
    @Override
    public void cleanup() {
        if (regionBoundaries != null) {
            regionBoundaries.clear();
            log.info("행정구역 데이터 리소스 정리 완료");
        }
    }
    
    private void loadRegionBoundaries() throws Exception {
        regionBoundaries.clear();
        
        Resource resource = resourceLoader.getResource(csvPath);
        if (!resource.exists()) {
            log.warn("CSV 파일을 찾을 수 없음: {}", csvPath);
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 13) {
                    try {
                        RegionBoundary boundary = new RegionBoundary(
                            parts[0].trim(), // adminCode
                            parts[1].trim(), // sidoCode
                            parts[2].trim(), // sidoName
                            parts[3].trim(), // sigunguCode
                            parts[4].trim(), // sigunguName
                            parts[5].trim(), // emdCode
                            parts[6].trim(), // emdName
                            Double.parseDouble(parts[7].trim()), // minLat
                            Double.parseDouble(parts[8].trim()), // maxLat
                            Double.parseDouble(parts[9].trim()), // minLng
                            Double.parseDouble(parts[10].trim()), // maxLng
                            Double.parseDouble(parts[11].trim()), // centerLat
                            Double.parseDouble(parts[12].trim())  // centerLng
                        );
                        regionBoundaries.add(boundary);
                    } catch (NumberFormatException e) {
                        log.warn("잘못된 좌표 데이터 무시: {}", line);
                    }
                }
            }
        }
        
        log.info("지역 경계 데이터 로드 완료: {} 개", regionBoundaries.size());
    }
    
    /**
     * 좌표와 가장 가까운 지역을 찾는 메서드
     */
    private Optional<AdministrativeDistrict> findNearestRegion(Coordinate coordinate) {
        if (regionBoundaries.isEmpty()) {
            return Optional.empty();
        }
        
        double lat = coordinate.getLatitude();
        double lng = coordinate.getLongitude();
        
        RegionBoundary nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (RegionBoundary boundary : regionBoundaries) {
            double distance = calculateDistance(lat, lng, boundary.centerLat, boundary.centerLng);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = boundary;
            }
        }
        
        if (nearest != null) {
            log.debug("가장 가까운 지역 찾음: {} (거리: {:.2f}m)", nearest.emdName, minDistance);
            return Optional.of(AdministrativeDistrict.builder()
                    .adminCode(nearest.adminCode)
                    .adminName(nearest.emdName)
                    .sidoCode(nearest.sidoCode)
                    .sidoName(nearest.sidoName)
                    .sigunguCode(nearest.sigunguCode)
                    .sigunguName(nearest.sigunguName)
                    .emdCode(nearest.emdCode)
                    .emdName(nearest.emdName)
                    .build());
        }
        
        return Optional.empty();
    }
    
    /**
     * 하버사인 공식을 사용하여 두 좌표 간 거리 계산 (미터 단위)
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final double EARTH_RADIUS = 6371000; // 지구 반지름 (미터)
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * 지역 경계 정보를 담는 내부 클래스
     */
    private static class RegionBoundary {
        final String adminCode;
        final String sidoCode;
        final String sidoName;
        final String sigunguCode;
        final String sigunguName;
        final String emdCode;
        final String emdName;
        final double minLat;
        final double maxLat;
        final double minLng;
        final double maxLng;
        final double centerLat;
        final double centerLng;
        
        RegionBoundary(String adminCode, String sidoCode, String sidoName,
                      String sigunguCode, String sigunguName, String emdCode, String emdName,
                      double minLat, double maxLat, double minLng, double maxLng,
                      double centerLat, double centerLng) {
            this.adminCode = adminCode;
            this.sidoCode = sidoCode;
            this.sidoName = sidoName;
            this.sigunguCode = sigunguCode;
            this.sigunguName = sigunguName;
            this.emdCode = emdCode;
            this.emdName = emdName;
            this.minLat = minLat;
            this.maxLat = maxLat;
            this.minLng = minLng;
            this.maxLng = maxLng;
            this.centerLat = centerLat;
            this.centerLng = centerLng;
        }
        
        /**
         * 주어진 좌표가 이 지역의 경계 박스 안에 있는지 확인
         */
        boolean contains(double lat, double lng) {
            return lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng;
        }
    }
}