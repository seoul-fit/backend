# 트리거 API

> 실시간 알림 트리거 평가 및 관리

## 📋 개요

UrbanPing의 핵심 기능인 트리거 시스템을 관리하는 API입니다. 사용자 위치, 환경 데이터, 시설 상태 등을 기반으로 실시간 트리거 평가를 수행합니다.

**Base URL**: `/api/triggers`

## 🔐 인증 요구사항

모든 트리거 API는 JWT 토큰 인증이 필요합니다.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📚 API 엔드포인트

### 1. 위치 기반 트리거 평가

사용자의 현재 위치를 기반으로 모든 트리거 조건을 평가하고 알림을 발송합니다.

```http
POST /api/triggers/evaluate/location
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": "user@example.com",
  "latitude": 37.5665,
  "longitude": 126.9780,
  "radius": 2000,
  "triggerTypes": ["AIR_QUALITY", "BIKE_SHARING", "CULTURAL_EVENT"]
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "evaluatedCount": 5,
    "triggeredCount": 2,
    "location": {
      "latitude": 37.5665,
      "longitude": 126.9780,
      "address": "서울특별시 중구 명동"
    },
    "evaluationTime": "2025-01-XX 10:00:00",
    "notifications": [
      {
        "id": 123,
        "type": "AIR_QUALITY",
        "title": "대기질 악화 알림",
        "message": "현재 지역 미세먼지 농도가 높습니다.",
        "priority": "HIGH",
        "triggerCondition": {
          "threshold": 80,
          "operator": "GREATER_THAN",
          "currentValue": 85,
          "unit": "㎍/㎥"
        }
      },
      {
        "id": 124,
        "type": "BIKE_SHARING",
        "title": "따릉이 부족 알림",
        "message": "명동역 따릉이 대여소에 자전거가 부족합니다.",
        "priority": "NORMAL",
        "triggerCondition": {
          "threshold": 3,
          "operator": "LESS_THAN",
          "currentValue": 1,
          "unit": "대"
        }
      }
    ],
    "evaluatedTriggers": [
      {
        "type": "AIR_QUALITY",
        "triggered": true,
        "condition": "PM2.5 > 80㎍/㎥",
        "currentValue": 85
      },
      {
        "type": "BIKE_SHARING",
        "triggered": true,
        "condition": "Available bikes < 3",
        "currentValue": 1
      },
      {
        "type": "CULTURAL_EVENT",
        "triggered": false,
        "condition": "Event within 500m",
        "currentValue": "No events found"
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 사용자 트리거 히스토리 조회

사용자의 트리거 발동 히스토리를 조회합니다.

```http
GET /api/triggers/history?page=0&size=20
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": [
    {
      "evaluationId": "eval_123456",
      "evaluatedCount": 5,
      "triggeredCount": 2,
      "location": {
        "latitude": 37.5665,
        "longitude": 126.9780,
        "address": "서울특별시 중구 명동"
      },
      "evaluationTime": "2025-01-XX 10:00:00",
      "notifications": [
        {
          "type": "AIR_QUALITY",
          "title": "대기질 악화 알림",
          "triggered": true
        }
      ]
    },
    {
      "evaluationId": "eval_123455",
      "evaluatedCount": 3,
      "triggeredCount": 0,
      "location": {
        "latitude": 37.5665,
        "longitude": 126.9780,
        "address": "서울특별시 중구 명동"
      },
      "evaluationTime": "2025-01-XX 09:30:00",
      "notifications": []
    }
  ],
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 트리거 설정 조회

사용자의 현재 트리거 설정을 조회합니다.

