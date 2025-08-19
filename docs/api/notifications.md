# 알림 API

> 실시간 도시 정보 알림 생성, 조회 및 관리

## 📋 개요

UrbanPing의 핵심 기능인 알림 시스템을 관리하는 API입니다. 사용자 맞춤형 알림 생성, 히스토리 조회, 읽음 상태 관리 등을 제공합니다.

**Base URL**: `/api/notifications`

## 🔐 인증 요구사항

모든 알림 API는 JWT 토큰 인증이 필요합니다.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📚 API 엔드포인트

### 1. 알림 생성

새로운 알림을 생성합니다.

```http
POST /api/notifications
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "notificationType": "AIR_QUALITY",
  "title": "대기질 악화 알림",
  "message": "현재 지역 미세먼지 농도가 높습니다. 외출 시 마스크를 착용하세요.",
  "triggerCondition": {
    "threshold": 80,
    "operator": "GREATER_THAN",
    "location": {
      "latitude": 37.5665,
      "longitude": 126.9780,
      "address": "서울특별시 중구 명동"
    }
  },
  "locationInfo": {
    "latitude": 37.5665,
    "longitude": 126.9780,
    "address": "서울특별시 중구 명동",
    "radius": 2000
  }
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "id": 123,
    "userId": 1,
    "notificationType": "AIR_QUALITY",
    "title": "대기질 악화 알림",
    "message": "현재 지역 미세먼지 농도가 높습니다. 외출 시 마스크를 착용하세요.",
    "isRead": false,
    "createdAt": "2025-01-XX 10:00:00",
    "triggerCondition": {
      "threshold": 80,
      "operator": "GREATER_THAN",
      "currentValue": 85
    },
    "locationInfo": {
      "latitude": 37.5665,
      "longitude": 126.9780,
      "address": "서울특별시 중구 명동"
    }
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 내 알림 히스토리 조회

사용자의 알림 히스토리를 페이지네이션으로 조회합니다.

```http
GET /api/notifications?userId=1&page=0&size=20&sort=createdAt,desc
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "content": [
      {
        "id": 123,
        "userId": 1,
        "notificationType": "AIR_QUALITY",
        "title": "대기질 악화 알림",
        "message": "현재 지역 미세먼지 농도가 높습니다.",
        "isRead": false,
        "createdAt": "2025-01-XX 10:00:00",
        "locationInfo": {
          "address": "서울특별시 중구 명동"
        }
      },
      {
        "id": 122,
        "userId": 1,
        "notificationType": "BIKE_SHARING",
        "title": "따릉이 부족 알림",
        "message": "명동역 따릉이 대여소에 자전거가 부족합니다.",
        "isRead": true,
        "createdAt": "2025-01-XX 09:30:00",
        "locationInfo": {
          "address": "서울특별시 중구 명동역"
        }
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "direction": "DESC",
        "property": "createdAt"
      }
    },
    "totalElements": 45,
    "totalPages": 3,
    "first": true,
    "last": false,
    "numberOfElements": 20
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 알림 읽음 처리

특정 알림을 읽음 처리합니다.

```http
PATCH /api/notifications/{notificationId}/read?userId=1
Authorization: Bearer {token}
```

**응답**
```http
HTTP/1.1 200 OK
```

### 4. 읽지 않은 알림 개수

사용자의 읽지 않은 알림 개수를 조회합니다.

