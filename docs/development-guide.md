# Seoul-Fit 개발 가이드

## 프로젝트 구조

```
src/main/java/com/seoulfit/backend/
├── BackendApplication.java          # 메인 애플리케이션
├── config/                          # 설정 클래스
│   └── RestClientConfig.java
├── domain/                          # 도메인 모델
│   ├── User.java
│   ├── UserInterest.java
│   ├── UserStatus.java
│   ├── Facility.java
│   ├── FacilityAmenity.java
│   ├── CongestionData.java
│   ├── CongestionLevel.java
│   └── InterestCategory.java
├── infra/                           # 인프라 계층 (Repository)
│   ├── UserRepository.java
│   └── FacilityRepository.java
├── application/                     # 애플리케이션 계층
│   ├── service/                     # 서비스
│   │   ├── UserService.java
│   │   └── FacilityService.java
│   └── scheduler/                   # 배치/스케줄러 (예정)
└── presentation/                    # 프레젠테이션 계층
    ├── UserController.java
    └── FacilityController.java
```

## 아키텍처 패턴

### 레이어드 아키텍처
- **Presentation Layer**: REST API 컨트롤러
- **Application Layer**: 비즈니스 로직 서비스
- **Domain Layer**: 도메인 모델과 비즈니스 규칙
- **Infrastructure Layer**: 데이터 액세스 (Repository)

### 주요 설계 원칙
1. **단일 책임 원칙**: 각 클래스는 하나의 책임만 가짐
2. **의존성 역전**: 인터페이스에 의존, 구현체에 의존하지 않음
3. **도메인 중심 설계**: 비즈니스 로직을 도메인 모델에 집중

## 개발 규칙

### 1. 코딩 컨벤션
- **패키지명**: 소문자, 단수형 사용
- **클래스명**: PascalCase
- **메서드명**: camelCase
- **상수명**: UPPER_SNAKE_CASE

### 2. 엔티티 설계 규칙
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 사용
- Builder 패턴 적용
- 비즈니스 메서드를 엔티티에 포함
- 연관관계는 지연 로딩(LAZY) 사용

### 3. 서비스 계층 규칙
- `@Transactional(readOnly = true)` 기본 적용
- 쓰기 작업에만 `@Transactional` 적용
- 예외 처리는 명확한 메시지와 함께

### 4. API 설계 규칙
- RESTful API 원칙 준수
- HTTP 상태 코드 적절히 사용
- Request/Response DTO는 record 사용
- 에러 응답 형식 통일

## 데이터베이스 관련

### 1. JPA 설정
- `spring.jpa.hibernate.ddl-auto=create` (개발환경)
- `spring.jpa.open-in-view=false` (OSIV 비활성화)
- JPA Auditing 활성화 (`@EnableJpaAuditing`)

### 2. 쿼리 최적화
- N+1 문제 방지를 위한 fetch join 사용
- 적절한 인덱스 설정
- 페이징 처리 고려

## 테스트 전략

### 1. 단위 테스트
- 서비스 계층 테스트 우선
- Mockito를 활용한 의존성 모킹
- 도메인 로직 테스트

### 2. 통합 테스트
- `@SpringBootTest` 활용
- 실제 데이터베이스 연동 테스트
- API 엔드포인트 테스트

## 보안 고려사항

### 1. 인증/인가
- Spring Security + JWT 토큰 방식
- 비밀번호 암호화 (BCrypt)
- CORS 설정

### 2. 데이터 보호
- SQL Injection 방지 (JPA 사용)
- XSS 방지
- 개인정보 로깅 금지

## 성능 최적화

### 1. 데이터베이스
- 적절한 인덱스 설정
- 쿼리 최적화
- 커넥션 풀 설정

### 2. 캐싱
- Redis 활용 계획
- 실시간 혼잡도 데이터 캐싱
- API 응답 캐싱

## 배포 및 운영

### 1. 환경 분리
- 개발(dev), 스테이징(staging), 운영(prod)
- 환경별 설정 파일 분리
- 환경 변수 활용

### 2. 모니터링
- 애플리케이션 로그
- 성능 메트릭
- 에러 추적

## 다음 단계 개발 계획

### 1. 인증/인가 구현
- JWT 토큰 기반 인증
- Spring Security 설정
- 사용자 권한 관리

### 2. 공공데이터 ETL
- 서울시 공공데이터 API 연동
- 배치 처리 시스템
- 데이터 동기화

### 3. 실시간 기능
- WebSocket 연동
- 실시간 혼잡도 업데이트
- 알림 시스템

### 4. 외부 연동
- 지도 API 연동 (카카오/네이버)
- 예약 시스템 연동
- 푸시 알림 서비스
