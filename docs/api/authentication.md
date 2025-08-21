# 인증 API

> OAuth 2.0 기반 사용자 인증 및 JWT 토큰 관리

## 📋 개요

Seoul Fit은 카카오 OAuth 2.0 Authorization Code Flow를 통한 소셜 로그인과 JWT 토큰 기반 인증을 제공합니다.

**Base URL**: `/api/auth`

## 🔐 인증 플로우

### OAuth 2.0 Authorization Code Flow (권장)

```mermaid
sequenceDiagram
    participant Client as 클라이언트
    participant Auth as 인증 서버
    participant Kakao as 카카오
    
    Client->>Kakao: 1. 인가코드 요청
    Kakao->>Client: 2. 인가코드 반환
    Client->>Auth: 3. 인가코드 검증
    Auth->>Kakao: 4. 토큰 교환
    Kakao->>Auth: 5. 사용자 정보
    Auth->>Client: 6. JWT 토큰
```

## 📚 API 엔드포인트

### 1. OAuth 인가코드 검증

프론트엔드에서 받은 인가코드로 OAuth 제공자에서 사용자 정보를 조회합니다.

```http
POST /api/auth/oauth/authorizecheck
Content-Type: application/json

{
  "provider": "KAKAO",
  "authorizationCode": "authorization_code_from_kakao",
  "redirectUri": "http://localhost:3000/auth/callback"
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "oauthUserId": "123456789",
    "nickname": "홍길동",
    "email": "user@example.com",
    "profileImageUrl": "https://example.com/profile.jpg",
    "provider": "KAKAO"
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 2. OAuth 사용자 확인

OAuth 사용자 존재 여부를 확인합니다.

```http
POST /api/auth/oauth/check
Content-Type: application/json