```http
GET /api/notifications/unread-count?userId=1
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": 5,
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 5. 모든 알림 읽음 처리

사용자의 모든 알림을 읽음 처리합니다.

```http
PATCH /api/notifications/read-all?userId=1
Authorization: Bearer {token}
```

**응답**
```http
HTTP/1.1 200 OK
```

## 📝 알림 타입

### 환경 정보 알림
- `AIR_QUALITY`: 대기질 악화 알림
- `WEATHER_ALERT`: 기상 특보 (폭염, 한파, 강풍 등)
- `UV_INDEX`: 자외선 지수 경고

### 교통 정보 알림
- `BIKE_SHARING`: 따릉이 대여소 상태 (부족/포화)
- `SUBWAY_DELAY`: 지하철 지연 정보
- `TRAFFIC_CONGESTION`: 교통 혼잡 정보

### 문화 행사 알림
- `CULTURAL_EVENT`: 문화행사 시작/종료
- `TICKET_OPEN`: 티켓 예매 오픈
- `EVENT_REMINDER`: 행사 참여 리마인더

### 공공 시설 알림
- `FACILITY_CLOSURE`: 시설 휴관/폐쇄
- `PROGRAM_OPEN`: 프로그램 신청 오픈
- `RESERVATION_AVAILABLE`: 예약 가능 알림

### 위치 기반 알림
- `LOCATION_ALERT`: 특정 위치 도착 시 알림
- `NEARBY_EVENT`: 주변 이벤트 발생
- `CONGESTION_ALERT`: 지역 혼잡도 경고

## 📝 요청/응답 스키마

### CreateNotificationRequest
```json
{
  "userId": 1,                          // 필수: 사용자 ID
  "notificationType": "AIR_QUALITY",    // 필수: 알림 타입
  "title": "string",                    // 필수: 알림 제목
  "message": "string",                  // 필수: 알림 내용
  "triggerCondition": {                 // 선택: 트리거 조건
    "threshold": 80,                    // 임계값
    "operator": "GREATER_THAN",         // 비교 연산자
    "location": {                       // 위치 정보
      "latitude": 37.5665,
      "longitude": 126.9780,
      "address": "string"
    }
  },
  "locationInfo": {                     // 선택: 위치 정보
    "latitude": 37.5665,               // 위도
    "longitude": 126.9780,             // 경도
    "address": "string",               // 주소
    "radius": 2000                     // 반경 (미터)
  }
}
```

### NotificationHistoryResult
```json
{
  "id": 123,                           // 알림 ID
  "userId": 1,                         // 사용자 ID
  "notificationType": "AIR_QUALITY",   // 알림 타입
  "title": "string",                   // 제목
  "message": "string",                 // 내용
  "isRead": false,                     // 읽음 여부
  "createdAt": "2025-01-XX 10:00:00",  // 생성일시
  "readAt": "2025-01-XX 10:05:00",     // 읽은일시 (읽은 경우)
  "triggerCondition": {                // 트리거 조건
    "threshold": 80,
    "operator": "GREATER_THAN",
    "currentValue": 85
  },
  "locationInfo": {                    // 위치 정보
    "latitude": 37.5665,
    "longitude": 126.9780,
    "address": "string"
  }
}
```

### TriggerCondition
```json
{
  "threshold": 80,                     // 임계값
  "operator": "GREATER_THAN",          // 비교 연산자
  "currentValue": 85,                  // 현재값 (응답 시)
  "location": {                        // 위치 정보
    "latitude": 37.5665,
    "longitude": 126.9780,
    "address": "string"
  }
}
```

## 🎯 비교 연산자

| 연산자 | 설명 | 사용 예시 |
|--------|------|-----------|
| `GREATER_THAN` | 초과 | 미세먼지 농도 > 80 |
| `GREATER_THAN_OR_EQUAL` | 이상 | 온도 >= 35 |
| `LESS_THAN` | 미만 | 따릉이 개수 < 3 |
| `LESS_THAN_OR_EQUAL` | 이하 | 습도 <= 30 |
| `EQUAL` | 같음 | 상태 == "운영중단" |
| `NOT_EQUAL` | 다름 | 상태 != "정상" |

## 🔔 알림 우선순위

### 긴급 (URGENT)
- 기상특보, 재해 경보
- 즉시 푸시 알림 발송

### 높음 (HIGH)
- 대기질 악화, 교통 지연
- 5분 내 푸시 알림 발송

### 보통 (NORMAL)
- 문화행사, 시설 정보
- 30분 내 푸시 알림 발송

### 낮음 (LOW)
- 일반 정보, 리마인더
- 1시간 내 푸시 알림 발송

## 📊 알림 통계

### 알림 발송 현황 조회

```http
GET /api/notifications/stats?userId=1&period=7d
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "period": "7d",
    "totalNotifications": 25,
    "readNotifications": 20,
    "unreadNotifications": 5,
    "readRate": 80.0,
    "typeBreakdown": {
      "AIR_QUALITY": 8,
      "BIKE_SHARING": 6,
      "CULTURAL_EVENT": 5,
      "WEATHER_ALERT": 3,
      "FACILITY_CLOSURE": 3
    },
    "dailyStats": [
      {
        "date": "2025-01-XX",
        "count": 4,
        "readCount": 3
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| NOTIF_001 | 알림을 찾을 수 없음 | 존재하지 않는 알림 ID |
| NOTIF_002 | 권한이 없음 | 다른 사용자의 알림 접근 시도 |
| NOTIF_003 | 유효하지 않은 알림 타입 | 지원하지 않는 알림 타입 |
| NOTIF_004 | 트리거 조건 오류 | 잘못된 트리거 조건 설정 |
| NOTIF_005 | 위치 정보 오류 | 잘못된 위도/경도 값 |
| NOTIF_006 | 알림 생성 실패 | 서버 오류로 인한 알림 생성 실패 |

## 📋 사용 예시

### 1. 대기질 알림 생성

```javascript
const createAirQualityAlert = async (userId, location) => {
  const response = await fetch('/api/notifications', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      userId,
      notificationType: 'AIR_QUALITY',
      title: '대기질 악화 알림',
      message: '현재 지역 미세먼지 농도가 높습니다. 외출 시 마스크를 착용하세요.',
      triggerCondition: {
        threshold: 80,
        operator: 'GREATER_THAN',
        location
      },
      locationInfo: {
        ...location,
        radius: 2000
      }
    })
  });
  return response.json();
};
```

### 2. 알림 히스토리 조회 및 페이지네이션

```javascript
const getNotificationHistory = async (userId, page = 0, size = 20) => {
  const params = new URLSearchParams({
    userId,
    page,
    size,
    sort: 'createdAt,desc'
  });
  
  const response = await fetch(`/api/notifications?${params}`, {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  });
  return response.json();
};

// 무한 스크롤 구현
const loadMoreNotifications = async () => {
  const nextPage = currentPage + 1;
  const data = await getNotificationHistory(userId, nextPage);
  
  if (data.data.content.length > 0) {
    setNotifications(prev => [...prev, ...data.data.content]);
    setCurrentPage(nextPage);
    setHasMore(!data.data.last);
  }
};
```

### 3. 실시간 알림 상태 관리

```javascript
// React Hook 예시
const useNotifications = (userId) => {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  // 읽지 않은 알림 개수 조회
  const fetchUnreadCount = async () => {
    const response = await fetch(`/api/notifications/unread-count?userId=${userId}`, {
      headers: { 'Authorization': `Bearer ${accessToken}` }
    });
    const data = await response.json();
    setUnreadCount(data.data);
  };

  // 알림 읽음 처리
  const markAsRead = async (notificationId) => {
    await fetch(`/api/notifications/${notificationId}/read?userId=${userId}`, {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${accessToken}` }
    });
    
    setNotifications(prev => 
      prev.map(notif => 
        notif.id === notificationId 
          ? { ...notif, isRead: true, readAt: new Date().toISOString() }
          : notif
      )
    );
    setUnreadCount(prev => Math.max(0, prev - 1));
  };

  // 모든 알림 읽음 처리
  const markAllAsRead = async () => {
    await fetch(`/api/notifications/read-all?userId=${userId}`, {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${accessToken}` }
    });
    
    setNotifications(prev => 
      prev.map(notif => ({ ...notif, isRead: true, readAt: new Date().toISOString() }))
    );
    setUnreadCount(0);
  };

  useEffect(() => {
    fetchUnreadCount();
    // 주기적으로 읽지 않은 알림 개수 업데이트
    const interval = setInterval(fetchUnreadCount, 30000);
    return () => clearInterval(interval);
  }, [userId]);

  return {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead,
    fetchUnreadCount
  };
};
```

### 4. 위치 기반 알림 설정

```javascript
const setupLocationBasedAlert = async (userId, alertType, location) => {
  const alertConfigs = {
    AIR_QUALITY: {
      title: '대기질 알림',
      message: '설정한 지역의 대기질이 악화되었습니다.',
      threshold: 80,
      operator: 'GREATER_THAN'
    },
    BIKE_SHARING: {
      title: '따릉이 알림',
      message: '주변 따릉이 대여소 상태가 변경되었습니다.',
      threshold: 3,
      operator: 'LESS_THAN'
    }
  };

  const config = alertConfigs[alertType];
  
  return await fetch('/api/notifications', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      userId,
      notificationType: alertType,
      title: config.title,
      message: config.message,
      triggerCondition: {
        threshold: config.threshold,
        operator: config.operator,
        location
      },
      locationInfo: {
        ...location,
        radius: 2000
      }
    })
  });
};
```

---

**업데이트**: 2025-01-XX  
**버전**: v1.0.0
