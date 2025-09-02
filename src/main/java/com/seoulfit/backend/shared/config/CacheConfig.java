package com.seoulfit.backend.shared.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Cache with Caffeine 설정
 * 
 * Redis 대신 인메모리 캐시인 Caffeine을 사용하여 성능을 최적화합니다.
 * 단일 서버 환경에 최적화되어 있으며, 별도의 인프라 없이 동작합니다.
 * 
 * **캐시 전략:**
 * - SHORT_LIVED: 1분 캐시 (실시간성이 중요한 데이터)
 * - MEDIUM_LIVED: 5분 캐시 (자주 변경되는 데이터)
 * - LONG_LIVED: 1시간 캐시 (거의 변경되지 않는 데이터)
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    /**
     * 캐시 이름 상수
     */
    public static class CacheNames {
        // 단기 캐시 (1분)
        public static final String AIR_QUALITY_CURRENT = "airQualityCurrent";
        public static final String USER_LOCATION = "userLocation";
        public static final String TRIGGER_EVALUATION = "triggerEvaluation";
        
        // 중기 캐시 (5분)
        public static final String CULTURAL_EVENTS = "culturalEvents";
        public static final String LOCATION_DATA = "locationData";
        public static final String RESTAURANTS = "restaurants";
        public static final String PARKS = "parks";
        public static final String SPORTS_FACILITIES = "sportsFacilities";
        
        // 장기 캐시 (1시간)
        public static final String USER_PROFILES = "userProfiles";
        public static final String LIBRARIES = "libraries";
        public static final String COOLING_CENTERS = "coolingCenters";
        public static final String CULTURAL_SPACES = "culturalSpaces";
        public static final String DISTRICT_INFO = "districtInfo";
    }

    /**
     * 기본 캐시 매니저 설정
     * 
     * 서로 다른 TTL을 가진 여러 캐시를 관리합니다.
     */
    @Bean
    @Primary
    @Override
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        
        List<CaffeineCache> caches = new ArrayList<>();
        
        // 단기 캐시 (1분)
        caches.add(buildCache(CacheNames.AIR_QUALITY_CURRENT, 60, 100));
        caches.add(buildCache(CacheNames.USER_LOCATION, 60, 1000));
        caches.add(buildCache(CacheNames.TRIGGER_EVALUATION, 60, 500));
        
        // 중기 캐시 (5분)
        caches.add(buildCache(CacheNames.CULTURAL_EVENTS, 300, 500));
        caches.add(buildCache(CacheNames.LOCATION_DATA, 300, 200));
        caches.add(buildCache(CacheNames.RESTAURANTS, 300, 500));
        caches.add(buildCache(CacheNames.PARKS, 300, 300));
        caches.add(buildCache(CacheNames.SPORTS_FACILITIES, 300, 300));
        
        // 장기 캐시 (1시간)
        caches.add(buildCache(CacheNames.USER_PROFILES, 3600, 1000));
        caches.add(buildCache(CacheNames.LIBRARIES, 3600, 200));
        caches.add(buildCache(CacheNames.COOLING_CENTERS, 3600, 200));
        caches.add(buildCache(CacheNames.CULTURAL_SPACES, 3600, 300));
        caches.add(buildCache(CacheNames.DISTRICT_INFO, 3600, 50));
        
        cacheManager.setCaches(caches);
        cacheManager.initializeCaches();
        
        log.info("Caffeine cache manager initialized with {} caches", caches.size());
        return cacheManager;
    }

    /**
     * 개별 캐시 생성
     * 
     * @param name 캐시 이름
     * @param ttlSeconds TTL (초)
     * @param maxSize 최대 엔트리 수
     * @return CaffeineCache 인스턴스
     */
    private CaffeineCache buildCache(String name, int ttlSeconds, int maxSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .recordStats()  // 통계 수집 활성화
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maxSize)
                .build());
    }

    /**
     * 동적 TTL을 가진 캐시 매니저
     * 
     * 시간대별로 다른 캐시 전략을 적용할 때 사용합니다.
     */
    @Bean
    public CacheManager dynamicCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        
        List<CaffeineCache> caches = new ArrayList<>();
        
        // 피크 시간대를 고려한 동적 캐시
        int currentHour = java.time.LocalDateTime.now().getHour();
        boolean isPeakTime = (currentHour >= 18 && currentHour <= 22) || 
                             (currentHour >= 11 && currentHour <= 13);
        
        // 피크 시간대는 짧은 TTL
        int dynamicTtl = isPeakTime ? 30 : 180;  // 30초 vs 3분
        
        caches.add(buildCache("dynamicEvents", dynamicTtl, 200));
        caches.add(buildCache("dynamicLocation", dynamicTtl, 500));
        
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    /**
     * 커스텀 키 생성기
     * 
     * 복잡한 파라미터를 가진 메서드의 캐시 키를 생성합니다.
     */
    @Bean
    public KeyGenerator customKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName()).append(".");
            sb.append(method.getName()).append(":");
            
            for (Object param : params) {
                if (param != null) {
                    sb.append(param.toString()).append(",");
                }
            }
            
            String key = sb.toString();
            log.debug("Generated cache key: {}", key);
            return key;
        };
    }

    /**
     * 기본 키 생성기
     */
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    /**
     * 캐시 에러 핸들러
     * 
     * 캐시 오류가 발생해도 애플리케이션이 정상 동작하도록 보장합니다.
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, 
                                           org.springframework.cache.Cache cache, Object key) {
                log.warn("Cache get error - cache: {}, key: {}", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, 
                                           org.springframework.cache.Cache cache, 
                                           Object key, Object value) {
                log.warn("Cache put error - cache: {}, key: {}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, 
                                            org.springframework.cache.Cache cache, Object key) {
                log.warn("Cache evict error - cache: {}, key: {}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, 
                                            org.springframework.cache.Cache cache) {
                log.warn("Cache clear error - cache: {}", cache.getName(), exception);
            }
        };
    }

    @Override
    public CacheResolver cacheResolver() {
        return null; // 기본값 사용
    }
}