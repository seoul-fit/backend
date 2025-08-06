# OAuth 2.0 Authorization Code Flow 가이드

UrbanPing 프로젝트에서 OAuth 2.0 Authorization Code Flow를 사용한 인증 시스템 사용법을 안내합니다.

## 🔄 OAuth 인증 플로우

### 1. 클라이언트에서 OAuth 인증 URL로 리다이렉트

```javascript
// 프론트엔드에서 OAuth 인증 시작
const oauthUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code`;
window.location.href = oauthUrl;
```

### 2. OAuth 제공자에서 승인 후 리다이렉트

사용자가 OAuth 제공자에서 승인하면 `authorization_code`와 함께 리다이렉트됩니다:

```
http://localhost:3000/callback?code=AUTHORIZATION_CODE&state=STATE
```

### 3. 서버에 Authorization Code 전송하여 로그인

```javascript
// 프론트엔드에서 서버로 Authorization Code 전송
const response = await fetch('/api/auth/oauth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    provider: 'KAKAO',
    authorizationCode: 'AUTHORIZATION_CODE',
    redirectUri: 'http://localhost:3000/callback'
  })
});

const { accessToken, refreshToken } = await response.json();
```

## 🛠️ API 엔드포인트

### 1. OAuth 로그인 (Authorization Code Flow)

**POST** `/api/auth/oauth/login`

```json
{
  "provider": "KAKAO",
  "authorizationCode": "authorization_code_from_oauth_provider",
  "redirectUri": "http://localhost:3000/callback"
}
```

**응답:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "nickname": "사용자닉네임"
}
```

### 2. OAuth 간편 로그인

**POST** `/api/auth/oauth/login/simple`

```
POST /api/auth/oauth/login/simple?provider=KAKAO&code=AUTHORIZATION_CODE&redirectUri=http://localhost:3000/callback
```

### 3. OAuth 인증 URL 생성 (참고용)

**GET** `/api/auth/oauth/url/{provider}`

```
GET /api/auth/oauth/url/kakao?redirectUri=http://localhost:3000/callback&state=random_state
```

## 🔧 서버 내부 동작

### 1. Authorization Code → Access Token 교환

```java
// KakaoOAuthClient.java
public OAuthTokenResponse exchangeCodeForToken(String authorizationCode, String redirectUri) {
    // OAuth 제공자의 토큰 엔드포인트에 요청
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", redirectUri);
    params.add("code", authorizationCode);
    
    return restTemplate.postForObject(tokenUri, request, OAuthTokenResponse.class);
}
```

### 2. Access Token → 사용자 정보 조회

```java
// KakaoOAuthClient.java
public OAuthUserInfo getUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    
    Map<String, Object> response = restTemplate.exchange(
        userInfoUri, HttpMethod.GET, request, Map.class
    ).getBody();
    
    return extractUserInfo(response);
}
```

### 3. 사용자 조회 또는 생성

```java
// AuthenticationService.java
protected User findOrCreateUser(OAuthUserInfo userInfo) {
    return userPort.findByProviderAndOauthUserId(userInfo.getProvider(), userInfo.getOAuthId())
        .orElseGet(() -> {
            // 새 사용자 생성
            User newUser = User.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .status(UserStatus.ACTIVE)
                .build();
            return userPort.save(newUser);
        });
}
```

## 🔐 보안 고려사항

### 1. HTTPS 사용 필수
- 모든 OAuth 통신은 HTTPS를 통해 이루어져야 합니다.
- Authorization Code와 Access Token이 평문으로 전송되므로 보안이 중요합니다.

### 2. State 파라미터 사용
```javascript
const state = generateRandomString();
const oauthUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code&state=${state}`;

// 콜백에서 state 검증
if (callbackState !== originalState) {
  throw new Error('CSRF 공격 가능성');
}
```

### 3. Redirect URI 검증
- OAuth 제공자에 등록된 Redirect URI와 정확히 일치해야 합니다.
- 서버에서도 허용된 Redirect URI인지 검증해야 합니다.

## 🌐 지원하는 OAuth 제공자

### 1. Kakao
- **Authorization URL**: `https://kauth.kakao.com/oauth/authorize`
- **Token URL**: `https://kauth.kakao.com/oauth/token`
- **User Info URL**: `https://kapi.kakao.com/v2/user/me`

### 2. Google
- **Authorization URL**: `https://accounts.google.com/oauth/authorize`
- **Token URL**: `https://oauth2.googleapis.com/token`
- **User Info URL**: `https://www.googleapis.com/oauth2/v2/userinfo`

## 📝 환경 설정

### application.yml 설정

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET}
            redirect-uri: ${KAKAO_REDIRECT_URI:http://localhost:8080/login/oauth2/code/kakao}
            authorization-grant-type: authorization_code
            scope: profile_nickname, profile_image, account_email
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            redirect-uri: ${GOOGLE_REDIRECT_URI:http://localhost:8080/login/oauth2/code/google}
            scope: profile, email
        provider:
          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-name-attribute: id
```

## 🚀 프론트엔드 통합 예시

### React 예시

```jsx
import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

function OAuthCallback() {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');

    if (code) {
      handleOAuthLogin(code);
    }
  }, [location]);

  const handleOAuthLogin = async (authorizationCode) => {
    try {
      const response = await fetch('/api/auth/oauth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          provider: 'KAKAO',
          authorizationCode,
          redirectUri: window.location.origin + '/oauth/callback'
        })
      });

      const data = await response.json();
      
      // 토큰 저장
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);
      
      // 메인 페이지로 이동
      navigate('/dashboard');
    } catch (error) {
      console.error('OAuth 로그인 실패:', error);
      navigate('/login?error=oauth_failed');
    }
  };

  return <div>로그인 처리 중...</div>;
}
```

## 🔄 기존 방식과의 호환성

기존 방식(oauthUserId 직접 전달)도 여전히 지원하지만 deprecated 상태입니다:

```json
// Deprecated 방식
{
  "provider": "KAKAO",
  "oauthUserId": "12345678"
}
```

새로운 Authorization Code Flow 방식을 사용하는 것을 강력히 권장합니다.

## 🐛 트러블슈팅

### 1. "Invalid authorization code" 오류
- Authorization Code는 일회용이므로 재사용할 수 없습니다.
- 코드가 만료되었을 수 있습니다 (보통 10분).

### 2. "Redirect URI mismatch" 오류
- OAuth 제공자에 등록된 Redirect URI와 정확히 일치하는지 확인하세요.
- 프로토콜(http/https), 포트 번호까지 정확해야 합니다.

### 3. CORS 오류
- 프론트엔드와 백엔드가 다른 도메인인 경우 CORS 설정이 필요합니다.

---

이 가이드를 통해 UrbanPing의 OAuth 2.0 Authorization Code Flow를 안전하고 효율적으로 사용할 수 있습니다. 추가 질문이나 문제가 있으면 GitHub Issues에 등록해주세요.
