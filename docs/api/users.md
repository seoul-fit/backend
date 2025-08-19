# 사용자 관리 API

> 사용자 프로필 및 관심사 관리

## 📋 개요

사용자 정보 조회, 수정 및 관심사 설정을 관리하는 API입니다.

**Base URL**: `/api/users`

## 🔐 인증 요구사항

모든 사용자 관리 API는 JWT 토큰 인증이 필요합니다.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📚 API 엔드포인트

### 1. 사용자 정보 조회

특정 사용자의 정보를 조회합니다.

```http
GET /api/users/{userId}
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "nickname": "홍길동",
    "email": "user@example.com",
    "profileImageUrl": "https://example.com/profile.jpg",
    "provider": "KAKAO",
    "createdAt": "2025-01-XX 10:00:00",
    "updatedAt": "2025-01-XX 10:00:00"
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 내 정보 조회

OAuth 인증 정보로 현재 사용자 정보를 조회합니다.

```http
GET /api/users/me?oauthUserId=123456789&oauthProvider=KAKAO
Authorization: Bearer {token}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "nickname": "홍길동",
    "email": "user@example.com",
    "profileImageUrl": "https://example.com/profile.jpg",
    "provider": "KAKAO",
    "interests": ["CULTURE", "SPORTS", "ENVIRONMENT"],
    "createdAt": "2025-01-XX 10:00:00",
    "updatedAt": "2025-01-XX 10:00:00"
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 사용자 정보 수정

사용자 프로필 정보를 수정합니다.

```http
PUT /api/users/{userId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "nickname": "새로운닉네임",
  "profileImageUrl": "https://example.com/new-profile.jpg"
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "nickname": "새로운닉네임",
    "email": "user@example.com",
    "profileImageUrl": "https://example.com/new-profile.jpg",
    "provider": "KAKAO",
    "updatedAt": "2025-01-XX 10:00:00"
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

## 🎯 관심사 관리 API

**Base URL**: `/api/users/interests`

### 1. 사용자 관심사 조회

현재 로그인한 사용자의 관심사 설정을 조회합니다.

```http
POST /api/users/interests
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "userId": 1,
    "interests": [
      {
        "category": "CULTURE",
        "name": "문화행사",
        "description": "공연, 전시, 축제 등 문화행사 정보",
        "enabled": true
      },
      {
        "category": "SPORTS",
        "name": "체육시설",
        "description": "체육시설 및 운동 프로그램 정보",
        "enabled": true
      },
      {
        "category": "ENVIRONMENT",
        "name": "환경정보",
        "description": "대기질, 날씨 등 환경 정보",
        "enabled": false
      },
      {
        "category": "TRANSPORTATION",
        "name": "교통정보",
        "description": "따릉이, 지하철 등 교통 정보",
        "enabled": false
      },
      {
        "category": "FACILITIES",
        "name": "공공시설",
        "description": "도서관, 공원 등 공공시설 정보",
        "enabled": false
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. 사용자 관심사 설정

사용자의 관심사를 업데이트합니다.

```http
PUT /api/users/interests
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "interests": ["CULTURE", "ENVIRONMENT", "TRANSPORTATION"]
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "userId": 1,
    "updatedInterests": ["CULTURE", "ENVIRONMENT", "TRANSPORTATION"],
    "message": "관심사가 성공적으로 업데이트되었습니다."
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. 관심사 카테고리 목록

사용 가능한 모든 관심사 카테고리를 조회합니다.

```http
GET /api/users/interests/categories
```

**응답**
```json
{
  "status": "success",
  "data": {
    "categories": [
      {
        "code": "CULTURE",
        "name": "문화행사",
        "description": "공연, 전시, 축제 등 문화행사 정보",
        "icon": "🎭",
        "color": "#FF6B6B"
      },
      {
        "code": "SPORTS",
        "name": "체육시설",
        "description": "체육시설 및 운동 프로그램 정보",
        "icon": "⚽",
        "color": "#4ECDC4"
      },
      {
        "code": "ENVIRONMENT",
        "name": "환경정보",
        "description": "대기질, 날씨 등 환경 정보",
        "icon": "🌱",
        "color": "#45B7D1"
      },
      {
        "code": "TRANSPORTATION",
        "name": "교통정보",
        "description": "따릉이, 지하철 등 교통 정보",
        "icon": "🚲",
        "color": "#96CEB4"
      },
      {
        "code": "FACILITIES",
        "name": "공공시설",
        "description": "도서관, 공원 등 공공시설 정보",
        "icon": "🏛️",
        "color": "#FFEAA7"
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

## 📝 요청/응답 스키마

### UpdateUserRequest
```json
{
  "nickname": "string",           // 선택: 닉네임 (2-20자)
  "profileImageUrl": "string"     // 선택: 프로필 이미지 URL
}
```

### UserResult
```json
{
  "id": 1,                       // 사용자 ID
  "nickname": "string",          // 닉네임
  "email": "string",             // 이메일
  "profileImageUrl": "string",   // 프로필 이미지 URL
  "provider": "KAKAO",           // OAuth 제공자
  "interests": ["CULTURE"],      // 관심사 목록
  "createdAt": "2025-01-XX",     // 생성일시
  "updatedAt": "2025-01-XX"      // 수정일시
}
```

### UserInterestRequest
```json
{
  "userId": 1,                                    // 필수: 사용자 ID
  "interests": ["CULTURE", "SPORTS", "ENVIRONMENT"] // 필수: 관심사 목록
}
```

### InterestCategory
```json
{
  "code": "CULTURE",                    // 카테고리 코드
  "name": "문화행사",                   // 카테고리 이름
  "description": "공연, 전시, 축제...", // 설명
  "icon": "🎭",                        // 아이콘
  "color": "#FF6B6B",                  // 테마 색상
  "enabled": true                      // 사용자 설정 여부
}
```

## 🎯 관심사 카테고리

| 코드 | 이름 | 설명 | 관련 알림 |
|------|------|------|-----------|
| `CULTURE` | 문화행사 | 공연, 전시, 축제 등 | 문화행사 시작 알림, 티켓 오픈 알림 |
| `SPORTS` | 체육시설 | 체육시설 및 운동 프로그램 | 시설 예약 가능 알림, 프로그램 모집 |
| `ENVIRONMENT` | 환경정보 | 대기질, 날씨 등 | 대기질 악화 알림, 폭염/한파 주의보 |
| `TRANSPORTATION` | 교통정보 | 따릉이, 지하철 등 | 따릉이 부족/포화 알림, 지하철 지연 |
| `FACILITIES` | 공공시설 | 도서관, 공원 등 | 시설 휴관 알림, 특별 프로그램 안내 |

## 🔒 권한 관리

### 사용자 정보 접근 권한
- **본인 정보**: 모든 정보 조회/수정 가능
- **타인 정보**: 공개 정보만 조회 가능 (닉네임, 프로필 이미지)
- **관리자**: 모든 사용자 정보 조회 가능

### 관심사 설정 권한
- **본인 관심사**: 조회/수정 가능
- **타인 관심사**: 조회 불가
- **관리자**: 모든 사용자 관심사 조회 가능

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| USER_001 | 사용자를 찾을 수 없음 | 존재하지 않는 사용자 ID |
| USER_002 | 권한이 없음 | 다른 사용자 정보 수정 시도 |
| USER_003 | 유효하지 않은 닉네임 | 닉네임 형식 오류 (2-20자) |
| USER_004 | 중복된 닉네임 | 이미 사용 중인 닉네임 |
| USER_005 | 유효하지 않은 관심사 | 존재하지 않는 관심사 카테고리 |
| USER_006 | 관심사 개수 초과 | 최대 5개까지 선택 가능 |

## 📋 사용 예시

### 1. 사용자 프로필 조회 및 수정

```javascript
// 내 정보 조회
const getMyProfile = async () => {
  const response = await fetch('/api/users/me?oauthUserId=123&oauthProvider=KAKAO', {
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  });
  return response.json();
};

// 프로필 수정
const updateProfile = async (nickname, profileImageUrl) => {
  const response = await fetch(`/api/users/${userId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      nickname,
      profileImageUrl
    })
  });
  return response.json();
};
```

### 2. 관심사 관리

```javascript
// 현재 관심사 조회
const getMyInterests = async () => {
  const response = await fetch('/api/users/interests', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ userId })
  });
  return response.json();
};

