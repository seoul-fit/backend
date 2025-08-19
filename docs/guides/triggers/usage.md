# 🎯 UrbanPing 트리거 사용 가이드

## 📋 개요

UrbanPing의 트리거 시스템은 두 가지 방식으로 동작합니다:

1. **시간 기반 트리거**: 스케줄러가 주기적으로 모든 사용자에 대해 트리거 조건을 평가
2. **위치 기반 실시간 트리거**: 사용자가 앱에 접속하여 위치 정보를 전달할 때 즉시 평가

## 🕐 시간 기반 트리거 (스케줄러)

### 동작 방식

```
매 5분마다 실행 (설정 가능)
├── 모든 활성 사용자 조회
├── 공공 데이터 API 호출
├── 각 사용자별 트리거 평가
└── 조건 충족 시 알림 발송
```

### 설정

```yaml
urbanping:
  scheduler:
    enabled: true
    realtime-interval: 300000  # 5분 (밀리초)
    batch-size: 100           # 배치 처리 크기
```

### 장점
- 사용자가 앱을 사용하지 않아도 알림 수신
- 일정한 간격으로 모든 사용자 모니터링
- 서버 리소스 효율적 사용

### 단점
- 실시간성 부족 (최대 5분 지연)
- 위치 정보가 없는 경우 제한적

## 📍 위치 기반 실시간 트리거

### 동작 방식

```
사용자 앱 접속
├── 위치 정보 전송
├── 실시간 공공 데이터 조회
├── 해당 위치 기반 트리거 평가
└── 즉시 알림 발송
```

### API 사용법

#### 1. 위치 기반 트리거 평가

```http
POST /api/triggers/evaluate/location
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "latitude": 37.5665,
  "longitude": 126.9780,
  "radius": 2000,
  "triggerTypes": ["TEMPERATURE", "AIR_QUALITY", "BIKE_SHARE"]
}
```

#### 2. 특정 타입 트리거 평가

```http
POST /api/triggers/evaluate/TEMPERATURE
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "latitude": 37.5665,
  "longitude": 126.9780,
  "radius": 1000
}
```

### 프론트엔드 구현 예시

```javascript
// 위치 정보 획득 후 트리거 평가
const evaluateLocationTriggers = async () => {
  try {
    // 위치 정보 획득
    const position = await getCurrentPosition();
    
    // 트리거 평가 요청
    const response = await fetch('/api/triggers/evaluate/location', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        radius: 2000,
        triggerTypes: ['TEMPERATURE', 'AIR_QUALITY', 'BIKE_SHARE']
      })
    });

    const result = await response.json();
    
    if (result.triggered) {
      // 알림 표시
      result.triggeredList.forEach(trigger => {
        showNotification(trigger.title, trigger.message);
      });
    }
  } catch (error) {
    console.error('트리거 평가 실패:', error);
  }
};

// 앱 시작 시 또는 위치 변경 시 호출
window.addEventListener('load', evaluateLocationTriggers);
```

### 장점
- 실시간 알림 (즉시 응답)
- 정확한 위치 기반 정보
- 사용자 상호작용 기반

### 단점
- 사용자가 앱을 사용해야만 동작
- API 호출 빈도에 따른 서버 부하

## 🔄 하이브리드 접근법 (권장)

### 최적의 사용 시나리오

1. **백그라운드 모니터링**: 시간 기반 트리거로 기본 알림 제공
2. **실시간 업데이트**: 사용자 접속 시 위치 기반 트리거로 즉시 알림

### 구현 전략

```javascript
class TriggerManager {
  constructor() {
    this.lastLocationUpdate = null;
    this.updateInterval = 5 * 60 * 1000; // 5분
  }

  async onAppStart() {
    // 앱 시작 시 즉시 평가
    await this.evaluateLocationTriggers();
    
    // 주기적 위치 업데이트 설정
    this.startPeriodicUpdates();
  }

  async onLocationChange(position) {
    // 위치 변경 시 즉시 평가
    await this.evaluateLocationTriggers(position);
  }

  startPeriodicUpdates() {
    setInterval(async () => {
      if (this.isAppActive()) {
        await this.evaluateLocationTriggers();
      }
    }, this.updateInterval);
  }
}
```

