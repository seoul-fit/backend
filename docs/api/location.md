# 위치기반 API

> 사용자 위치 기반 주변 시설 검색 및 개인화 추천

## 📋 개요

사용자의 현재 위치를 기반으로 주변 시설 정보를 제공하고, 사용자 관심사에 따른 맞춤형 추천을 제공하는 API입니다.

**Base URL**: `/api/location`

## 🔐 인증 요구사항

모든 위치기반 API는 JWT 토큰 인증이 필요합니다.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📚 API 엔드포인트

### 1. 위치 기반 통합 데이터 조회

사용자 위치 주변의 모든 시설 정보를 통합하여 반환합니다.

```http
GET /api/location/nearby?latitude=37.5665&longitude=126.9780&radius=2.0
Authorization: Bearer {token}
```

**파라미터**
- `latitude` (double, 필수): 위도 (-90.0 ~ 90.0)
- `longitude` (double, 필수): 경도 (-180.0 ~ 180.0)
- `radius` (double, 선택): 검색 반경 (km, 기본값: 2.0)

**응답**
```json
{
  "status": "success",
  "data": {
    "location": {
      "latitude": 37.5665,
      "longitude": 126.9780,
      "address": "서울특별시 중구 명동2가",
      "district": {
        "sido": "서울특별시",
        "sigungu": "중구",
        "dong": "명동2가"
      }
    },
    "radius": 2.0,
    "totalCount": 156,
    "categories": {
      "restaurants": 45,
      "libraries": 8,
      "parks": 12,
      "sportsFacilities": 15,
      "coolingCenters": 6,
      "culturalFacilities": 25,
      "bikeStations": 45
    },
    "facilities": [
      {
        "id": "rest_001",
        "type": "RESTAURANT",
        "name": "명동교자",
        "category": "한식",
        "address": "서울특별시 중구 명동10길 29",
        "coordinates": {
          "latitude": 37.5658,
          "longitude": 126.9784
        },
        "distance": 0.12,
        "rating": 4.5,
        "isOpen": true,
        "openingHours": "11:00-22:00",
        "contact": "02-776-5348"
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 개인화된 주변 데이터 조회

사용자 관심사에 따른 맞춤형 주변 시설 정보를 제공합니다.

```http
GET /api/location/nearby/personalized?latitude=37.5665&longitude=126.9780&userId=1
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "userInterests": ["CULTURE", "SPORTS", "ENVIRONMENT"],
    "recommendations": [
      {
        "category": "CULTURE",
        "title": "주변 문화시설",
        "count": 8,
        "facilities": [
          {
            "id": "culture_001",
            "name": "명동예술극장",
            "type": "THEATER",
            "distance": 0.3,
            "rating": 4.8,
            "currentEvent": "뮤지컬 '레미제라블'",
            "ticketAvailable": true
          }
        ]
      },
      {
        "category": "SPORTS",
        "title": "주변 체육시설",
        "count": 5,
        "facilities": [
          {
            "id": "sports_001",
            "name": "중구체육센터",
            "type": "GYM",
            "distance": 0.8,
            "availablePrograms": ["수영", "헬스", "요가"],
            "reservationAvailable": true
          }
        ]
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 카테고리별 시설 조회

특정 카테고리의 시설만 조회합니다.

#### 3-1. 주변 맛집 조회

```http
GET /api/location/restaurants?latitude=37.5665&longitude=126.9780&radius=1.0&category=한식
Authorization: Bearer {token}
```

#### 3-2. 주변 도서관 조회

```http
GET /api/location/libraries?latitude=37.5665&longitude=126.9780&radius=3.0
Authorization: Bearer {token}
```

#### 3-3. 주변 공원 조회

```http
GET /api/location/parks?latitude=37.5665&longitude=126.9780&radius=2.0
Authorization: Bearer {token}
```

#### 3-4. 주변 체육시설 조회

```http
GET /api/location/sports-facilities?latitude=37.5665&longitude=126.9780&radius=2.0
Authorization: Bearer {token}
```

#### 3-5. 주변 무더위쉼터 조회

```http
GET /api/location/cooling-centers?latitude=37.5665&longitude=126.9780&radius=1.5
Authorization: Bearer {token}
```

### 4. 고급 위치 기반 서비스

#### 4-1. 개인화된 위치 추천

```http
POST /api/location/advanced/personalized
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "currentLocation": {
    "latitude": 37.5665,
    "longitude": 126.9780
  },
  "preferences": {
    "categories": ["CULTURE", "FOOD", "SPORTS"],
    "maxDistance": 2.0,
    "priceRange": "MEDIUM",
    "timeOfDay": "EVENING",
    "groupSize": 2
  }
}
```

## 📝 요청/응답 스키마

### LocationDataResponse
```json
{
  "location": {
    "latitude": 37.5665,
    "longitude": 126.9780,
    "address": "string"
  },
  "radius": 2.0,
  "totalCount": 156,
  "facilities": []
}
```

## 🎯 시설 카테고리

### 음식점 (RESTAURANT)
- **한식**: 전통 한국 요리
- **중식**: 중국 요리
- **일식**: 일본 요리
- **양식**: 서양 요리

### 문화시설 (CULTURAL_FACILITY)
- **극장**: 연극, 뮤지컬 공연장
- **영화관**: 영화 상영관
- **박물관**: 전시 및 교육 시설

### 체육시설 (SPORTS_FACILITY)
- **체육관**: 실내 운동시설
- **수영장**: 수영 전용시설

### 공공시설 (PUBLIC_FACILITY)
- **도서관**: 공공 도서관
- **공원**: 도시 공원

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| LOC_001 | 유효하지 않은 좌표 | 위도/경도 범위 초과 |
| LOC_002 | 검색 반경 초과 | 최대 검색 반경 10km 초과 |
| LOC_003 | 시설 정보 없음 | 해당 지역에 시설 정보 없음 |

## 📋 사용 예시

```javascript
// 주변 시설 검색
const searchNearbyFacilities = async (latitude, longitude) => {
  const response = await fetch(
    `/api/location/nearby?latitude=${latitude}&longitude=${longitude}&radius=2.0`,
    {
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    }
  );
  return response.json();
};
```