```http
GET /api/triggers/settings?userId=1
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "userId": 1,
    "activeTriggersCount": 8,
    "triggers": [
      {
        "id": 1,
        "type": "AIR_QUALITY",
        "name": "대기질 모니터링",
        "description": "미세먼지 농도가 높을 때 알림",
        "enabled": true,
        "condition": {
          "threshold": 80,
          "operator": "GREATER_THAN",
          "unit": "㎍/㎥"
        },
        "location": {
          "latitude": 37.5665,
          "longitude": 126.9780,
          "radius": 2000,
          "address": "서울특별시 중구 명동"
        },
        "schedule": {
          "enabled": true,
          "startTime": "06:00",
          "endTime": "22:00",
          "days": ["MON", "TUE", "WED", "THU", "FRI"]
        }
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 4. 트리거 설정 생성

새로운 트리거를 생성합니다.

```http
POST /api/triggers/settings
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "type": "AIR_QUALITY",
  "name": "대기질 모니터링",
  "description": "미세먼지 농도가 높을 때 알림",
  "condition": {
    "threshold": 80,
    "operator": "GREATER_THAN"
  },
  "location": {
    "latitude": 37.5665,
    "longitude": 126.9780,
    "radius": 2000,
    "address": "서울특별시 중구 명동"
  },
  "schedule": {
    "enabled": true,
    "startTime": "06:00",
    "endTime": "22:00",
    "days": ["MON", "TUE", "WED", "THU", "FRI"]
  }
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "userId": 1,
    "type": "AIR_QUALITY",
    "name": "대기질 모니터링",
    "enabled": true,
    "createdAt": "2025-01-XX 10:00:00"
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 5. 트리거 설정 수정

기존 트리거 설정을 수정합니다.

```http
PUT /api/triggers/settings/{triggerId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "수정된 대기질 모니터링",
  "condition": {
    "threshold": 70,
    "operator": "GREATER_THAN"
  },
  "enabled": true
}
```

### 6. 트리거 설정 삭제

트리거 설정을 삭제합니다.

```http
DELETE /api/triggers/settings/{triggerId}
Authorization: Bearer {token}
```

## 🎯 트리거 타입

### 환경 모니터링
- `AIR_QUALITY`: 대기질 모니터링 (PM2.5, PM10, O3 등)
- `WEATHER_ALERT`: 기상 특보 (폭염, 한파, 강풍 등)
- `UV_INDEX`: 자외선 지수 모니터링
- `TEMPERATURE`: 온도 임계값 모니터링

### 교통 및 이동
- `BIKE_SHARING`: 따릉이 대여소 상태 모니터링
- `SUBWAY_DELAY`: 지하철 지연 모니터링
- `TRAFFIC_CONGESTION`: 교통 혼잡도 모니터링
- `PARKING_AVAILABILITY`: 주차장 가용성 모니터링

### 문화 및 이벤트
- `CULTURAL_EVENT`: 문화행사 시작/종료 알림
- `TICKET_AVAILABILITY`: 티켓 예매 가능 알림
- `EVENT_REMINDER`: 행사 참여 리마인더
- `EXHIBITION_NEW`: 새로운 전시 오픈 알림

### 공공 시설
- `FACILITY_STATUS`: 시설 운영 상태 변경
- `PROGRAM_REGISTRATION`: 프로그램 신청 오픈
- `LIBRARY_BOOK`: 도서 대출 가능 알림
- `SPORTS_FACILITY`: 체육시설 예약 가능 알림

### 위치 기반
- `GEOFENCE_ENTER`: 특정 지역 진입 시
- `GEOFENCE_EXIT`: 특정 지역 이탈 시
- `NEARBY_POI`: 관심 지점 근처 도달 시
- `LOCATION_REMINDER`: 위치 기반 리마인더

## 📝 요청/응답 스키마

### LocationTriggerRequest
```json
{
  "userId": "string",              // 필수: 사용자 ID
  "latitude": 37.5665,            // 필수: 위도
  "longitude": 126.9780,          // 필수: 경도
  "radius": 2000,                 // 선택: 반경 (미터, 기본값: 1000)
  "triggerTypes": ["AIR_QUALITY"] // 선택: 평가할 트리거 타입 (전체 평가 시 null)
}
```

