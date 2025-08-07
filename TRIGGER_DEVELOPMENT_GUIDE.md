# 🎯 UrbanPing 트리거 개발 가이드

## 📋 목차

1. [트리거 시스템 개요](#-트리거-시스템-개요)
2. [새로운 트리거 전략 개발](#-새로운-트리거-전략-개발)
3. [트리거 설정 관리](#-트리거-설정-관리)
4. [테스트 가이드](#-테스트-가이드)
5. [모범 사례](#-모범-사례)
6. [문제 해결](#-문제-해결)

## 🔍 트리거 시스템 개요

### 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                    Trigger Manager                          │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ Strategy Registry│  │ Context Builder │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Trigger Strategies                       │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ Weather Trigger │  │ Location Trigger│                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Event Publishers                         │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ Notification    │  │ History Logger  │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

### 핵심 컴포넌트

1. **TriggerManager**: 트리거 전략 관리 및 실행
2. **TriggerStrategy**: 개별 트리거 로직 구현
3. **TriggerContext**: 평가에 필요한 데이터 전달
4. **TriggerResult**: 평가 결과 및 알림 정보

## 🛠 새로운 트리거 전략 개발

### 1. 트리거 전략 클래스 생성

```java
@Component
public class CustomTriggerStrategy implements TriggerStrategy {
    
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        // 트리거 로직 구현
        if (triggerConditionMet()) {
            return TriggerResult.triggered(
                NotificationType.CUSTOM,
                TriggerCondition.CUSTOM,
                "알림 제목",
                "알림 메시지",
                "위치 정보"
            );
        }
        return TriggerResult.notTriggered();
    }
    
    @Override
    public String getSupportedTriggerType() {
        return "CUSTOM_TRIGGER";
    }
    
    @Override
    public String getDescription() {
        return "사용자 정의 트리거 설명";
    }
    
    @Override
    public int getPriority() {
        return 50; // 우선순위 설정
    }
}
```

### 2. 설정 관리 추가

```java
@ConfigurationProperties(prefix = "urbanping.trigger.custom")
@Component
@Getter
@Setter
public class CustomTriggerProperties {
    private double threshold = 10.0;
    private int interval = 300;
    private boolean enabled = true;
}
```

### 3. 테스트 작성

```java
@SpringBootTest
class CustomTriggerStrategyTest {
    
    @Autowired
    private CustomTriggerStrategy strategy;
    
    @Test
    void whenConditionMet_thenTriggerFires() {
        // Given
        TriggerContext context = createTestContext();
        
        // When
        TriggerResult result = strategy.evaluate(context);
        
        // Then
        assertTrue(result.isTriggered());
        assertEquals("알림 제목", result.getTitle());
    }
}
```

## ⚙️ 트리거 설정 관리

### 설정 파일 (application.yml)

```yaml
urbanping:
  trigger:
    enabled: true
    scheduler:
      realtime-interval: 300000  # 5분
    temperature:
      high-threshold: 35.0
      low-threshold: 0.0
    air-quality:
      pm10-threshold: 100
      pm25-threshold: 50
    bike-share:
      shortage-threshold: 2
      excess-threshold: 15
```

### 동적 설정 변경

```java
@RestController
@RequestMapping("/api/triggers/config")
public class TriggerConfigController {
    
    @PutMapping("/{triggerType}/threshold")
    public ResponseEntity<Void> updateThreshold(
            @PathVariable String triggerType,
            @RequestParam double value) {
        // 설정 업데이트 로직
    }
}
```

## 🧪 테스트 가이드

### 1. 단위 테스트

- 각 트리거 전략 별 테스트 케이스 작성
- 경계값 테스트 필수
- 실패 케이스 처리 검증

### 2. 통합 테스트

```java
@SpringBootTest
class TriggerIntegrationTest {
    
    @Autowired
    private TriggerManager triggerManager;
    
    @Test
    void whenMultipleTriggersActive_thenHighestPriorityWins() {
        // 테스트 구현
    }
}
```

### 3. 성능 테스트

```java
@Test
void performanceTest() {
    int userCount = 1000;
    long startTime = System.currentTimeMillis();
    
    // 성능 테스트 로직
    
    long endTime = System.currentTimeMillis();
    assertTrue((endTime - startTime) < 5000); // 5초 이내 처리
}
```

## 💡 모범 사례

1. **우선순위 관리**
   - 긴급 알림: 0-10
   - 중요 알림: 11-30
   - 일반 알림: 31-70
   - 정보성 알림: 71-100

2. **중복 알림 방지**
   ```java
   private boolean isRecentlyTriggered(String userId, String triggerType) {
       return triggerHistoryRepository.existsRecentTrigger(
           userId, triggerType, LocalDateTime.now().minusMinutes(30)
       );
   }
   ```

3. **에러 처리**
   ```java
   try {
       // 트리거 로직
   } catch (Exception e) {
       log.error("트리거 평가 실패: {}", e.getMessage(), e);
       return TriggerResult.notTriggered();
   }
   ```

## 🔧 문제 해결

### 일반적인 문제

1. **성능 이슈**
   - 캐싱 적용
   - 배치 처리 활용
   - 비동기 처리 검토

2. **메모리 누수**
   - 주기적 리소스 정리
   - 메모리 사용량 모니터링

3. **동시성 문제**
   - 락 매커니즘 활용
   - 동시성 제어 패턴 적용

### 디버깅 팁

```java
@Slf4j
public class TriggerDebugUtil {
    public static void logTriggerContext(TriggerContext context) {
        log.debug("트리거 컨텍스트: userId={}, location=[{}, {}], interests={}",
            context.getUser().getId(),
            context.getUserLatitude(),
            context.getUserLongitude(),
            context.getUserInterests()
        );
    }
}
```

## 📊 모니터링

### 메트릭 수집

```java
@Component
public class TriggerMetrics {
    private final MeterRegistry registry;
    
    public void recordTriggerExecution(String triggerType, boolean success) {
        registry.counter("trigger.executions", 
            "type", triggerType,
            "result", success ? "success" : "failure"
        ).increment();
    }
}
```

### 대시보드 구성

1. 트리거 실행 횟수
2. 성공/실패율
3. 평균 실행 시간
4. 사용자별 트리거 발동 통계

## 🔄 배포 프로세스

1. 테스트 실행
2. 설정 검증
3. 단계적 롤아웃
4. 모니터링 강화

## 📝 문서화 체크리스트

- [ ] 트리거 설명
- [ ] 설정 파라미터
- [ ] 테스트 케이스
- [ ] API 문서
- [ ] 모니터링 지표

## 🤝 기여 가이드

1. 이슈 생성
2. 브랜치 생성
3. 코드 작성
4. 테스트 추가
5. PR 제출

## 📚 참고 자료

- [Spring Boot 문서](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [헥사고날 아키텍처](https://netflixtechblog.com/ready-for-changes-with-hexagonal-architecture-b315ec967749)
- [이벤트 기반 아키텍처](https://microservices.io/patterns/data/event-driven-architecture.html)
