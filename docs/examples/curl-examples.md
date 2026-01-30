# cURL 사용 예제

> Seoul Fit API를 cURL로 호출하는 예제 모음

## 🔐 인증

### OAuth 로그인

```bash
# 1. OAuth 인가코드 검증
curl -X POST "http://localhost:8080/api/auth/oauth/authorizecheck" \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "KAKAO",
    "authorizationCode": "your-authorization-code",
    "redirectUri": "http://localhost:3000/auth/callback"
  }'

# 2. OAuth 로그인
curl -X POST "http://localhost:8080/api/auth/oauth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "KAKAO",
    "authorizationCode": "your-authorization-code",
    "redirectUri": "http://localhost:3000/auth/callback"
  }'

# 3. 토큰 갱신
curl -X POST "http://localhost:8080/api/auth/refresh?refreshToken=your-refresh-token"
```

## 👤 사용자 관리

### 사용자 정보 조회

```bash
# 내 정보 조회
curl -X GET "http://localhost:8080/api/users/me?oauthUserId=123&oauthProvider=KAKAO" \
  -H "Authorization: Bearer your-jwt-token"

# 사용자 정보 수정
curl -X PUT "http://localhost:8080/api/users/1" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "새로운닉네임",
    "profileImageUrl": "https://example.com/profile.jpg"
  }'
```

### 관심사 관리

```bash
# 관심사 조회
curl -X POST "http://localhost:8080/api/users/interests" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{"userId": 1}'

# 관심사 설정
curl -X PUT "http://localhost:8080/api/users/interests" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "interests": ["CULTURE", "SPORTS", "ENVIRONMENT"]
  }'
```

## 🔔 알림 관리

### 알림 생성

```bash
# 대기질 알림 생성
curl -X POST "http://localhost:8080/api/notifications" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "notificationType": "AIR_QUALITY",
    "title": "대기질 악화 알림",
    "message": "현재 지역 미세먼지 농도가 높습니다.",
    "triggerCondition": {
      "threshold": 80,
      "operator": "GREATER_THAN"
    },
    "locationInfo": {
      "latitude": 37.5665,
      "longitude": 126.9780,
      "address": "서울특별시 중구 명동",
      "radius": 2000
    }
  }'
```

### 알림 조회 및 관리

```bash
# 알림 히스토리 조회
curl -X GET "http://localhost:8080/api/notifications?userId=1&page=0&size=20" \
  -H "Authorization: Bearer your-jwt-token"

# 알림 읽음 처리
curl -X PATCH "http://localhost:8080/api/notifications/123/read?userId=1" \
  -H "Authorization: Bearer your-jwt-token"

# 읽지 않은 알림 개수
curl -X GET "http://localhost:8080/api/notifications/unread-count?userId=1" \
  -H "Authorization: Bearer your-jwt-token"

# 모든 알림 읽음 처리
curl -X PATCH "http://localhost:8080/api/notifications/read-all?userId=1" \
  -H "Authorization: Bearer your-jwt-token"
```

## ⚡ 트리거 시스템

### 위치 기반 트리거 평가

```bash
curl -X POST "http://localhost:8080/api/triggers/evaluate/location" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user@example.com",
    "latitude": 37.5665,
    "longitude": 126.9780,
    "radius": 2000,
    "triggerTypes": ["AIR_QUALITY", "BIKE_SHARING", "CULTURAL_EVENT"]
  }'
```

### 트리거 히스토리 조회

```bash
curl -X GET "http://localhost:8080/api/triggers/history?page=0&size=20" \
  -H "Authorization: Bearer your-jwt-token"
```

## 🗺️ 지리정보

### 행정구역 조회

```bash
# GET 방식
curl -X GET "http://localhost:8080/api/v1/geocoding/administrative-district?latitude=37.5665&longitude=126.9780"

# POST 방식
curl -X POST "http://localhost:8080/api/v1/geocoding/administrative-district" \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 37.5665,
    "longitude": 126.9780
  }'
```

### 배치 지오코딩

```bash
curl -X POST "http://localhost:8080/api/v1/geocoding/batch" \
  -H "Content-Type: application/json" \
  -d '{
    "locations": [
      {
        "id": "location1",
        "latitude": 37.5665,
        "longitude": 126.9780
      },
      {
        "id": "location2",
        "latitude": 37.5651,
        "longitude": 126.9895
      }
    ]
  }'
```

### 거리 계산

```bash
curl -X POST "http://localhost:8080/api/v1/geocoding/distance" \
  -H "Content-Type: application/json" \
  -d '{
    "origin": {
      "latitude": 37.5665,
      "longitude": 126.9780
    },
    "destination": {
      "latitude": 37.5651,
      "longitude": 126.9895
    },
    "unit": "METER"
  }'
```

## 📍 위치기반 서비스

### 주변 시설 조회

```bash
# 통합 데이터 조회
curl -X GET "http://localhost:8080/api/location/nearby?latitude=37.5665&longitude=126.9780&radius=2.0" \
  -H "Authorization: Bearer your-jwt-token"

# 개인화된 추천
curl -X GET "http://localhost:8080/api/location/nearby/personalized?latitude=37.5665&longitude=126.9780&userId=1" \
  -H "Authorization: Bearer your-jwt-token"
```

### 카테고리별 시설 조회

```bash
# 맛집 조회
curl -X GET "http://localhost:8080/api/location/restaurants?latitude=37.5665&longitude=126.9780&radius=1.0" \
  -H "Authorization: Bearer your-jwt-token"

# 도서관 조회
curl -X GET "http://localhost:8080/api/location/libraries?latitude=37.5665&longitude=126.9780&radius=3.0" \
  -H "Authorization: Bearer your-jwt-token"

# 공원 조회
curl -X GET "http://localhost:8080/api/location/parks?latitude=37.5665&longitude=126.9780&radius=2.0" \
  -H "Authorization: Bearer your-jwt-token"
```

