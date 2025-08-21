# 트리거 시스템 가이드

> UrbanPing의 핵심 기능인 실시간 알림 트리거 시스템 개발 및 운영 가이드

## 📋 트리거 시스템 개요

트리거 시스템은 **사용자 위치, 환경 데이터, 시설 상태** 등을 실시간으로 모니터링하여 조건에 맞는 알림을 자동으로 발송하는 UrbanPing의 핵심 기능입니다.

### 🎯 주요 특징
- **실시간 모니터링**: 5분~1시간 주기로 데이터 체크
- **위치 기반 트리거**: 사용자 위치 변경 시 자동 평가
- **확장 가능한 아키텍처**: 새로운 트리거 타입 쉽게 추가
- **조건부 로직**: 복합 조건 및 시간 기반 트리거 지원

### ⚡ 지원하는 트리거 타입
- **환경 모니터링**: 대기질, 날씨, 자외선 지수
- **교통 정보**: 따릉이, 지하철, 교통 혼잡도
- **문화 행사**: 공연, 전시, 축제 정보
- **공공 시설**: 도서관, 체육시설, 공원 상태
- **위치 기반**: 지오펜스, 관심 지점 도달

## 📚 가이드 문서

### 🚀 빠른 시작
1. **[시스템 분석](system-analysis.md)** - 트리거 시스템 아키텍처 이해 (권장)
2. **[사용법 가이드](usage.md)** - 트리거 설정 및 사용 방법
3. **[개발 가이드](development.md)** - 새로운 트리거 개발 방법

### 📖 상세 가이드

#### 1. [시스템 분석](system-analysis.md)
- 트리거 시스템 전체 아키텍처
- 핵심 컴포넌트 및 데이터 플로우
- 성능 최적화 및 확장성 고려사항
- 디자인 패턴 및 설계 원칙

#### 2. [사용법 가이드](usage.md)
- 트리거 설정 및 관리 방법
- 사용자 인터페이스 사용법
- 트리거 조건 설정 가이드
- 알림 수신 및 관리

#### 3. [개발 가이드](development.md)
- 새로운 트리거 타입 개발
- 트리거 전략 패턴 구현
- 테스트 및 디버깅 방법
- 배포 및 운영 가이드

## 🏗️ 시스템 아키텍처

### 핵심 컴포넌트
```
┌─────────────────────────────────────────────────────────────┐
│                    Trigger Evaluation Engine                │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  Location       │  │  Environment    │  │  Event          │ │
│  │  Triggers       │  │  Triggers       │  │  Triggers       │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                    Data Collection Layer                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  Seoul API      │  │  User Location  │  │  Facility       │ │
│  │  Integration    │  │  Tracking       │  │  Status         │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                    Notification System                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  Push           │  │  In-App         │  │  Email          │ │
│  │  Notifications  │  │  Notifications  │  │  Notifications  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 트리거 평가 플로우
1. **데이터 수집**: 외부 API 및 사용자 위치 정보 수집
2. **조건 평가**: 사용자별 트리거 조건과 현재 데이터 비교
3. **알림 생성**: 조건 만족 시 알림 메시지 생성
4. **중복 방지**: 쿨다운 시간 및 중복 알림 필터링
5. **알림 발송**: 다양한 채널을 통한 알림 전송

## 🛠️ 개발 예제

### 새로운 트리거 구현
```java
@Component
public class AirQualityTriggerStrategy implements TriggerStrategy {
    
    @Override
    public TriggerType getType() {
        return TriggerType.AIR_QUALITY;
    }
    
    @Override
    public TriggerEvaluationResult evaluate(TriggerContext context) {
        // 1. 현재 대기질 데이터 조회
        AirQualityData currentData = airQualityService
            .getCurrentData(context.getLocation());
        
        // 2. 사용자 설정 조건과 비교
        TriggerCondition condition = context.getCondition();
        boolean triggered = evaluateCondition(currentData, condition);
        
        // 3. 결과 반환
        return TriggerEvaluationResult.builder()
            .triggered(triggered)
            .currentValue(currentData.getPm25())
            .message(generateMessage(currentData))
            .build();
    }
}
```

### 트리거 설정 예제
```javascript
// 대기질 트리거 설정
const airQualityTrigger = {
  type: 'AIR_QUALITY',
  name: '출근길 대기질 체크',
  condition: {
    threshold: 80,
    operator: 'GREATER_THAN',
    parameter: 'PM2.5'
  },
  location: {
    latitude: 37.5665,
    longitude: 126.9780,
    radius: 1000
  },
  schedule: {
    enabled: true,
    startTime: '07:00',
    endTime: '09:00',
    days: ['MON', 'TUE', 'WED', 'THU', 'FRI']
  }
};
```

## 📊 트리거 성능 및 모니터링

### 성능 지표
- **평가 시간**: 평균 50ms 이하
- **처리량**: 초당 1000개 트리거 평가
- **정확도**: 99.9% 조건 평가 정확도
- **지연시간**: 실시간 데이터 기준 5분 이내 알림

### 모니터링 대시보드
- 트리거별 발동 횟수 및 성공률
- 사용자별 알림 수신 통계
- 시스템 성능 및 오류 모니터링
- 외부 API 응답 시간 추적

## 🔧 설정 및 튜닝

### 환경 설정
```yaml
trigger:
  evaluation:
    batch-size: 100
    thread-pool-size: 10
    timeout: 30s
  
  scheduling:
    air-quality: "0 */5 * * * *"    # 5분마다
    bike-status: "0 */3 * * * *"    # 3분마다
    cultural-events: "0 0 6 * * *"  # 매일 오전 6시
  
  cooldown:
    default: 30m
    air-quality: 1h
    emergency: 5m
```

### 성능 튜닝
- **배치 처리**: 여러 트리거를 묶어서 처리
- **캐싱**: 외부 API 응답 캐싱으로 성능 향상
- **비동기 처리**: 논블로킹 I/O로 처리량 증대
- **데이터베이스 최적화**: 인덱스 및 쿼리 최적화

## 📞 지원 및 문의

### 개발 지원
- **[트리거 개발 가이드](development.md)**: 상세한 개발 방법
- **GitHub Issues**: 트리거 관련 이슈 및 버그 리포트
- **API 문서**: [트리거 API](../../api/triggers.md)

### 운영 지원
- **[사용법 가이드](usage.md)**: 트리거 설정 및 관리
- **모니터링 대시보드**: 실시간 시스템 상태 확인
- **알림 설정**: 개인화된 알림 조건 설정

---

**업데이트**: 2025-08-21  
**버전**: v1.0.0