## 📊 트리거 타입별 특성

### 1. 온도 트리거 (TEMPERATURE)
- **실시간성**: 중요 (건강 관련)
- **권장 방식**: 하이브리드
- **업데이트 주기**: 5분

### 2. 대기질 트리거 (AIR_QUALITY)
- **실시간성**: 중요 (건강 관련)
- **권장 방식**: 하이브리드
- **업데이트 주기**: 10분

### 3. 따릉이 트리거 (BIKE_SHARE)
- **실시간성**: 매우 중요 (실시간 변동)
- **권장 방식**: 위치 기반 실시간
- **업데이트 주기**: 즉시

### 4. 문화행사 트리거 (CULTURAL_EVENT)
- **실시간성**: 낮음
- **권장 방식**: 시간 기반
- **업데이트 주기**: 1시간

## ⚙️ 설정 최적화

### 성능 최적화

```yaml
urbanping:
  trigger:
    # 배치 처리 설정
    batch:
      size: 100
      timeout: 30s
    
    # 캐시 설정
    cache:
      enabled: true
      ttl: 300s
    
    # 중복 알림 방지
    duplicate-prevention:
      enabled: true
      window: 30m
```

### 사용자 경험 최적화

```yaml
urbanping:
  trigger:
    # 우선순위 기반 제한
    max-notifications-per-session: 3
    
    # 시간대별 알림 제한
    quiet-hours:
      start: "22:00"
      end: "07:00"
    
    # 위치 기반 반경 설정
    default-radius: 2000  # 2km
    max-radius: 5000      # 5km
```

## 🔍 모니터링 및 분석

### 주요 메트릭

1. **트리거 실행 통계**
   - 실행 횟수
   - 성공/실패율
   - 평균 응답 시간

2. **사용자 참여도**
   - 알림 클릭률
   - 앱 사용 패턴
   - 위치 업데이트 빈도

### 대시보드 확인

```http
GET /api/triggers/strategies
GET /api/triggers/history?page=0&size=20
```

## 🚨 문제 해결

### 일반적인 문제

1. **알림이 오지 않는 경우**
   - 사용자 관심사 설정 확인
   - 위치 권한 확인
   - 트리거 전략 활성화 상태 확인

2. **중복 알림 발생**
   - 중복 방지 설정 확인
   - 캐시 상태 점검

3. **성능 이슈**
   - 배치 크기 조정
   - 캐시 TTL 최적화
   - API 호출 빈도 조절

### 디버깅 도구

```javascript
// 클라이언트 디버깅
const debugTrigger = async () => {
  const response = await fetch('/api/triggers/strategies');
  const strategies = await response.json();
  console.table(strategies);
};
```

## 📱 모바일 앱 통합

### React Native 예시

```javascript
import Geolocation from '@react-native-community/geolocation';

const TriggerService = {
  async evaluateLocationTriggers() {
    return new Promise((resolve, reject) => {
      Geolocation.getCurrentPosition(
        async (position) => {
          try {
            const result = await this.callTriggerAPI(position);
            resolve(result);
          } catch (error) {
            reject(error);
          }
        },
        (error) => reject(error),
        { enableHighAccuracy: true, timeout: 15000, maximumAge: 10000 }
      );
    });
  }
};
```

## 🔮 향후 개선 계획

1. **AI 기반 개인화**: 사용자 패턴 학습을 통한 맞춤형 알림
2. **예측 알림**: 날씨 예보 기반 사전 알림
3. **소셜 기능**: 친구들과 트리거 정보 공유
4. **IoT 연동**: 스마트 기기와의 연동

이 가이드를 통해 UrbanPing의 트리거 시스템을 효과적으로 활용하시기 바랍니다!