{
  "provider": "KAKAO",
  "oauthUserId": "123456789"
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "exists": true,
    "userId": 1,
    "nickname": "홍길동",
    "email": "user@example.com"
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 3. OAuth 회원가입

OAuth 정보로 새 사용자를 등록합니다.

```http
POST /api/auth/oauth/signup
Content-Type: application/json

{
  "provider": "KAKAO",
  "oauthUserId": "123456789",
  "nickname": "홍길동",
  "email": "user@example.com",
  "profileImageUrl": "https://example.com/profile.jpg",
  "interests": ["CULTURE", "SPORTS", "ENVIRONMENT"]
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userId": 1,
    "nickname": "홍길동",
    "email": "user@example.com"
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 4. OAuth 로그인

OAuth 권한부여 승인코드를 사용하여 로그인합니다.

```http
POST /api/auth/oauth/login
Content-Type: application/json

{
  "provider": "KAKAO",
  "authorizationCode": "authorization_code_from_kakao",
  "redirectUri": "http://localhost:3000/auth/callback"
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "userId": 1,
    "nickname": "홍길동",
    "email": "user@example.com"
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 5. 토큰 갱신

리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.

```http
POST /api/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**응답**
```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 6. OAuth 로그아웃

OAuth 제공자에서 로그아웃 처리합니다.

```http
POST /api/auth/oauth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**응답**
```json
{
  "status": "success",
  "data": {
    "result": "로그아웃 성공",
    "message": "로그아웃이 완료되었습니다."
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 7. 위치 기반 로그인

사용자 로그인과 동시에 위치 정보를 전달하여 실시간 트리거를 평가합니다.

```http
POST /api/auth/login/location
Content-Type: application/json

{
  "userId": "user@example.com",
  "latitude": 37.5665,
  "longitude": 126.9780,
  "radius": 2000
}
```

**응답**
```json
{
  "status": "success",
  "data": {
    "success": true,
    "userId": 1,
    "nickname": "홍길동",
    "email": "user@example.com",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "triggerEvaluation": {
      "evaluatedCount": 3,
      "triggeredCount": 1,
      "notifications": [
        {
          "type": "AIR_QUALITY",
          "title": "대기질 주의",
          "message": "현재 지역 미세먼지 농도가 높습니다."
        }
      ]
    }
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 8. OAuth 연결 해제

OAuth 제공자와의 연결을 해제합니다.

```http
POST /api/auth/oauth/unlink
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**응답**
```json
{
  "status": "success",
  "data": {
    "result": "연결 해제 성공",
    "message": "연결 해제가 완료되었습니다."
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

## 📝 요청/응답 스키마

### OAuthAuthorizeCheckRequest
```json
{
  "provider": "KAKAO",           // 필수: OAuth 제공자
  "authorizationCode": "string", // 필수: 인가코드
  "redirectUri": "string"        // 필수: 리다이렉트 URI
}
```

### OAuthSignUpRequest
```json
{
  "provider": "KAKAO",                    // 필수: OAuth 제공자
  "oauthUserId": "string",               // 필수: OAuth 사용자 ID
  "nickname": "string",                  // 필수: 닉네임
  "email": "string",                     // 필수: 이메일
  "profileImageUrl": "string",           // 선택: 프로필 이미지 URL
  "interests": ["CULTURE", "SPORTS"]     // 선택: 관심사 목록
}
```

### TokenResponse
```json
{
  "accessToken": "string",    // JWT 액세스 토큰
  "refreshToken": "string",   // JWT 리프레시 토큰
  "tokenType": "Bearer",      // 토큰 타입
  "expiresIn": 86400,        // 만료 시간 (초)
  "userId": 1,               // 사용자 ID
  "nickname": "string",      // 닉네임
  "email": "string"          // 이메일
}
```

## 🔒 보안 고려사항

### JWT 토큰 관리
- **액세스 토큰**: 24시간 유효
- **리프레시 토큰**: 30일 유효
- **토큰 저장**: 클라이언트에서 안전하게 저장 (HttpOnly Cookie 권장)

### OAuth 보안
- **PKCE**: Authorization Code Flow에서 PKCE 사용 권장
- **State 파라미터**: CSRF 공격 방지를 위한 state 파라미터 사용
- **Redirect URI**: 등록된 URI만 허용

### API 보안
- **HTTPS**: 모든 인증 관련 요청은 HTTPS 사용 필수
- **토큰 검증**: 모든 보호된 리소스 접근 시 JWT 토큰 검증
- **Rate Limiting**: 인증 시도 횟수 제한

## ❌ 에러 코드

| 코드 | 메시지 | 설명 |
|------|--------|------|
| AUTH_001 | 유효하지 않은 인가코드 | OAuth 인가코드가 만료되었거나 잘못됨 |
| AUTH_002 | 사용자를 찾을 수 없음 | 등록되지 않은 사용자 |
| AUTH_003 | 토큰이 만료됨 | JWT 토큰이 만료됨 |
| AUTH_004 | 유효하지 않은 토큰 | JWT 토큰이 손상되었거나 잘못됨 |
| AUTH_005 | OAuth 제공자 오류 | 카카오 API 호출 실패 |
| AUTH_006 | 이미 등록된 사용자 | 중복 회원가입 시도 |

## 📋 사용 예시

### 1. 카카오 로그인 전체 플로우

```javascript
// 1. 카카오 인가코드 획득 (프론트엔드)
const authCode = getKakaoAuthCode();

// 2. 인가코드 검증
const checkResponse = await fetch('/api/auth/oauth/authorizecheck', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    provider: 'KAKAO',
    authorizationCode: authCode,
    redirectUri: 'http://localhost:3000/auth/callback'
  })
});

// 3. 사용자 존재 여부 확인
const userCheck = await fetch('/api/auth/oauth/check', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    provider: 'KAKAO',
    oauthUserId: checkResponse.data.oauthUserId
  })
});

// 4. 로그인 또는 회원가입
if (userCheck.data.exists) {
  // 로그인
  const loginResponse = await fetch('/api/auth/oauth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      provider: 'KAKAO',
      authorizationCode: authCode,
      redirectUri: 'http://localhost:3000/auth/callback'
    })
  });
} else {
  // 회원가입
  const signupResponse = await fetch('/api/auth/oauth/signup', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      provider: 'KAKAO',
      oauthUserId: checkResponse.data.oauthUserId,
      nickname: checkResponse.data.nickname,
      email: checkResponse.data.email,
      interests: ['CULTURE', 'ENVIRONMENT']
    })
  });
}
```

### 2. 토큰 갱신

```javascript
// 토큰 만료 시 자동 갱신
async function refreshToken(refreshToken) {
  const response = await fetch(`/api/auth/refresh?refreshToken=${refreshToken}`, {
    method: 'POST'
  });
  
  if (response.ok) {
    const data = await response.json();
    localStorage.setItem('accessToken', data.data.accessToken);
    localStorage.setItem('refreshToken', data.data.refreshToken);
    return data.data.accessToken;
  }
  
  // 리프레시 토큰도 만료된 경우 재로그인 필요
  redirectToLogin();
}
```

---

**업데이트**: 2025-08-21  
**버전**: v1.0.0
