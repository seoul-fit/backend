# 검색 API

> 통합 검색 및 배치 검색 서비스

## 📋 개요

UrbanPing의 모든 데이터를 대상으로 하는 통합 검색 기능과 대량 데이터 처리를 위한 배치 검색 기능을 제공합니다.

**Base URL**: `/api/search`

## 🔐 인증 요구사항

모든 검색 API는 JWT 토큰 인증이 필요합니다.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📚 API 엔드포인트

### 1. 통합 검색

모든 카테고리를 대상으로 통합 검색을 수행합니다.

```http
GET /api/search?query=명동&latitude=37.5665&longitude=126.9780&radius=2.0&categories=RESTAURANT,CULTURE
Authorization: Bearer {token}
```

**파라미터**
- `query` (string, 필수): 검색어
- `latitude` (double, 선택): 위도 (위치 기반 검색 시)
- `longitude` (double, 선택): 경도 (위치 기반 검색 시)
- `radius` (double, 선택): 검색 반경 (km, 기본값: 5.0)
- `categories` (string, 선택): 검색 카테고리 (쉼표로 구분)
- `page` (int, 선택): 페이지 번호 (기본값: 0)
- `size` (int, 선택): 페이지 크기 (기본값: 20)

**응답**
```json
{
  "status": "success",
  "data": {
    "query": "명동",
    "totalResults": 156,
    "searchTime": 0.045,
    "categories": {
      "RESTAURANT": 45,
      "CULTURE": 25,
      "SHOPPING": 30,
      "ACCOMMODATION": 15,
      "TRANSPORTATION": 41
    },
    "results": [
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
        "relevanceScore": 0.95,
        "highlights": ["<mark>명동</mark>교자", "전통 <mark>명동</mark> 맛집"],
        "rating": 4.5,
        "isOpen": true
      }
    ],
    "suggestions": ["명동 맛집", "명동 쇼핑", "명동 호텔"],
    "relatedSearches": ["을지로", "종로", "중구"]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 카테고리별 검색

특정 카테고리에서만 검색을 수행합니다.

```http
GET /api/search/restaurants?query=한식&latitude=37.5665&longitude=126.9780
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "category": "RESTAURANT",
    "query": "한식",
    "totalResults": 25,
    "results": [
      {
        "id": "rest_001",
        "name": "명동교자",
        "category": "한식",
        "subcategory": "만두전문점",
        "address": "서울특별시 중구 명동10길 29",
        "distance": 0.12,
        "rating": 4.5,
        "priceRange": "10000-20000",
        "specialties": ["왕만두", "물만두"],
        "isOpen": true
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 자동완성 검색

검색어 입력 시 자동완성 제안을 제공합니다.

```http
GET /api/search/autocomplete?query=명동&limit=10
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "query": "명동",
    "suggestions": [
      {
        "text": "명동교자",
        "type": "RESTAURANT",
        "category": "한식",
        "popularity": 0.95
      },
      {
        "text": "명동성당",
        "type": "LANDMARK",
        "category": "종교시설",
        "popularity": 0.88
      },
      {
        "text": "명동 쇼핑",
        "type": "KEYWORD",
        "category": "쇼핑",
        "popularity": 0.82
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 4. 고급 검색

복합 조건을 사용한 고급 검색을 수행합니다.

```http
POST /api/search/advanced
Authorization: Bearer {token}
Content-Type: application/json

{
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
    "openNow": true,
    "features": ["주차가능", "배달가능"]
  },
  "sort": {
    "field": "distance",
    "order": "ASC"
  },
  "page": 0,
  "size": 20
}
```

### 5. 배치 검색

여러 검색어를 한 번에 처리합니다.

```http
POST /api/search/batch
Authorization: Bearer {token}
Content-Type: application/json

{
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
}
```

### 6. 인기 검색어

현재 인기 있는 검색어 목록을 제공합니다.

```http
GET /api/search/trending?period=24h&limit=20
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "period": "24h",
    "trending": [
      {
        "rank": 1,
        "keyword": "명동 맛집",
        "searchCount": 1250,
        "changeRate": 15.2,
        "category": "RESTAURANT"
      },
      {
        "rank": 2,
        "keyword": "한강공원",
        "searchCount": 980,
        "changeRate": -5.1,
        "category": "PARK"
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

## 📝 요청/응답 스키마

### SearchRequest
```json
{
  "query": "string",              // 필수: 검색어
  "location": {                   // 선택: 위치 정보
    "latitude": 37.5665,
    "longitude": 126.9780,
    "radius": 2.0
  },
  "categories": ["RESTAURANT"],   // 선택: 검색 카테고리
  "filters": {},                  // 선택: 추가 필터
  "sort": {                       // 선택: 정렬 조건
    "field": "distance",
    "order": "ASC"
  },
  "page": 0,                      // 선택: 페이지 번호
  "size": 20                      // 선택: 페이지 크기
}
```

### SearchResult
```json
{
  "id": "string",                 // 결과 ID
  "type": "RESTAURANT",           // 결과 타입
  "name": "string",               // 이름
  "category": "string",           // 카테고리
  "address": "string",            // 주소
  "coordinates": {                // 좌표
    "latitude": 37.5665,
    "longitude": 126.9780
  },
  "distance": 0.12,              // 거리 (km)
  "relevanceScore": 0.95,        // 관련도 점수
  "highlights": [],              // 하이라이트된 텍스트
  "rating": 4.5,                 // 평점
  "metadata": {}                 // 추가 메타데이터
}
```

## 🔍 검색 카테고리

| 카테고리 | 설명 | 검색 필드 |
|----------|------|-----------|
| `RESTAURANT` | 음식점 | 이름, 카테고리, 메뉴, 주소 |
| `CULTURE` | 문화시설 | 이름, 장르, 프로그램, 주소 |
| `LIBRARY` | 도서관 | 이름, 시설, 프로그램, 주소 |
| `PARK` | 공원 | 이름, 시설, 특징, 주소 |
| `SPORTS` | 체육시설 | 이름, 종목, 프로그램, 주소 |
| `SHOPPING` | 쇼핑 | 이름, 브랜드, 카테고리, 주소 |
| `ACCOMMODATION` | 숙박 | 이름, 타입, 시설, 주소 |
| `TRANSPORTATION` | 교통 | 역명, 노선, 시설, 주소 |

## 🎯 검색 필터

### 위치 필터
- **반경**: 0.5km ~ 10km
- **행정구역**: 시도, 시군구, 동 단위
- **지하철역**: 역 기준 반경 검색

### 운영 상태 필터
- **현재 운영중**: 현재 시간 기준
- **24시간 운영**: 24시간 운영 시설
- **주말 운영**: 주말 운영 시설

### 평점 및 가격 필터
- **평점**: 1.0 ~ 5.0
- **가격대**: 저가, 중가, 고가
- **리뷰 수**: 최소 리뷰 수

### 시설 및 서비스 필터
- **주차 가능**: 주차장 보유 시설
- **배달 가능**: 배달 서비스 제공
- **예약 가능**: 온라인 예약 지원
- **장애인 접근**: 장애인 편의시설

## 📊 검색 분석

### 검색 통계 조회

```http
GET /api/search/analytics?period=7d&userId=1
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "period": "7d",
    "totalSearches": 45,
    "uniqueQueries": 32,
    "topCategories": [
      {
        "category": "RESTAURANT",
        "count": 18,
        "percentage": 40.0
      }
    ],
    "searchHistory": [
      {
        "query": "명동 한식",
        "timestamp": "2025-01-XX 10:00:00",
        "resultCount": 25
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| SEARCH_001 | 검색어가 너무 짧음 | 최소 2자 이상 입력 필요 |
| SEARCH_002 | 검색 결과 없음 | 검색 조건에 맞는 결과 없음 |
| SEARCH_003 | 검색 서비스 오류 | 검색 엔진 내부 오류 |
| SEARCH_004 | 필터 조건 오류 | 잘못된 필터 조건 |
| SEARCH_005 | 배치 검색 한도 초과 | 한 번에 최대 10개 검색 |
| SEARCH_006 | 검색 빈도 제한 | 분당 검색 횟수 초과 |

## 📋 사용 예시

### 1. 실시간 검색 구현

```javascript
const useRealTimeSearch = () => {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(false);

  // 디바운스된 검색
  const debouncedSearch = useCallback(
    debounce(async (searchQuery) => {
      if (searchQuery.length < 2) return;
      
      setLoading(true);
      try {
        const response = await fetch(
          `/api/search?query=${encodeURIComponent(searchQuery)}`,
          {
            headers: {
              'Authorization': `Bearer ${accessToken}`
            }
          }
        );
        const data = await response.json();
        setResults(data.data.results);
      } catch (error) {
        console.error('검색 오류:', error);
      } finally {
        setLoading(false);
      }
    }, 300),
    []
  );

  // 자동완성
  const fetchSuggestions = useCallback(
    debounce(async (searchQuery) => {
      if (searchQuery.length < 1) return;
      
      const response = await fetch(
        `/api/search/autocomplete?query=${encodeURIComponent(searchQuery)}`,
        {
          headers: {
            'Authorization': `Bearer ${accessToken}`
          }
        }
      );
      const data = await response.json();
      setSuggestions(data.data.suggestions);
    }, 200),
    []
  );

  useEffect(() => {
    debouncedSearch(query);
    fetchSuggestions(query);
  }, [query, debouncedSearch, fetchSuggestions]);

  return {
    query,
    setQuery,
    results,
    suggestions,
    loading
  };
};
```

### 2. 고급 검색 필터

```javascript
const AdvancedSearch = () => {
  const [filters, setFilters] = useState({
    categories: [],
    priceRange: { min: 0, max: 100000 },
    rating: { min: 0 },
    openNow: false,
    features: []
  });

  const performAdvancedSearch = async (query) => {
    const response = await fetch('/api/search/advanced', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        query,
        filters,
        sort: { field: 'relevance', order: 'DESC' }
      })
    });
    
    return response.json();
  };

  return (
    <div className="advanced-search">
      <SearchFilters filters={filters} onChange={setFilters} />
      <SearchResults onSearch={performAdvancedSearch} />
    </div>
  );
};
```

### 3. 검색 결과 무한 스크롤

```javascript
const useInfiniteSearch = (query) => {
  const [results, setResults] = useState([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);

  const loadMore = useCallback(async () => {
    if (loading || !hasMore) return;
    
    setLoading(true);
    try {
      const response = await fetch(
        `/api/search?query=${encodeURIComponent(query)}&page=${page}&size=20`,
        {
          headers: {
            'Authorization': `Bearer ${accessToken}`
          }
        }
      );
      const data = await response.json();
      
      if (data.data.results.length === 0) {
        setHasMore(false);
      } else {
        setResults(prev => [...prev, ...data.data.results]);
        setPage(prev => prev + 1);
      }
    } catch (error) {
      console.error('검색 로드 오류:', error);
    } finally {
      setLoading(false);
    }
  }, [query, page, loading, hasMore]);

  // 쿼리 변경 시 초기화
  useEffect(() => {
    setResults([]);
    setPage(0);
    setHasMore(true);
    loadMore();
  }, [query]);

  return { results, loadMore, hasMore, loading };
};
```

---

**업데이트**: 2025-01-XX  
**버전**: v1.0.0
