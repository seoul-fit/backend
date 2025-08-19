# OAuth 인가코드 검증 API 가이드

## 개요

프론트엔드에서 OAuth 제공자로부터 받은 인가코드를 사용하여 사용자 정보를 조회하는 API입니다.
회원가입 전에 사용자 정보를 미리 확인할 때 사용합니다.

## API 엔드포인트

```
POST /api/auth/oauth/authorizecheck
```

## 요청 형식

### Headers
```
Content-Type: application/json
```

### Request Body
```json
{
  "provider": "KAKAO",
  "authorizationCode": "abc123def456",
  "redirectUri": "http://localhost:3000/auth/callback"
}
```

### 필드 설명
- `provider`: OAuth 제공자 (KAKAO, GOOGLE 등)
- `authorizationCode`: OAuth 제공자에서 받은 인가코드
- `redirectUri`: OAuth 인증 시 사용한 리다이렉트 URI

## 응답 형식

### 성공 응답 (200 OK)
```json
{
  "provider": "KAKAO",
  "oauthUserId": "123456789",
  "nickname": "홍길동",
  "email": "user@example.com",
  "profileImageUrl": "https://example.com/profile.jpg"
}
```

### 필드 설명
- `provider`: OAuth 제공자
- `oauthUserId`: OAuth 제공자에서의 사용자 고유 ID
- `nickname`: 사용자 닉네임
- `email`: 사용자 이메일
- `profileImageUrl`: 프로필 이미지 URL

### 오류 응답

#### 400 Bad Request - 잘못된 요청
```json
{
  "error": "Bad Request",
  "message": "권한부여 승인코드는 필수입니다.",
  "timestamp": "2025-08-07T03:00:00Z"
}
```

#### 500 Internal Server Error - 서버 오류
```json
{
  "error": "Internal Server Error",
  "message": "OAuth 인가코드 검증에 실패했습니다: Invalid authorization code",
  "timestamp": "2025-08-07T03:00:00Z"
}
```

## 사용 시나리오

### 1. 카카오 OAuth 인가코드 검증

```javascript
// 프론트엔드 예제 (JavaScript)
const checkAuthorizationCode = async (authorizationCode) => {
  try {
    const response = await fetch('/api/auth/oauth/authorizecheck', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        provider: 'KAKAO',
        authorizationCode: authorizationCode,
        redirectUri: 'http://localhost:3000/auth/callback'
      })
    });

    if (response.ok) {
      const userInfo = await response.json();
      console.log('사용자 정보:', userInfo);
      
      // 회원가입 폼에 정보 미리 채우기
      document.getElementById('nickname').value = userInfo.nickname;
      document.getElementById('email').value = userInfo.email;
      document.getElementById('profileImage').src = userInfo.profileImageUrl;
      
      return userInfo;
    } else {
      throw new Error('인가코드 검증 실패');
    }
  } catch (error) {
    console.error('오류:', error);
    alert('사용자 정보 조회에 실패했습니다.');
  }
};
```

### 2. 전체 OAuth 플로우

```
1. 프론트엔드: 카카오 로그인 버튼 클릭
2. 프론트엔드: 카카오 인증 페이지로 리다이렉트
3. 사용자: 카카오 로그인 및 권한 동의
4. 카카오: 인가코드와 함께 리다이렉트 URI로 리다이렉트
5. 프론트엔드: 인가코드 추출
6. 프론트엔드: /api/auth/oauth/authorizecheck API 호출
7. 백엔드: 인가코드로 카카오에서 사용자 정보 조회
8. 백엔드: 사용자 정보 반환
9. 프론트엔드: 회원가입 폼에 정보 미리 채우기
10. 사용자: 추가 정보 입력 후 회원가입 완료
```

## 주의사항

1. **인가코드 유효성**: 인가코드는 일회성이며 짧은 시간 내에 사용해야 합니다.
2. **리다이렉트 URI**: OAuth 인증 시 사용한 리다이렉트 URI와 정확히 일치해야 합니다.
3. **보안**: 인가코드는 민감한 정보이므로 HTTPS를 통해 전송해야 합니다.
4. **에러 처리**: 네트워크 오류나 OAuth 제공자 오류에 대한 적절한 처리가 필요합니다.

## 관련 API

- `POST /api/auth/oauth/signup`: OAuth 회원가입
- `POST /api/auth/oauth/login`: OAuth 로그인
- `GET /api/auth/oauth/url/{provider}`: OAuth 인증 URL 생성

## 테스트

Swagger UI에서 테스트할 수 있습니다:
```
http://localhost:8080/swagger-ui.html
```

또는 curl 명령어로 테스트:
```bash
curl -X POST "http://localhost:8080/api/auth/oauth/authorizecheck" \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "KAKAO",
    "authorizationCode": "your_authorization_code",
    "redirectUri": "http://localhost:3000/auth/callback"
  }'
```
