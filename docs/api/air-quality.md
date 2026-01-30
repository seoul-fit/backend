# 대기질 API

> 서울시 실시간 대기환경 정보 조회

## 📋 개요

서울시 공공데이터 포탈의 실시간 대기환경 현황 API를 활용하여 미세먼지, 초미세먼지, 오존 등의 대기질 정보를 제공합니다.

**Base URL**: `/api/air-quality`

## 📚 API 엔드포인트

### 1. 실시간 대기질 조회

서울시 전체 측정소의 실시간 대기질 정보를 조회합니다.

```http
GET /api/air-quality/current
```

**응답**
```json
{
  "status": "success",
  "data": {
    "updateTime": "2025-01-XX 10:00:00",
    "totalStations": 25,
    "stations": [
      {
        "stationName": "중구",
        "district": "중구",
        "pm10": {
          "value": 45,
          "grade": "보통",
          "unit": "㎍/㎥"
        },
        "pm25": {
          "value": 28,
          "grade": "좋음",
          "unit": "㎍/㎥"
        },
        "o3": {
          "value": 0.025,
          "grade": "좋음",
          "unit": "ppm"
        },
        "no2": {
          "value": 0.018,
          "grade": "좋음",
          "unit": "ppm"
        },
        "co": {
          "value": 0.4,
          "grade": "좋음",
          "unit": "ppm"
        },
        "so2": {
          "value": 0.003,
          "grade": "좋음",
          "unit": "ppm"
        },
        "overallGrade": "보통",
        "coordinates": {
          "latitude": 37.5665,
          "longitude": 126.9780
        }
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 특정 지역 대기질 조회

특정 자치구의 대기질 정보를 조회합니다.

```http
GET /api/air-quality/district/{district}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "district": "중구",
    "stationName": "중구",
    "updateTime": "2025-01-XX 10:00:00",
    "airQuality": {
      "pm10": {
        "value": 45,
        "grade": "보통",
        "unit": "㎍/㎥",
        "threshold": {
          "good": 30,
          "normal": 80,
          "bad": 150
        }
      },
      "pm25": {
        "value": 28,
        "grade": "좋음",
        "unit": "㎍/㎥",
        "threshold": {
          "good": 15,
          "normal": 35,
          "bad": 75
        }
      },
      "overallGrade": "보통",
      "healthAdvice": {
        "general": "실외활동 시 마스크 착용을 권장합니다.",
        "sensitive": "민감군은 장시간 실외활동을 자제하세요.",
        "children": "어린이는 실외활동을 줄이세요."
      }
    }
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 위치 기반 대기질 조회

사용자 위치 기준 가장 가까운 측정소의 대기질 정보를 조회합니다.

```http
GET /api/air-quality/nearby?latitude=37.5665&longitude=126.9780
```

### 4. 대기질 예보 조회

서울시 대기질 예보 정보를 조회합니다.

```http
GET /api/air-quality/forecast?days=3
```

**응답**
```json
{
  "status": "success",
  "data": {
    "issueTime": "2025-01-XX 06:00:00",
    "forecasts": [
      {
        "date": "2025-01-XX",
        "pm10": {
          "grade": "보통",
          "range": "31-80㎍/㎥",
          "description": "대기 정체로 인해 미세먼지 농도가 다소 높을 것으로 예상됩니다."
        },
        "pm25": {
          "grade": "좋음",
          "range": "16-35㎍/㎥",
          "description": "초미세먼지 농도는 양호한 수준을 유지할 것으로 예상됩니다."
        },
        "o3": {
          "grade": "보통",
          "description": "오존 농도는 보통 수준을 유지할 것으로 예상됩니다."
        }
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 5. 대기질 히스토리 조회

특정 기간의 대기질 변화 추이를 조회합니다.

```http
GET /api/air-quality/history?district=중구&startDate=2025-01-01&endDate=2025-01-07
```

## 📝 대기질 등급 기준

### 미세먼지 (PM10)
| 등급 | 농도 (㎍/㎥) | 색상 | 설명 |
|------|-------------|------|------|
| 좋음 | 0-30 | 파란색 | 대기오염 관련 질환자군에서도 영향이 거의 없는 수준 |
| 보통 | 31-80 | 초록색 | 민감군은 장시간 또는 무리한 실외활동 제한 |
| 나쁨 | 81-150 | 주황색 | 일반인도 장시간 실외활동 시 간헐적 증상 가능 |
| 매우나쁨 | 151+ | 빨간색 | 일반인도 실외활동 제한 및 실내 머물기 권고 |

### 초미세먼지 (PM2.5)
| 등급 | 농도 (㎍/㎥) | 색상 | 설명 |
|------|-------------|------|------|
| 좋음 | 0-15 | 파란색 | 대기오염 관련 질환자군에서도 영향이 거의 없는 수준 |
| 보통 | 16-35 | 초록색 | 민감군은 장시간 또는 무리한 실외활동 제한 |
| 나쁨 | 36-75 | 주황색 | 일반인도 장시간 실외활동 시 간헐적 증상 가능 |
| 매우나쁨 | 76+ | 빨간색 | 일반인도 실외활동 제한 및 실내 머물기 권고 |

## 🏥 건강 가이드

### 민감군 대상
- 어린이, 노인, 천식 등 호흡기 질환자
- 심혈관 질환자
- 임산부

### 등급별 행동 요령

#### 좋음 (파란색)
- 실외활동 및 환기 가능

#### 보통 (초록색)
- 민감군: 장시간 실외활동 시 마스크 착용
- 일반인: 정상적인 실외활동 가능

#### 나쁨 (주황색)
- 민감군: 실외활동 제한, 실내 머물기
- 일반인: 장시간 실외활동 시 마스크 착용

#### 매우나쁨 (빨간색)
- 모든 사람: 실외활동 금지, 실내 머물기
- 외출 시 보건용 마스크 필수 착용

## 📊 대기질 통계

### 월별 평균 조회

```http
GET /api/air-quality/statistics/monthly?year=2024&district=중구
```

### 연간 추이 조회

```http
GET /api/air-quality/statistics/yearly?district=중구&years=3
```

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| AIR_001 | 측정소 정보 없음 | 해당 지역 측정소 없음 |
| AIR_002 | 데이터 업데이트 지연 | 실시간 데이터 수신 지연 |
| AIR_003 | 서울시 API 오류 | 외부 API 호출 실패 |
| AIR_004 | 유효하지 않은 지역 | 존재하지 않는 자치구명 |

## 📋 사용 예시

```javascript
// 현재 위치 대기질 조회
const getCurrentAirQuality = async (latitude, longitude) => {
  const response = await fetch(
    `/api/air-quality/nearby?latitude=${latitude}&longitude=${longitude}`
  );
  const data = await response.json();
  
  const airQuality = data.data.airQuality;
  const pm25Grade = airQuality.pm25.grade;
  
  // 대기질에 따른 알림
  if (pm25Grade === '나쁨' || pm25Grade === '매우나쁨') {
    showAirQualityAlert(airQuality);
  }
  
  return data;
};

// 대기질 알림 표시
const showAirQualityAlert = (airQuality) => {
  const message = `현재 미세먼지 농도: ${airQuality.pm25.value}㎍/㎥ (${airQuality.pm25.grade})
${airQuality.healthAdvice.general}`;
  
  showNotification({
    title: '대기질 주의보',
    message,
    type: 'warning',
    priority: 'high'
  });
};
```

---

**업데이트**: 2025-08-21  
**버전**: v1.0.0
