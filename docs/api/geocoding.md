# 지리정보 API

> 위치 좌표와 행정구역 간 변환 서비스

## 📋 개요

위도/경도 좌표를 서울시 행정구역 정보로 변환하거나, 주소를 좌표로 변환하는 지오코딩 서비스를 제공합니다.

**Base URL**: `/api/v1/geocoding`

## 📚 API 엔드포인트

### 1. 위경도로 행정구역 조회 (GET)

위도와 경도를 입력받아 해당하는 행정구역 정보를 반환합니다.

```http
GET /api/v1/geocoding/administrative-district?latitude=37.5665&longitude=126.9780
```

**파라미터**
- `latitude` (double, 필수): 위도 (예: 37.5665)
- `longitude` (double, 필수): 경도 (예: 126.9780)

**응답**
```json
{
  "status": "success",
  "data": {
    "sido": "서울특별시",
    "sigungu": "중구",
    "dong": "명동2가",
    "fullAddress": "서울특별시 중구 명동2가",
    "coordinates": {
      "latitude": 37.5665,
      "longitude": 126.9780
    },
    "administrativeCode": {
      "sido": "11",
      "sigungu": "140",
      "dong": "105"
    },
    "bounds": {
      "northeast": {
        "latitude": 37.5675,
        "longitude": 126.9790
      },
      "southwest": {
        "latitude": 37.5655,
        "longitude": 126.9770
      }
    }
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 위경도로 행정구역 조회 (POST)

요청 본문에 위도와 경도를 포함하여 행정구역 정보를 반환합니다.

```http
POST /api/v1/geocoding/administrative-district
Content-Type: application/json

