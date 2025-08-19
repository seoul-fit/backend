# UrbanPing API 문서

> 서울시 공공 데이터를 활용한 실시간 도시 정보 알림 시스템 API

## 📋 개요

UrbanPing은 RESTful API를 통해 서울시의 다양한 공공 데이터와 사용자 맞춤형 알림 서비스를 제공합니다.

### API 버전
- **현재 버전**: v1
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`

### 인증 방식
- **OAuth 2.0**: 카카오 소셜 로그인
- **JWT Token**: API 요청 시 Bearer 토큰 사용

## 📚 API 카테고리

### 🔐 인증 관리
- [인증 API](./authentication.md) - OAuth 로그인, 토큰 관리

### 👤 사용자 관리  
- [사용자 API](./users.md) - 사용자 프로필, 관심사 관리

### 🔔 알림 시스템
- [알림 API](./notifications.md) - 알림 생성, 조회, 상태 관리

### ⚡ 트리거 시스템
- [트리거 API](./triggers.md) - 알림 트리거 설정 및 관리

### 🏙️ 서울시 공공데이터
- [지리정보 API](./geocoding.md) - 주소 변환, 행정구역 조회
- [위치기반 API](./location.md) - 주변 시설 검색, 개인화 추천
- [대기질 API](./air-quality.md) - 실시간 대기환경 정보
- [문화행사 API](./cultural-events.md) - 문화행사, 공연 정보
- [공공시설 API](./public-facilities.md) - 도서관, 공원, 체육시설 등

### 🔍 검색 시스템
- [검색 API](./search.md) - 통합 검색, 배치 검색

## 🚀 빠른 시작

### 1. 인증 토큰 획득
```bash
# OAuth 로그인
POST /api/auth/oauth/login
{
  "authorizationCode": "your-kakao-auth-code",
  "redirectUri": "your-redirect-uri"
}
```

### 2. API 호출 예시
```bash
# 사용자 정보 조회
GET /api/users/me?oauthUserId=123&oauthProvider=KAKAO
Authorization: Bearer your-jwt-token
```

### 3. 알림 생성
```bash
# 대기질 알림 생성
POST /api/notifications
Authorization: Bearer your-jwt-token
{
  "userId": 1,
  "notificationType": "AIR_QUALITY",
  "title": "대기질 악화 알림",
  "message": "현재 지역 미세먼지 농도가 높습니다.",
  "triggerCondition": {
    "threshold": 80,
    "operator": "GREATER_THAN"
  }
}
```

## 📊 응답 형식

### 성공 응답
```json
{
  "status": "success",
  "data": {
    // 응답 데이터
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

### 에러 응답
```json
{
  "status": "error",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력 데이터가 올바르지 않습니다.",
    "details": [
      {
        "field": "email",
        "message": "이메일 형식이 올바르지 않습니다."
      }
    ]
  },
  "timestamp": "2025-01-XX 10:00:00"
}
```

## 🔧 개발 도구

### Swagger UI
- **URL**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### Postman 컬렉션
- [Postman Collection 다운로드](examples/postman-collection.json)

### cURL 예제
- [cURL 사용 예제](examples/curl-examples.md)

## 📝 상태 코드

| 코드 | 설명 |
|------|------|
| 200 | 성공 |
| 201 | 생성 성공 |
| 400 | 잘못된 요청 |
| 401 | 인증 실패 |
| 403 | 권한 없음 |
| 404 | 리소스 없음 |
| 500 | 서버 오류 |

## 🔒 보안

- 모든 API 요청은 HTTPS 사용 권장
- JWT 토큰은 Authorization 헤더에 Bearer 형식으로 전송
- API 키는 환경 변수로 관리
- 요청 제한: 분당 100회

## 📞 지원

- **GitHub Issues**: [이슈 등록](https://github.com/your-org/urbanping/issues)
- **API 문의**: urbanping-api@example.com
- **문서 개선**: [기여 가이드](../../CONTRIBUTING.md)

---

**업데이트**: 2025-01-XX  
**버전**: v1.0.0
