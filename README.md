# seoul-fit Backend

## 0. Settings

## 1. Skills

## 2. OAuth 인증 기능

### 지원하는 OAuth 프로바이더
- KAKAO (카카오)
- GOOGLE (구글)
- NAVER (네이버)
- APPLE (애플)

### OAuth API 엔드포인트

#### 1. OAuth 사용자 확인
```
GET /api/auth/oauth/check?provider={PROVIDER}&oauthUserId={USER_ID}
```
- OAuth 사용자가 이미 가입되어 있는지 확인
- 응답: 사용자 존재 여부와 사용자 정보

#### 2. OAuth 회원가입
```
POST /api/auth/oauth/signup
Content-Type: application/json

{
  "provider": "KAKAO",
  "oauthUserId": "kakao123",
  "nickname": "홍길동",
  "email": "user@example.com",
  "profileImageUrl": "https://example.com/profile.jpg"
}
```

#### 3. OAuth 로그인
```
POST /api/auth/oauth/login
Content-Type: application/json

{
  "provider": "KAKAO",
  "oauthUserId": "kakao123"
}
```

### OAuth 인증 플로우

1. **신규 사용자**
   - `GET /api/auth/oauth/check` → 사용자 존재하지 않음
   - `POST /api/auth/oauth/signup` → 회원가입 및 토큰 발급

2. **기존 사용자**
   - `GET /api/auth/oauth/check` → 사용자 존재함
   - `POST /api/auth/oauth/login` → 로그인 및 토큰 발급

### 예외 처리

- `OAuthUserAlreadyExistsException`: 이미 가입된 OAuth 사용자
- `OAuthUserNotFoundException`: 등록되지 않은 OAuth 사용자
- 일반적인 validation 오류들

### 데이터베이스 스키마

User 테이블에 OAuth 관련 필드:
- `oauth_provider`: OAuth 제공자 (KAKAO, GOOGLE, NAVER, APPLE)
- `oauth_id`: OAuth 제공자에서의 사용자 식별자
- `profile_image_url`: 프로필 이미지 URL

## 3. 기존 인증 기능

### 일반 회원가입/로그인
- 이메일/비밀번호 기반 회원가입
- 이메일/비밀번호 기반 로그인
- JWT 토큰 기반 인증
- 리프레시 토큰을 통한 토큰 갱신