{
  "latitude": 37.5665,
  "longitude": 126.9780
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "sido": "서울특별시",
    "sigungu": "중구",
    "dong": "명동2가",
    "fullAddress": "서울특별시 중구 명동2가",
    "coordinates": {
      "latitude": 37.5665,
      "longitude": 126.9780
    },
    "administrativeCode": {
      "sido": "11",
      "sigungu": "140",
      "dong": "105"
    }
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 주소로 좌표 조회

주소를 입력받아 해당하는 위도/경도 좌표를 반환합니다.

```http
GET /api/v1/geocoding/coordinates?address=서울특별시 중구 명동2가
```

**파라미터**
- `address` (string, 필수): 주소 (예: "서울특별시 중구 명동2가")

**응답**
```json
{
  "status": "success",
  "data": {
    "address": "서울특별시 중구 명동2가",
    "coordinates": {
      "latitude": 37.5665,
      "longitude": 126.9780
    },
    "accuracy": "EXACT",
    "addressComponents": {
      "sido": "서울특별시",
      "sigungu": "중구",
      "dong": "명동2가",
      "roadName": null,
      "buildingNumber": null
    }
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 4. 배치 지오코딩

여러 위치를 한 번에 처리합니다.

```http
POST /api/v1/geocoding/batch
Content-Type: application/json

{
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
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "processedCount": 2,
    "results": [
      {
        "id": "location1",
        "success": true,
        "district": {
          "sido": "서울특별시",
          "sigungu": "중구",
          "dong": "명동2가",
          "fullAddress": "서울특별시 중구 명동2가"
        }
      },
      {
        "id": "location2",
        "success": true,
        "district": {
          "sido": "서울특별시",
          "sigungu": "중구",
          "dong": "소공동",
          "fullAddress": "서울특별시 중구 소공동"
        }
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 5. 거리 계산

두 지점 간의 거리를 계산합니다.

```http
POST /api/v1/geocoding/distance
Content-Type: application/json

{
  "origin": {
    "latitude": 37.5665,
    "longitude": 126.9780
  },
  "destination": {
    "latitude": 37.5651,
    "longitude": 126.9895
  },
  "unit": "METER"
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "distance": 1247.5,
    "unit": "METER",
    "origin": {
      "latitude": 37.5665,
      "longitude": 126.9780,
      "address": "서울특별시 중구 명동2가"
    },
    "destination": {
      "latitude": 37.5651,
      "longitude": 126.9895,
      "address": "서울특별시 중구 소공동"
    },
    "straightLineDistance": 1247.5,
    "estimatedWalkingTime": 15,
    "estimatedDrivingTime": 3
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 6. 주변 행정구역 조회

특정 지점 주변의 행정구역들을 조회합니다.

```http
GET /api/v1/geocoding/nearby-districts?latitude=37.5665&longitude=126.9780&radius=2000
```

**파라미터**
- `latitude` (double, 필수): 중심점 위도
- `longitude` (double, 필수): 중심점 경도
- `radius` (int, 선택): 반경 (미터, 기본값: 1000)

**응답**
```json
{
  "status": "success",
  "data": {
    "center": {
      "latitude": 37.5665,
      "longitude": 126.9780
    },
    "radius": 2000,
    "districts": [
      {
        "sido": "서울특별시",
        "sigungu": "중구",
        "dong": "명동2가",
        "distance": 0,
        "direction": "CENTER"
      },
      {
        "sido": "서울특별시",
        "sigungu": "중구",
        "dong": "소공동",
        "distance": 1247,
        "direction": "EAST"
      },
      {
        "sido": "서울특별시",
        "sigungu": "중구",
        "dong": "회현동1가",
        "distance": 856,
        "direction": "SOUTH"
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

## 📝 요청/응답 스키마

### CoordinateRequest
```json
{
  "latitude": 37.5665,    // 필수: 위도 (-90 ~ 90)
  "longitude": 126.9780   // 필수: 경도 (-180 ~ 180)
}
```

### AdministrativeDistrictResponse
```json
{
  "sido": "서울특별시",           // 시도명
  "sigungu": "중구",             // 시군구명
  "dong": "명동2가",             // 동명
  "fullAddress": "string",       // 전체 주소
  "coordinates": {               // 좌표 정보
    "latitude": 37.5665,
    "longitude": 126.9780
  },
  "administrativeCode": {        // 행정구역 코드
    "sido": "11",
    "sigungu": "140",
    "dong": "105"
  },
  "bounds": {                    // 경계 정보
    "northeast": {
      "latitude": 37.5675,
      "longitude": 126.9790
    },
    "southwest": {
      "latitude": 37.5655,
      "longitude": 126.9770
    }
  }
}
```

### BatchGeocodingRequest
```json
{
  "locations": [                 // 필수: 위치 목록 (최대 100개)
    {
      "id": "string",           // 필수: 위치 식별자
      "latitude": 37.5665,      // 필수: 위도
      "longitude": 126.9780     // 필수: 경도
    }
  ]
}
```

### DistanceRequest
```json
{
  "origin": {                   // 필수: 출발지
    "latitude": 37.5665,
    "longitude": 126.9780
  },
  "destination": {              // 필수: 목적지
    "latitude": 37.5651,
    "longitude": 126.9895
  },
  "unit": "METER"              // 선택: 거리 단위 (METER, KILOMETER)
}
```

## 🗺️ 지원 지역

### 서울시 행정구역
- **25개 자치구**: 종로구, 중구, 용산구, 성동구, 광진구, 동대문구, 중랑구, 성북구, 강북구, 도봉구, 노원구, 은평구, 서대문구, 마포구, 양천구, 강서구, 구로구, 금천구, 영등포구, 동작구, 관악구, 서초구, 강남구, 송파구, 강동구
- **424개 행정동**: 각 자치구별 상세 행정동 정보

### 좌표계
- **입력/출력**: WGS84 (EPSG:4326)
- **내부 처리**: GRS80 / UTM-K (EPSG:5178)

## 🎯 정확도 수준

### 주소 매칭 정확도
- `EXACT`: 정확한 주소 매칭 (건물 단위)
- `INTERPOLATED`: 보간된 위치 (도로명 기준)
- `GEOMETRIC_CENTER`: 행정구역 중심점
- `APPROXIMATE`: 근사 위치

### 거리 계산 방식
- **직선거리**: Haversine 공식 사용
- **도보 예상시간**: 평균 보행속도 4km/h 기준
- **차량 예상시간**: 도심 평균속도 25km/h 기준

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| GEO_001 | 유효하지 않은 좌표 | 위도/경도 범위 초과 |
| GEO_002 | 행정구역을 찾을 수 없음 | 서울시 외 지역 또는 해상 |
| GEO_003 | 주소를 찾을 수 없음 | 존재하지 않는 주소 |
| GEO_004 | 배치 처리 한도 초과 | 한 번에 100개 초과 요청 |
| GEO_005 | 지오코딩 서비스 오류 | 외부 지오코딩 API 오류 |
| GEO_006 | 좌표계 변환 실패 | 좌표계 변환 중 오류 |

## 📋 사용 예시

### 1. 현재 위치의 행정구역 조회

```javascript
const getCurrentDistrict = async (latitude, longitude) => {
  const response = await fetch(
    `/api/v1/geocoding/administrative-district?latitude=${latitude}&longitude=${longitude}`
  );
  
  if (response.ok) {
    const data = await response.json();
    return data.data;
  } else if (response.status === 404) {
    throw new Error('해당 위치의 행정구역을 찾을 수 없습니다.');
  }
  throw new Error('지오코딩 서비스 오류');
};

// 사용 예시
navigator.geolocation.getCurrentPosition(async (position) => {
  try {
    const district = await getCurrentDistrict(
      position.coords.latitude,
      position.coords.longitude
    );
    console.log(`현재 위치: ${district.fullAddress}`);
  } catch (error) {
    console.error('위치 조회 실패:', error.message);
  }
});
```

### 2. 주소 검색 및 좌표 변환

```javascript
const searchAddress = async (address) => {
  const response = await fetch(
    `/api/v1/geocoding/coordinates?address=${encodeURIComponent(address)}`
  );
  
  const data = await response.json();
  return data.data;
};

// 자동완성 기능과 함께 사용
const AddressSearch = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);

  const handleSearch = async (searchQuery) => {
    if (searchQuery.length < 2) return;
    
    try {
      const result = await searchAddress(searchQuery);
      setResults([result]);
    } catch (error) {
      setResults([]);
    }
  };

  return (
    <div>
      <input
        value={query}
        onChange={(e) => {
          setQuery(e.target.value);
          handleSearch(e.target.value);
        }}
        placeholder="주소를 입력하세요"
      />
      {results.map((result, index) => (
        <div key={index} onClick={() => selectAddress(result)}>
          {result.address} ({result.coordinates.latitude}, {result.coordinates.longitude})
        </div>
      ))}
    </div>
  );
};
```

### 3. 배치 지오코딩

```javascript
const batchGeocode = async (locations) => {
  const response = await fetch('/api/v1/geocoding/batch', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ locations })
  });
  
  const data = await response.json();
  return data.data.results;
};

// 여러 매장 위치 처리 예시
const processStoreLocations = async (stores) => {
  const locations = stores.map(store => ({
    id: store.id,
    latitude: store.latitude,
    longitude: store.longitude
  }));
  
  const results = await batchGeocode(locations);
  
  return stores.map(store => {
    const result = results.find(r => r.id === store.id);
    return {
      ...store,
      district: result?.success ? result.district : null
    };
  });
};
```

### 4. 거리 계산 및 주변 검색

```javascript
const calculateDistance = async (origin, destination) => {
  const response = await fetch('/api/v1/geocoding/distance', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      origin,
      destination,
      unit: 'METER'
    })
  });
  
  const data = await response.json();
  return data.data;
};

// 가까운 시설 찾기
const findNearbyFacilities = async (userLocation, facilities) => {
  const distances = await Promise.all(
    facilities.map(async (facility) => {
      const distance = await calculateDistance(userLocation, {
        latitude: facility.latitude,
        longitude: facility.longitude
      });
      
      return {
        ...facility,
        distance: distance.distance,
        walkingTime: distance.estimatedWalkingTime
      };
    })
  );
  
  // 거리순 정렬
  return distances.sort((a, b) => a.distance - b.distance);
};
```

### 5. 실시간 위치 추적 및 행정구역 변경 감지

```javascript
const useLocationTracking = () => {
  const [currentDistrict, setCurrentDistrict] = useState(null);
  const [locationHistory, setLocationHistory] = useState([]);

  useEffect(() => {
    let watchId;

    const startTracking = () => {
      watchId = navigator.geolocation.watchPosition(
        async (position) => {
          const { latitude, longitude } = position.coords;
          
          try {
            const district = await getCurrentDistrict(latitude, longitude);
            
            // 행정구역이 변경된 경우
            if (!currentDistrict || 
                currentDistrict.fullAddress !== district.fullAddress) {
              setCurrentDistrict(district);
              
              // 위치 히스토리 업데이트
              setLocationHistory(prev => [...prev, {
                district,
                timestamp: new Date(),
                coordinates: { latitude, longitude }
              }]);
              
              // 행정구역 변경 이벤트 발생
              onDistrictChange(district);
            }
          } catch (error) {
            console.error('위치 추적 오류:', error);
          }
        },
        (error) => {
          console.error('GPS 오류:', error);
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 60000
        }
      );
    };

    startTracking();

    return () => {
      if (watchId) {
        navigator.geolocation.clearWatch(watchId);
      }
    };
  }, [currentDistrict]);

  const onDistrictChange = (newDistrict) => {
    console.log(`행정구역 변경: ${newDistrict.fullAddress}`);
    // 트리거 평가 또는 알림 발송
  };

  return {
    currentDistrict,
    locationHistory
  };
};
```

---

**업데이트**: 2025-01-XX  
**버전**: v1.0.0