### TriggerCondition
```json
{
  "threshold": 80,                // 임계값
  "operator": "GREATER_THAN",     // 비교 연산자
  "unit": "㎍/㎥",                // 단위
  "currentValue": 85,             // 현재값 (응답 시)
  "dataSource": "seoul_api",      // 데이터 소스
  "lastUpdated": "2025-01-XX"     // 마지막 업데이트 시간
}
```

### TriggerSchedule
```json
{
  "enabled": true,                           // 스케줄 사용 여부
  "startTime": "06:00",                     // 시작 시간
  "endTime": "22:00",                       // 종료 시간
  "days": ["MON", "TUE", "WED"],            // 요일 (MON~SUN)
  "timezone": "Asia/Seoul",                 // 시간대
  "excludeHolidays": false                  // 공휴일 제외 여부
}
```

## ⚙️ 트리거 평가 엔진

### 평가 우선순위
1. **긴급 트리거**: 기상특보, 재해 경보
2. **높은 우선순위**: 대기질, 교통 지연
3. **보통 우선순위**: 문화행사, 시설 정보
4. **낮은 우선순위**: 일반 정보, 리마인더

### 평가 주기
- **실시간**: 사용자 위치 변경 시
- **정기**: 5분, 15분, 30분, 1시간 간격
- **이벤트 기반**: 외부 API 데이터 변경 시
- **수동**: 사용자 요청 시

### 중복 방지
- **쿨다운 시간**: 동일 트리거 재발동 방지 (기본 30분)
- **위치 기반 중복**: 반경 내 동일 트리거 통합
- **시간 기반 중복**: 짧은 시간 내 동일 알림 방지

## 🔧 고급 기능

### 1. 조건부 트리거

복합 조건을 가진 트리거 설정:

```json
{
  "type": "COMPLEX_CONDITION",
  "conditions": [
    {
      "type": "AIR_QUALITY",
      "threshold": 80,
      "operator": "GREATER_THAN"
    },
    {
      "type": "WEATHER",
      "condition": "RAIN",
      "operator": "EQUAL"
    }
  ],
  "logic": "AND"  // AND, OR, NOT
}
```

### 2. 시간 기반 트리거

특정 시간에 발동하는 트리거:

```json
{
  "type": "TIME_BASED",
  "schedule": {
    "type": "CRON",
    "expression": "0 8 * * MON-FRI",
    "action": "CHECK_AIR_QUALITY"
  }
}
```

### 3. 연쇄 트리거

하나의 트리거가 다른 트리거를 발동:

```json
{
  "type": "CHAIN_TRIGGER",
  "primaryTrigger": "AIR_QUALITY_HIGH",
  "chainedTriggers": [
    {
      "type": "INDOOR_ACTIVITY_SUGGEST",
      "delay": 300  // 5분 후 발동
    }
  ]
}
```

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| TRIGGER_001 | 유효하지 않은 위치 정보 | 잘못된 위도/경도 값 |
| TRIGGER_002 | 지원하지 않는 트리거 타입 | 존재하지 않는 트리거 타입 |
| TRIGGER_003 | 트리거 평가 실패 | 외부 API 오류로 인한 평가 실패 |
| TRIGGER_004 | 트리거 설정 오류 | 잘못된 조건 또는 스케줄 설정 |
| TRIGGER_005 | 트리거 개수 초과 | 사용자당 최대 트리거 개수 초과 |
| TRIGGER_006 | 권한 없음 | 다른 사용자의 트리거 접근 시도 |

## 📋 사용 예시

### 1. 위치 기반 실시간 트리거 평가

```javascript
const evaluateLocationTriggers = async (userId, location) => {
  const response = await fetch('/api/triggers/evaluate/location', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      userId,
      latitude: location.latitude,
      longitude: location.longitude,
      radius: 2000,
      triggerTypes: ['AIR_QUALITY', 'BIKE_SHARING', 'CULTURAL_EVENT']
    })
  });
  
  const result = await response.json();
  
  // 발동된 알림 처리
  if (result.data.triggeredCount > 0) {
    result.data.notifications.forEach(notification => {
      showNotification(notification);
    });
  }
  
  return result;
};

// 위치 변경 시 자동 평가
navigator.geolocation.watchPosition(async (position) => {
  const location = {
    latitude: position.coords.latitude,
    longitude: position.coords.longitude
  };
  
  await evaluateLocationTriggers(userId, location);
});
```