### 고급 위치 서비스

```bash
# 개인화된 추천
curl -X POST "http://localhost:8080/api/location/advanced/personalized" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
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
  }'
```

## 🔍 검색

### 통합 검색

```bash
curl -X GET "http://localhost:8080/api/search?query=명동&latitude=37.5665&longitude=126.9780&radius=2.0&categories=RESTAURANT,CULTURE" \
  -H "Authorization: Bearer your-jwt-token"
```

### 자동완성

```bash
curl -X GET "http://localhost:8080/api/search/autocomplete?query=명동&limit=10" \
  -H "Authorization: Bearer your-jwt-token"
```

### 고급 검색

```bash
curl -X POST "http://localhost:8080/api/search/advanced" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "한식",
    "filters": {
      "location": {
        "latitude": 37.5665,
        "longitude": 126.9780,
        "radius": 2.0
      },
      "categories": ["RESTAURANT"],
      "priceRange": {
        "min": 10000,
        "max": 30000
      },
      "rating": {
        "min": 4.0
      },
      "openNow": true
    },
    "sort": {
      "field": "distance",
      "order": "ASC"
    }
  }'
```

### 배치 검색

```bash
curl -X POST "http://localhost:8080/api/search/batch" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "searches": [
      {
        "id": "search1",
        "query": "명동 한식",
        "categories": ["RESTAURANT"]
      },
      {
        "id": "search2",
        "query": "중구 도서관",
        "categories": ["LIBRARY"]
      }
    ],
    "location": {
      "latitude": 37.5665,
      "longitude": 126.9780,
      "radius": 3.0
    }
  }'
```

## 🌬️ 대기질 정보

### 실시간 대기질 조회

```bash
# 전체 측정소
curl -X GET "http://localhost:8080/api/air-quality/current"

# 특정 지역
curl -X GET "http://localhost:8080/api/air-quality/district/중구"

# 위치 기반
curl -X GET "http://localhost:8080/api/air-quality/nearby?latitude=37.5665&longitude=126.9780"
```

### 대기질 예보

```bash
curl -X GET "http://localhost:8080/api/air-quality/forecast?days=3"
```

## 🎭 문화행사 정보

### 현재 진행중인 행사

```bash
curl -X GET "http://localhost:8080/api/cultural-events/current?page=0&size=20&category=공연"
```

### 위치 기반 문화행사

```bash
curl -X GET "http://localhost:8080/api/cultural-events/nearby?latitude=37.5665&longitude=126.9780&radius=3.0"
```

### 문화행사 검색

```bash
curl -X GET "http://localhost:8080/api/cultural-events/search?query=뮤지컬&category=공연&priceRange=50000-100000"
```

## 🏛️ 공공시설 정보

### 도서관 정보

```bash
curl -X GET "http://localhost:8080/api/public-facilities/libraries?district=중구&page=0&size=20"
```

### 공원 정보

```bash
curl -X GET "http://localhost:8080/api/public-facilities/parks?type=근린공원&latitude=37.5665&longitude=126.9780&radius=3.0"
```

### 체육시설 정보

```bash
curl -X GET "http://localhost:8080/api/public-facilities/sports?sport=수영&district=중구"
```

## 📊 배치 작업

### 검색 배치 처리

```bash
curl -X POST "http://localhost:8080/api/search/batch" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "batchId": "batch_001",
    "operations": [
      {
        "type": "SEARCH",
        "query": "명동 맛집",
        "filters": {"category": "RESTAURANT"}
      },
      {
        "type": "LOCATION_SEARCH",
        "latitude": 37.5665,
        "longitude": 126.9780,
        "radius": 2.0
      }
    ]
  }'
```

## 🔧 유틸리티

### 헬스 체크

```bash
curl -X GET "http://localhost:8080/actuator/health"
```

### API 문서

```bash
curl -X GET "http://localhost:8080/api-docs"
```

### 메트릭스

```bash
curl -X GET "http://localhost:8080/actuator/metrics"
```

## 📝 응답 처리 예제

### jq를 사용한 JSON 파싱

```bash
# 대기질 정보에서 PM2.5 값만 추출
curl -s "http://localhost:8080/api/air-quality/district/중구" | \
  jq '.data.airQuality.pm25.value'

# 주변 맛집 이름 목록 추출
curl -s "http://localhost:8080/api/location/restaurants?latitude=37.5665&longitude=126.9780" \
  -H "Authorization: Bearer your-jwt-token" | \
  jq '.data.restaurants[].name'

# 읽지 않은 알림 개수 확인
UNREAD_COUNT=$(curl -s "http://localhost:8080/api/notifications/unread-count?userId=1" \
  -H "Authorization: Bearer your-jwt-token" | jq '.data')
echo "읽지 않은 알림: $UNREAD_COUNT개"
```

### 에러 처리

```bash
# HTTP 상태 코드와 함께 응답 확인
curl -w "HTTP Status: %{http_code}\n" \
  -X GET "http://localhost:8080/api/users/me?oauthUserId=123&oauthProvider=KAKAO" \
  -H "Authorization: Bearer invalid-token"

# 에러 발생 시 상세 정보 출력
response=$(curl -s -w "HTTPSTATUS:%{http_code}" \
  -X GET "http://localhost:8080/api/notifications?userId=999" \
  -H "Authorization: Bearer your-jwt-token")

http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')

if [ $http_code -ne 200 ]; then
  echo "Error: HTTP $http_code"
  echo $body | jq '.error'
else
  echo $body | jq '.data'
fi
```

---

**업데이트**: 2025-08-21  
**버전**: v1.0.0
