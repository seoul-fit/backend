# 공공시설 API

> 서울시 공공시설 정보 조회 (도서관, 공원, 체육시설 등)

## 📋 개요

서울시의 다양한 공공시설 정보를 통합하여 제공하는 API입니다. 도서관, 공원, 체육시설, 무더위쉼터 등의 정보를 조회할 수 있습니다.

**Base URL**: `/api/public-facilities`

## 📚 API 엔드포인트

### 1. 도서관 정보 조회

서울시 공공도서관 정보를 조회합니다.

```http
GET /api/public-facilities/libraries?district=중구&page=0&size=20
```

**응답**
```json
{
  "status": "success",
  "data": {
    "totalCount": 8,
    "libraries": [
      {
        "id": "lib_001",
        "name": "중구립도서관",
        "type": "PUBLIC_LIBRARY",
        "address": "서울특별시 중구 서소문로 98",
        "coordinates": {
          "latitude": 37.5642,
          "longitude": 126.9751
        },
        "contact": {
          "phone": "02-3396-4300",
          "website": "https://junggulib.seoul.kr",
          "email": "info@junggulib.seoul.kr"
        },
        "operatingHours": {
          "weekday": {
            "open": "09:00",
            "close": "22:00"
          },
          "weekend": {
            "open": "09:00", 
            "close": "17:00"
          },
          "holiday": "휴관"
        },
        "facilities": {
          "readingRoom": {
            "totalSeats": 120,
            "availableSeats": 45,
            "types": ["일반열람실", "노트북실", "그룹스터디실"]
          },
          "specialRooms": ["디지털자료실", "어린이실", "문화강좌실"],
          "amenities": ["주차장", "카페", "수유실", "휠체어접근"]
        },
        "services": {
          "bookLoan": true,
          "digitalResources": true,
          "culturalPrograms": true,
          "studyRoomReservation": true
        },
        "collections": {
          "totalBooks": 85000,
          "digitalBooks": 12000,
          "periodicals": 150,
          "multimedia": 3500
        },
        "currentPrograms": [
          {
            "name": "독서토론회",
            "schedule": "매주 토요일 14:00",
            "target": "성인",
            "capacity": 20,
            "registrationRequired": true
          }
        ]
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 공원 정보 조회

서울시 공원 정보를 조회합니다.

```http
GET /api/public-facilities/parks?type=근린공원&latitude=37.5665&longitude=126.9780&radius=3.0
```

**응답**
```json
{
  "status": "success",
  "data": {
    "totalCount": 12,
    "parks": [
      {
        "id": "park_001",
        "name": "남산공원",
        "type": "도시자연공원",
        "category": "자연공원",
        "address": "서울특별시 중구 회현동1가 산1-3",
        "coordinates": {
          "latitude": 37.5512,
          "longitude": 126.9882
        },
        "area": 1020000,
        "features": {
          "walkingTrails": [
            {
              "name": "남산둘레길",
              "distance": 7.3,
              "difficulty": "쉬움",
              "duration": 120
            }
          ],
          "facilities": [
            "화장실", "음수대", "벤치", "운동기구", 
            "어린이놀이터", "주차장"
          ],
          "attractions": [
            "N서울타워", "남산한옥마을", "남산도서관"
          ]
        },
        "accessibility": {
          "wheelchairAccess": true,
          "publicTransport": [
            "명동역 3번출구 도보 10분",
            "회현역 4번출구 도보 15분"
          ],
          "parking": {
            "available": true,
            "capacity": 200,
            "fee": "시간당 1000원"
          }
        },
        "operatingInfo": {
          "openTime": "24시간",
          "restrictions": "일부 시설은 야간 이용 제한",
          "petPolicy": "반려동물 동반 가능 (목줄 필수)"
        },
        "seasonalInfo": {
          "spring": "벚꽃 명소 (4월 초)",
          "summer": "그늘진 산책로",
          "autumn": "단풍 명소 (10월 말)",
          "winter": "눈꽃 산책"
        }
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 체육시설 정보 조회

서울시 공공 체육시설 정보를 조회합니다.

```http
GET /api/public-facilities/sports?sport=수영&district=중구
```

**응답**
```json
{
  "status": "success",
  "data": {
    "totalCount": 5,
    "facilities": [
      {
        "id": "sports_001",
        "name": "중구체육센터",
        "type": "종합체육시설",
        "address": "서울특별시 중구 을지로 100",
        "coordinates": {
          "latitude": 37.5658,
          "longitude": 126.9895
        },
        "contact": {
          "phone": "02-1234-5678",
          "website": "https://sports.junggu.seoul.kr"
        },
        "facilities": {
          "swimming": {
            "poolType": "실내수영장",
            "lanes": 6,
            "depth": "1.2m-1.8m",
            "temperature": "26-28°C",
            "operatingHours": {
              "weekday": "06:00-22:00",
              "weekend": "09:00-18:00"
            }
          },
          "gym": {
            "area": 500,
            "equipment": ["런닝머신", "웨이트기구", "요가매트"],
            "capacity": 50
          },
          "courts": [
            {
              "type": "배드민턴",
              "count": 4,
              "reservationRequired": true
            }
          ]
        },
        "programs": [
          {
            "name": "성인수영교실",
            "schedule": "월/수/금 19:00-20:00",
            "duration": "3개월",
            "fee": 150000,
            "instructor": "전문강사",
            "capacity": 15,
            "currentEnrollment": 12
          }
        ],
        "fees": {
          "swimming": {
            "adult": 5000,
            "student": 3000,
            "senior": 2500
          },
          "gym": {
            "adult": 3000,
            "student": 2000
          }
        },
        "amenities": ["샤워실", "사물함", "주차장", "카페테리아"]
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 4. 무더위쉼터 정보 조회

여름철 무더위쉼터 정보를 조회합니다.

```http
GET /api/public-facilities/cooling-centers?latitude=37.5665&longitude=126.9780&radius=1.5
```

### 5. 시설 예약 정보 조회

공공시설의 예약 가능 정보를 조회합니다.

```http
GET /api/public-facilities/{facilityId}/reservations?date=2025-01-20&type=수영장
```

### 6. 시설 이용 통계 조회

시설별 이용 현황 및 혼잡도를 조회합니다.

```http
GET /api/public-facilities/{facilityId}/usage-stats?period=week
```

## 📝 시설 카테고리

### 도서관 (Library)
- **공공도서관**: 시립, 구립 도서관
- **전문도서관**: 특화 주제 도서관
- **작은도서관**: 지역 소규모 도서관
- **대학도서관**: 시민 개방 대학도서관

### 공원 (Park)
- **도시자연공원**: 대규모 자연공원
- **근린공원**: 지역 주민 이용 공원
- **어린이공원**: 어린이 전용 공원
- **체육공원**: 체육시설 중심 공원

### 체육시설 (Sports Facility)
- **종합체육시설**: 다목적 체육센터
- **수영장**: 실내/실외 수영장
- **체육관**: 구기종목 체육관
- **운동장**: 육상, 축구장 등

### 문화시설 (Cultural Facility)
- **문화센터**: 지역 문화센터
- **박물관**: 공립 박물관
- **미술관**: 공립 미술관
- **공연장**: 공공 공연시설

## 🕒 운영시간 정보

### 표준 운영시간
- **도서관**: 평일 09:00-22:00, 주말 09:00-17:00
- **체육시설**: 평일 06:00-22:00, 주말 09:00-18:00
- **공원**: 대부분 24시간 (일부 시설 제한)
- **문화시설**: 평일 09:00-18:00, 주말 09:00-17:00

### 휴관일
- **월요일**: 대부분의 도서관, 박물관
- **공휴일**: 시설별 상이
- **정기휴관**: 월 1회 시설점검일

## 💰 이용요금

### 무료 시설
- 공원 (일반 이용)
- 도서관 (도서 열람)
- 일부 문화시설

### 유료 시설
- 체육시설 (종목별 상이)
- 수영장 (성인 3,000-5,000원)
- 문화프로그램 (프로그램별 상이)

### 할인 혜택
- **어린이/청소년**: 50% 할인
- **경로우대**: 65세 이상 50% 할인
- **장애인**: 본인 및 동반 1인 50% 할인
- **다자녀가정**: 30% 할인

## 📊 시설 현황 통계

### 실시간 이용 현황

```http
GET /api/public-facilities/status/realtime?type=도서관&district=중구
```

### 월별 이용 통계

```http
GET /api/public-facilities/statistics/monthly?facilityId=lib_001&year=2024
```

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| FACILITY_001 | 시설 정보 없음 | 해당 지역에 시설 없음 |
| FACILITY_002 | 예약 불가 | 예약 마감 또는 휴관 |
| FACILITY_003 | 운영시간 외 | 시설 운영시간 외 접근 |
| FACILITY_004 | 유효하지 않은 시설 타입 | 존재하지 않는 시설 타입 |
| FACILITY_005 | 데이터 업데이트 지연 | 실시간 정보 수신 지연 |

## 📋 사용 예시

### 1. 주변 도서관 찾기 및 좌석 확인

```javascript
const findNearbyLibraries = async (latitude, longitude) => {
  const response = await fetch(
    `/api/public-facilities/libraries?latitude=${latitude}&longitude=${longitude}&radius=3.0`
  );
  const data = await response.json();
  
  // 이용 가능한 좌석이 있는 도서관 필터링
  const availableLibraries = data.data.libraries.filter(library => 
    library.facilities.readingRoom.availableSeats > 0
  );
  
  return availableLibraries.map(library => ({
    ...library,
    distance: calculateDistance(latitude, longitude, 
      library.coordinates.latitude, library.coordinates.longitude),
    occupancyRate: (library.facilities.readingRoom.totalSeats - 
      library.facilities.readingRoom.availableSeats) / 
      library.facilities.readingRoom.totalSeats * 100
  }));
};

// 도서관 혼잡도 알림
const checkLibraryCapacity = (library) => {
  const occupancyRate = library.occupancyRate;
  
  if (occupancyRate > 90) {
    return {
      status: 'crowded',
      message: `${library.name}이 매우 혼잡합니다. (잔여석: ${library.facilities.readingRoom.availableSeats}석)`
    };
  } else if (occupancyRate > 70) {
    return {
      status: 'busy',
      message: `${library.name}이 다소 혼잡합니다.`
    };
  } else {
    return {
      status: 'available',
      message: `${library.name}에 충분한 좌석이 있습니다.`
    };
  }
};
```

### 2. 체육시설 프로그램 예약

```javascript
const reserveSportsProgram = async (facilityId, programId, userInfo) => {
  // 프로그램 예약 가능 여부 확인
  const availabilityResponse = await fetch(
    `/api/public-facilities/${facilityId}/programs/${programId}/availability`
  );
  const availability = await availabilityResponse.json();
  
  if (!availability.data.available) {
    throw new Error('예약이 마감되었습니다.');
  }
  
  // 예약 진행
  const reservationResponse = await fetch(
    `/api/public-facilities/${facilityId}/programs/${programId}/reserve`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userInfo,
        paymentMethod: 'card'
      })
    }
  );
  
  return reservationResponse.json();
};
```

### 3. 공원 산책로 추천

```javascript
const recommendWalkingTrails = async (userPreferences) => {
  const response = await fetch('/api/public-facilities/parks/trails/recommend', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      location: userPreferences.location,
      difficulty: userPreferences.difficulty, // 'easy', 'medium', 'hard'
      duration: userPreferences.duration, // minutes
      features: userPreferences.features // ['scenic', 'exercise', 'quiet']
    })
  });
  
  const recommendations = await response.json();
  
  return recommendations.data.trails.map(trail => ({
    ...trail,
    estimatedCalories: calculateCalories(trail.distance, trail.difficulty),
    weatherSuitability: checkWeatherSuitability(trail.type),
    crowdLevel: getCurrentCrowdLevel(trail.parkId)
  }));
};
```

---

**업데이트**: 2025-08-21  
**버전**: v1.0.0