### 2. 트리거 설정 관리

```javascript
// 트리거 설정 생성
const createTrigger = async (triggerConfig) => {
  const response = await fetch('/api/triggers/settings', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(triggerConfig)
  });
  return response.json();
};

// 대기질 트리거 설정 예시
const airQualityTrigger = {
  userId: 1,
  type: 'AIR_QUALITY',
  name: '출근길 대기질 체크',
  description: '출근 시간에 대기질이 나쁘면 알림',
  condition: {
    threshold: 80,
    operator: 'GREATER_THAN'
  },
  location: {
    latitude: 37.5665,
    longitude: 126.9780,
    radius: 1000,
    address: '회사 근처'
  },
  schedule: {
    enabled: true,
    startTime: '07:00',
    endTime: '09:00',
    days: ['MON', 'TUE', 'WED', 'THU', 'FRI']
  }
};

await createTrigger(airQualityTrigger);
```

### 3. 실시간 트리거 모니터링

```javascript
// React Hook 예시
const useTriggerMonitoring = (userId) => {
  const [triggerHistory, setTriggerHistory] = useState([]);
  const [activeTriggers, setActiveTriggers] = useState([]);

  // 트리거 히스토리 조회
  const fetchTriggerHistory = async () => {
    const response = await fetch('/api/triggers/history?page=0&size=10', {
      headers: { 'Authorization': `Bearer ${accessToken}` }
    });
    const data = await response.json();
    setTriggerHistory(data.data);
  };

  // 활성 트리거 조회
  const fetchActiveTriggers = async () => {
    const response = await fetch(`/api/triggers/settings?userId=${userId}`, {
      headers: { 'Authorization': `Bearer ${accessToken}` }
    });
    const data = await response.json();
    setActiveTriggers(data.data.triggers);
  };

  // 트리거 토글
  const toggleTrigger = async (triggerId, enabled) => {
    await fetch(`/api/triggers/settings/${triggerId}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ enabled })
    });
    
    setActiveTriggers(prev => 
      prev.map(trigger => 
        trigger.id === triggerId 
          ? { ...trigger, enabled }
          : trigger
      )
    );
  };

  useEffect(() => {
    fetchTriggerHistory();
    fetchActiveTriggers();
  }, [userId]);

  return {
    triggerHistory,
    activeTriggers,
    toggleTrigger,
    refetch: () => {
      fetchTriggerHistory();
      fetchActiveTriggers();
    }
  };
};
```

### 4. 지오펜스 트리거 설정

```javascript
const setupGeofenceTrigger = async (userId, location, action) => {
  const geofenceTrigger = {
    userId,
    type: 'GEOFENCE_ENTER',
    name: `${location.name} 도착 알림`,
    description: `${location.name}에 도착하면 주변 정보를 확인합니다.`,
    condition: {
      action: action, // 'CHECK_AIR_QUALITY', 'FIND_PARKING', etc.
    },
    location: {
      latitude: location.latitude,
      longitude: location.longitude,
      radius: location.radius || 100,
      address: location.address
    },
    schedule: {
      enabled: true,
      startTime: '00:00',
      endTime: '23:59',
      days: ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
    }
  };

  return await createTrigger(geofenceTrigger);
};

// 사용 예시: 회사 도착 시 주변 점심 맛집 추천
await setupGeofenceTrigger(userId, {
  name: '회사',
  latitude: 37.5665,
  longitude: 126.9780,
  radius: 200,
  address: '서울특별시 중구 명동'
}, 'RECOMMEND_RESTAURANTS');
```

---

**업데이트**: 2025-01-XX  
**버전**: v1.0.0
