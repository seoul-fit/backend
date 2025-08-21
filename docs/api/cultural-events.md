# 문화행사 API

> 서울시 문화행사 및 공연 정보 조회

## 📋 개요

서울시 공공데이터 포탈의 문화행사 정보를 활용하여 공연, 전시, 축제 등의 문화행사 정보를 제공합니다.

**Base URL**: `/api/cultural-events`

## 📚 API 엔드포인트

### 1. 현재 진행중인 문화행사 조회

현재 진행중인 모든 문화행사를 조회합니다.

```http
GET /api/cultural-events/current?page=0&size=20&category=공연
```

**응답**
```json
{
  "status": "success",
  "data": {
    "totalCount": 156,
    "currentPage": 0,
    "totalPages": 8,
    "events": [
      {
        "id": "event_001",
        "title": "뮤지컬 레미제라블",
        "category": "공연",
        "subcategory": "뮤지컬",
        "description": "빅토르 위고의 소설을 원작으로 한 대표적인 뮤지컬",
        "venue": {
          "name": "명동예술극장",
          "address": "서울특별시 중구 명동길 35",
          "coordinates": {
            "latitude": 37.5636,
            "longitude": 126.9834
          },
          "capacity": 1200,
          "facilities": ["주차장", "휠체어석", "수유실"]
        },
        "schedule": {
          "startDate": "2025-01-15",
          "endDate": "2025-03-30",
          "showTimes": ["14:00", "19:30"],
          "closedDays": ["월요일"]
        },
        "ticketing": {
          "price": {
            "vip": 150000,
            "r": 120000,
            "s": 90000,
            "a": 60000
          },
          "bookingUrl": "https://ticket.example.com",
          "bookingPhone": "1588-1234",
          "availableSeats": 450,
          "totalSeats": 1200
        },
        "organizer": {
          "name": "서울시립극단",
          "contact": "02-1234-5678",
          "website": "https://theater.seoul.go.kr"
        },
        "images": [
          "https://example.com/poster1.jpg",
          "https://example.com/poster2.jpg"
        ],
        "tags": ["뮤지컬", "클래식", "가족관람"],
        "ageRating": "8세 이상",
        "duration": 180,
        "language": "한국어",
        "isPopular": true,
        "rating": 4.8,
        "reviewCount": 1250
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 카테고리별 문화행사 조회

특정 카테고리의 문화행사를 조회합니다.

```http
GET /api/cultural-events/category/{category}?startDate=2025-01-01&endDate=2025-01-31
```

**카테고리 목록**
- `공연`: 연극, 뮤지컬, 콘서트 등
- `전시`: 미술전시, 박물관 전시 등  
- `축제`: 문화축제, 거리축제 등
- `교육`: 문화강좌, 워크숍 등
- `영화`: 영화제, 특별상영 등

### 3. 위치 기반 문화행사 조회

사용자 위치 주변의 문화행사를 조회합니다.

```http
GET /api/cultural-events/nearby?latitude=37.5665&longitude=126.9780&radius=3.0
```

### 4. 문화행사 상세 정보 조회

특정 문화행사의 상세 정보를 조회합니다.

```http
GET /api/cultural-events/{eventId}
```

### 5. 인기 문화행사 조회

현재 인기 있는 문화행사 목록을 조회합니다.

```http
GET /api/cultural-events/popular?period=week&limit=10
```

### 6. 문화행사 검색

키워드로 문화행사를 검색합니다.

```http
GET /api/cultural-events/search?query=뮤지컬&category=공연&priceRange=50000-100000
```

### 7. 문화공간 정보 조회

문화행사가 열리는 공간 정보를 조회합니다.

```http
GET /api/cultural-spaces?type=극장&district=중구
```

**응답**
```json
{
  "status": "success",
  "data": {
    "spaces": [
      {
        "id": "space_001",
        "name": "명동예술극장",
        "type": "극장",
        "category": "공연장",
        "address": "서울특별시 중구 명동길 35",
        "coordinates": {
          "latitude": 37.5636,
          "longitude": 126.9834
        },
        "capacity": 1200,
        "facilities": {
          "parking": true,
          "wheelchairAccess": true,
          "nursery": true,
          "restaurant": true,
          "shop": true
        },
        "contact": {
          "phone": "02-1234-5678",
          "website": "https://theater.seoul.go.kr",
          "email": "info@theater.seoul.go.kr"
        },
        "transportation": {
          "subway": ["명동역 8번출구 도보 3분", "을지로입구역 5번출구 도보 5분"],
          "bus": ["100, 101, 102번 버스"],
          "parking": "지하 1-3층 (유료)"
        },
        "currentEvents": [
          {
            "title": "뮤지컬 레미제라블",
            "period": "2025-01-15 ~ 2025-03-30"
          }
        ]
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 8. 문화행사 예약 정보 조회

특정 문화행사의 예약 가능 정보를 조회합니다.

```http
GET /api/cultural-reservations/{eventId}/availability?date=2025-01-20
```

## 📝 문화행사 카테고리

### 공연 (Performance)
- **연극**: 창작연극, 번역연극, 아동연극
- **뮤지컬**: 창작뮤지컬, 라이선스뮤지컬
- **음악**: 클래식, 국악, 재즈, 팝
- **무용**: 발레, 현대무용, 한국무용
- **복합**: 복합장르 공연

### 전시 (Exhibition)
- **미술**: 회화, 조각, 설치미술
- **사진**: 사진전시, 포토페스티벌
- **디자인**: 산업디자인, 그래픽디자인
- **역사**: 역사전시, 문화재전시
- **과학**: 과학전시, 체험전시

### 축제 (Festival)
- **문화축제**: 지역문화축제, 예술축제
- **음식축제**: 전통음식, 세계음식
- **계절축제**: 봄꽃축제, 단풍축제
- **거리축제**: 거리공연, 플리마켓

## 🎫 예약 및 티켓팅

### 예약 상태
- `AVAILABLE`: 예약 가능
- `LIMITED`: 잔여석 부족 (10석 미만)
- `SOLD_OUT`: 매진
- `CLOSED`: 예약 마감
- `CANCELLED`: 공연 취소

### 가격 등급
- `VIP`: 최고급석
- `R`: 1등석  
- `S`: 2등석
- `A`: 3등석
- `B`: 4등석

### 할인 정보
- **조기예약**: 공연 1개월 전 예약 시 20% 할인
- **단체할인**: 10인 이상 15% 할인
- **학생할인**: 학생증 제시 시 30% 할인
- **시니어할인**: 65세 이상 20% 할인

## 📊 문화행사 통계

### 월별 통계 조회

```http
GET /api/cultural-events/statistics/monthly?year=2024&category=공연
```

### 인기 장르 분석

```http
GET /api/cultural-events/statistics/genres?period=6m
```

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| CULTURE_001 | 행사 정보 없음 | 해당 기간에 행사 없음 |
| CULTURE_002 | 예약 불가 | 예약 마감 또는 매진 |
| CULTURE_003 | 서울시 API 오류 | 외부 API 호출 실패 |
| CULTURE_004 | 유효하지 않은 카테고리 | 존재하지 않는 카테고리 |
| CULTURE_005 | 날짜 형식 오류 | 잘못된 날짜 형식 |

## 📋 사용 예시

### 1. 주변 문화행사 찾기

```javascript
const findNearbyEvents = async (latitude, longitude, interests) => {
  const response = await fetch(
    `/api/cultural-events/nearby?latitude=${latitude}&longitude=${longitude}&radius=3.0`
  );
  const data = await response.json();
  
  // 사용자 관심사에 맞는 행사 필터링
  const filteredEvents = data.data.events.filter(event => 
    interests.includes('CULTURE') && 
    event.category === '공연'
  );
  
  return filteredEvents;
};

// 문화행사 알림 생성
const createCulturalEventAlert = (event) => {
  return {
    title: `새로운 ${event.category}: ${event.title}`,
    message: `${event.venue.name}에서 ${event.schedule.startDate}부터 시작됩니다.`,
    actionUrl: event.ticketing.bookingUrl,
    priority: event.isPopular ? 'high' : 'normal'
  };
};
```

### 2. 티켓 예약 가능 여부 확인

```javascript
const checkTicketAvailability = async (eventId, date) => {
  const response = await fetch(
    `/api/cultural-reservations/${eventId}/availability?date=${date}`
  );
  const data = await response.json();
  
  const availability = data.data;
  
  if (availability.status === 'AVAILABLE') {
    return {
      available: true,
      seats: availability.availableSeats,
      prices: availability.prices
    };
  } else if (availability.status === 'LIMITED') {
    return {
      available: true,
      seats: availability.availableSeats,
      warning: '잔여석이 얼마 남지 않았습니다.'
    };
  } else {
    return {
      available: false,
      reason: availability.status
    };
  }
};
```

### 3. 문화행사 추천 시스템

```javascript
const recommendEvents = async (userId, preferences) => {
  // 사용자 선호도 기반 추천
  const response = await fetch('/api/cultural-events/recommendations', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      userId,
      preferences: {
        categories: preferences.categories,
        priceRange: preferences.budget,
        location: preferences.location,
        genres: preferences.genres
      }
    })
  });
  
  const recommendations = await response.json();
  
  return recommendations.data.events.map(event => ({
    ...event,
    recommendationScore: event.score,
    reasons: event.recommendationReasons
  }));
};
```

---

**업데이트**: 2025-08-21  
**버전**: v1.0.0