// 관심사 업데이트
const updateInterests = async (interests) => {
  const response = await fetch('/api/users/interests', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      userId,
      interests
    })
  });
  return response.json();
};

// 사용 예시
updateInterests(['CULTURE', 'ENVIRONMENT', 'TRANSPORTATION']);
```

### 3. 관심사 선택 UI 구현

```javascript
// 관심사 카테고리 목록 조회
const getInterestCategories = async () => {
  const response = await fetch('/api/users/interests/categories');
  return response.json();
};

// React 컴포넌트 예시
const InterestSelector = () => {
  const [categories, setCategories] = useState([]);
  const [selectedInterests, setSelectedInterests] = useState([]);

  useEffect(() => {
    getInterestCategories().then(data => {
      setCategories(data.data.categories);
    });
  }, []);

  const handleInterestToggle = (categoryCode) => {
    setSelectedInterests(prev => 
      prev.includes(categoryCode)
        ? prev.filter(code => code !== categoryCode)
        : [...prev, categoryCode]
    );
  };

  return (
    <div className="interest-selector">
      {categories.map(category => (
        <div key={category.code} className="interest-item">
          <input
            type="checkbox"
            checked={selectedInterests.includes(category.code)}
            onChange={() => handleInterestToggle(category.code)}
          />
          <span className="icon">{category.icon}</span>
          <span className="name">{category.name}</span>
          <span className="description">{category.description}</span>
        </div>
      ))}
    </div>
  );
};
```

---

**업데이트**: 2025-01-XX  
**버전**: v1.0.0
