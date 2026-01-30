# Seoul Fit 기여 가이드 (Contributing Guide)

Seoul Fit 프로젝트에 기여해주셔서 감사합니다! 이 문서는 프로젝트에 효과적으로 기여할 수 있도록 도와드립니다.

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [개발 환경 설정](#개발-환경-설정)
- [아키텍처 이해](#아키텍처-이해)
- [기여 방법](#기여-방법)
- [코딩 컨벤션](#코딩-컨벤션)
- [테스트 가이드](#테스트-가이드)
- [Pull Request 가이드](#pull-request-가이드)

## 🎯 프로젝트 개요

Seoul Fit은 서울시 공공 데이터를 활용한 실시간 도시 정보 알림 시스템입니다.

### 핵심 기능
- **실시간 트리거 시스템**: 다양한 조건 기반 알림 발송
- **공공 데이터 연동**: 서울시 Open API 활용
- **사용자 맞춤 알림**: 관심사 및 위치 기반 필터링
- **확장 가능한 아키텍처**: 플러그인 형태의 기능 추가

### 기술 스택
- **Backend**: Java 21, Spring Boot 3.x
- **Database**: H2 (개발), PostgreSQL (운영)
- **Architecture**: Hexagonal Architecture, DDD
- **Patterns**: Strategy, Observer, Factory, Template Method

## 🛠 개발 환경 설정

### 필수 요구사항
- Java 21 이상
- Maven 3.8 이상
- IDE (IntelliJ IDEA 권장)

### 프로젝트 설정
```bash
# 1. 저장소 클론
git clone https://github.com/seoul-fit/backend.git
cd seoul-fit

# 2. 의존성 설치
mvn clean install

# 3. 애플리케이션 실행
mvn spring-boot:run

# 4. 브라우저에서 확인
# http://localhost:8080/swagger-ui.html
```

### 환경 변수 설정
```bash
# .env 파일 생성 (선택사항)
SEOUL_API_KEY=your-seoul-api-key
JWT_SECRET=your-jwt-secret-key
```

## 🏗 아키텍처 이해

### Hexagonal Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Controllers   │  │   Event Listeners│                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                         │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Use Cases     │  │   Services      │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Entities      │  │   Domain Events │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                 Infrastructure Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │  Repositories   │  │  External APIs  │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

### 트리거 시스템 구조
```
TriggerManager
    ├── TriggerStrategy (Interface)
    │   ├── TemperatureTriggerStrategy
    │   ├── AirQualityTriggerStrategy
    │   ├── BikeShareTriggerStrategy
    │   └── [Your Custom Strategy]
    └── TriggerContext & TriggerResult
```

## 🤝 기여 방법

### 1. 새로운 트리거 전략 추가

가장 쉬운 기여 방법입니다!

```java
@Component
public class CustomTriggerStrategy implements TriggerStrategy {
    
    @Override
    public TriggerResult evaluate(TriggerContext context) {
        // 1. 사용자 관심사 확인
        if (!context.getUserInterests().contains(InterestCategory.YOUR_CATEGORY)) {
            return TriggerResult.notTriggered();
        }
        
        // 2. 공공 API 데이터에서 필요한 정보 추출
        YourDataType data = extractYourData(context);
        
        // 3. 트리거 조건 평가
        if (shouldTrigger(data)) {
            return TriggerResult.triggered(
                NotificationType.YOUR_TYPE,
                TriggerCondition.YOUR_CONDITION,
                "알림 제목",
                "알림 메시지",
                "위치 정보"
            );
        }
        
        return TriggerResult.notTriggered();
    }
    
    @Override
    public String getSupportedTriggerType() {
        return "YOUR_TRIGGER_TYPE";
    }
    
    @Override
    public String getDescription() {
        return "당신의 트리거 전략 설명";
    }
}
```

### 2. 새로운 공공 API 연동

```java
// PublicDataApiClient.java에 메서드 추가
public Mono<Map<String, Object>> getYourApiData(int startIndex, int endIndex) {
    String cacheKey = String.format("yourapi_%d_%d", startIndex, endIndex);
    
    // 캐시 확인
    CachedResponse cached = responseCache.get(cacheKey);
    if (cached != null && !cached.isExpired(Duration.ofMinutes(10))) {
        return Mono.just(cached.data);
    }
    
    String url = String.format("%s/%s/json/YourServiceName/%d/%d/", 
                              baseUrl, apiKey, startIndex, endIndex);
    
    return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToString()
            .map(this::parseJsonResponse)
            .doOnSuccess(data -> responseCache.put(cacheKey, new CachedResponse(data)));
}
```

### 3. 새로운 도메인 모델 추가

```java
@Entity
@Table(name = "your_entities")
public class YourEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 필드 정의
    
    // 도메인 로직 메서드
    public void yourDomainMethod() {
        // 비즈니스 로직
    }
}
```

### 4. 설정 추가

새로운 기능에 대한 설정을 `application.yml`에 추가:

```yaml
seoul-fit:
  trigger:
    your-feature:
      threshold: 100
      enabled: true
      custom-property: "value"
```

## 📝 코딩 컨벤션

### Java 코딩 스타일
- **패키지명**: 소문자, 점으로 구분 (`com.seoulfit.backend.trigger`)
- **클래스명**: PascalCase (`TriggerStrategy`)
- **메서드명**: camelCase (`evaluateTrigger`)
- **상수명**: UPPER_SNAKE_CASE (`MAX_RETRY_COUNT`)

### 주석 작성
```java
/**
 * 클래스 설명
 * 
 * 상세한 설명과 사용 예시
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public class YourClass {
    
    /**
     * 메서드 설명
     * 
     * @param parameter 파라미터 설명
     * @return 반환값 설명
     */
    public ReturnType yourMethod(ParameterType parameter) {
        // 구현
    }
}
```

### 로깅 가이드
```java
@Slf4j
public class YourClass {
    
    public void yourMethod() {
        log.debug("디버그 정보: parameter={}", parameter);
        log.info("중요한 정보: result={}", result);
        log.warn("경고 상황: issue={}", issue);
        log.error("오류 발생: error={}", error, exception);
    }
}
```

## 🧪 테스트 가이드

### 단위 테스트 작성
```java
@ExtendWith(MockitoExtension.class)
class YourClassTest {
    
    @Mock
    private DependencyClass dependency;
    
    @InjectMocks
    private YourClass yourClass;
    
    @Test
    @DisplayName("성공 케이스 테스트")
    void shouldSucceedWhenValidInput() {
        // given
        given(dependency.someMethod()).willReturn(expectedValue);
        
        // when
        Result result = yourClass.yourMethod(input);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(expectedValue);
    }
}
```

### 통합 테스트 작성
```java
@SpringBootTest
@TestPropertySource(properties = {
    "seoul-fit.scheduler.enabled=false"
})
class YourIntegrationTest {
    
    @Autowired
    private YourService yourService;
    
    @Test
    void shouldIntegrateCorrectly() {
        // 통합 테스트 로직
    }
}
```

## 📬 Pull Request 가이드

### PR 제출 전 체크리스트
- [ ] 코드가 컴파일되고 테스트가 통과하는가?
- [ ] 새로운 기능에 대한 테스트를 작성했는가?
- [ ] 코딩 컨벤션을 준수했는가?
- [ ] 문서를 업데이트했는가?
- [ ] 커밋 메시지가 명확한가?

### 커밋 메시지 형식
```
type(scope): subject

body

footer
```

**타입:**
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 코드 스타일 변경
- `refactor`: 리팩토링
- `test`: 테스트 추가/수정
- `chore`: 빌드 프로세스 또는 도구 변경

**예시:**
```
feat(trigger): add weather alert trigger strategy

- Add TemperatureTriggerStrategy for high/low temperature alerts
- Support configurable temperature thresholds
- Include location-based filtering

Closes #123
```

### PR 템플릿
```markdown
## 변경 사항
- [ ] 새로운 기능 추가
- [ ] 버그 수정
- [ ] 리팩토링
- [ ] 문서 업데이트

## 설명
변경 사항에 대한 상세한 설명

## 테스트
- [ ] 단위 테스트 추가/수정
- [ ] 통합 테스트 추가/수정
- [ ] 수동 테스트 완료

## 체크리스트
- [ ] 코드 리뷰 준비 완료
- [ ] 문서 업데이트 완료
- [ ] 테스트 통과 확인
```

## 🎯 기여 아이디어

### 초급자를 위한 기여
1. **새로운 트리거 전략 추가**
   - 강수량 기반 알림
   - 지하철 혼잡도 알림
   - 주차장 만차 알림

2. **설정 개선**
   - 새로운 임계값 설정
   - 알림 메시지 템플릿 개선

3. **테스트 코드 추가**
   - 기존 기능의 테스트 커버리지 향상

### 중급자를 위한 기여
1. **새로운 공공 API 연동**
   - 교통 정보 API
   - 복지 정보 API
   - 안전 정보 API

2. **성능 최적화**
   - 캐시 전략 개선
   - 데이터베이스 쿼리 최적화

3. **모니터링 기능 추가**
   - 메트릭스 수집
   - 헬스 체크 개선

### 고급자를 위한 기여
1. **아키텍처 개선**
   - 마이크로서비스 분리
   - 이벤트 소싱 도입

2. **확장성 개선**
   - 분산 처리 시스템
   - 메시지 큐 도입

3. **보안 강화**
   - API 보안 개선
   - 데이터 암호화

## 🆘 도움이 필요한 경우

- **이슈 등록**: [GitHub Issues](https://github.com/seoul-fit/backend/issues)
- **토론**: [GitHub Discussions](https://github.com/seoul-fit/backend/discussions)
- **이메일**: gmavsks@gmail.com

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

---

**함께 만들어가는 Seoul Fit! 🚀**

여러분의 기여가 더 나은 도시 생활을 만들어갑니다.
