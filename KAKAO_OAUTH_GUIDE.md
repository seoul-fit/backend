# 카카오 OAuth 2.0 로그인 가이드

UrbanPing 프로젝트에서 카카오 공식 문서 기준으로 구현된 OAuth 2.0 Authorization Code Flow 사용법을 안내합니다.

## 🔄 카카오 OAuth 인증 플로우

### 1. 인가 코드 요청

#### 방법 1: 직접 리다이렉트
```javascript
// 프론트엔드에서 카카오 인증 서버로 직접 리다이렉트
const kakaoAuthUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code`;
window.location.href = kakaoAuthUrl;
```

#### 방법 2: 서버 API 사용
```javascript
// 서버에서 생성된 인증 URL 사용
const response = await fetch('/api/auth/oauth/url/kakao?redirectUri=http://localhost:3000/callback');
const { authUrl } = await response.json();
window.location.href = authUrl;
```

#### 방법 3: 서버 리다이렉트 엔드포인트 사용
```javascript
// 서버가 직접 리다이렉트 처리
window.location.href = '/api/auth/oauth/authorize/kakao?redirectUri=http://localhost:3000/callback';
```

### 2. 인가 코드 수신

카카오에서 승인 후 리다이렉트 URI로 인가 코드가 전달됩니다:

```
http://localhost:3000/callback?code=AUTHORIZATION_CODE&state=STATE
```

### 3. 액세스 토큰 교환 및 로그인

```javascript
// 서버로 인가 코드 전송하여 로그인 처리
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

const { accessToken, refreshToken, userId, nickname } = await response.json();
```

## 🛠️ API 엔드포인트

### 1. OAuth 로그인 (Authorization Code Flow)

**POST** `/api/auth/oauth/login`

```json
{
  "provider": "KAKAO",
  "authorizationCode": "authorization_code_from_kakao",
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

### 3. OAuth 인증 URL 생성

**GET** `/api/auth/oauth/url/{provider}`

```
GET /api/auth/oauth/url/kakao?redirectUri=http://localhost:3000/callback&scope=profile_nickname,account_email&state=random_state
```

**응답:**
```json
{
  "authUrl": "https://kauth.kakao.com/oauth/authorize?client_id=CLIENT_ID&redirect_uri=REDIRECT_URI&response_type=code&scope=profile_nickname,account_email&state=random_state",
  "provider": "kakao",
  "redirectUri": "http://localhost:3000/callback"
}
```

### 4. OAuth 인가 코드 요청 (리다이렉트)

**GET** `/api/auth/oauth/authorize/{provider}`

```
GET /api/auth/oauth/authorize/kakao?redirectUri=http://localhost:3000/callback&scope=profile_nickname,account_email
```

이 엔드포인트는 302 리다이렉트로 카카오 인증 서버로 이동시킵니다.

### 5. OAuth 로그아웃

**POST** `/api/auth/oauth/logout`

```
POST /api/auth/oauth/logout?provider=KAKAO&accessToken=ACCESS_TOKEN
```

**응답:**
```json
{
  "id": 12345678
}
```

### 6. OAuth 연결 해제

**POST** `/api/auth/oauth/unlink`

```
POST /api/auth/oauth/unlink?provider=KAKAO&accessToken=ACCESS_TOKEN
```

**응답:**
```json
{
  "id": 12345678
}
```

## 🎯 카카오 스코프 (동의항목) 사용

### 기본 스코프
```javascript
// 기본 정보만 요청
const scope = 'profile_nickname,profile_image,account_email';
```

### 추가 스코프
```javascript
// 추가 정보 요청
const scope = 'profile_nickname,account_email,gender,age_range,birthday';

// 친구 목록 및 메시지 전송 권한
const scope = 'profile_nickname,friends,talk_message';
```

### 사용 가능한 스코프 목록

| 스코프 | 설명 | 필수 여부 |
|--------|------|-----------|
| `profile_nickname` | 닉네임 | 선택 |
| `profile_image` | 프로필 사진 | 선택 |
| `account_email` | 카카오계정(이메일) | 선택 |
| `gender` | 성별 | 선택 |
| `age_range` | 연령대 | 선택 |
| `birthday` | 생일 | 선택 |
| `birthyear` | 출생연도 | 선택 |
| `friends` | 카카오톡 친구 목록 | 선택 |
| `talk_message` | 카카오톡 메시지 전송 | 선택 |

## 🔐 보안 고려사항

### 1. 클라이언트 시크릿 사용 (선택사항)

```yaml
# application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET} # 선택사항
```

### 2. State 파라미터 사용 (CSRF 방지)

```javascript
// 랜덤 state 생성
const state = Math.random().toString(36).substring(2, 15);
sessionStorage.setItem('oauth_state', state);

// 인증 URL에 state 포함
const authUrl = `/api/auth/oauth/url/kakao?redirectUri=${redirectUri}&state=${state}`;

// 콜백에서 state 검증
const urlParams = new URLSearchParams(window.location.search);
const returnedState = urlParams.get('state');
const originalState = sessionStorage.getItem('oauth_state');

if (returnedState !== originalState) {
  throw new Error('CSRF 공격 가능성 - state 불일치');
}
```

### 3. HTTPS 사용 필수

모든 OAuth 통신은 HTTPS를 통해 이루어져야 합니다.

## 🚀 프론트엔드 통합 예시

### React 컴포넌트 예시

```jsx
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

function KakaoLogin() {
  const [isLoading, setIsLoading] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  // 카카오 로그인 시작
  const handleKakaoLogin = async () => {
    try {
      setIsLoading(true);
      
      // State 생성 (CSRF 방지)
      const state = Math.random().toString(36).substring(2, 15);
      sessionStorage.setItem('oauth_state', state);
      
      // 서버에서 인증 URL 생성
      const response = await fetch(`/api/auth/oauth/url/kakao?redirectUri=${encodeURIComponent(window.location.origin + '/oauth/callback')}&state=${state}`);
      const { authUrl } = await response.json();
      
      // 카카오 인증 페이지로 이동
      window.location.href = authUrl;
    } catch (error) {
      console.error('카카오 로그인 시작 실패:', error);
      setIsLoading(false);
    }
  };

  return (
    <div>
      <button 
        onClick={handleKakaoLogin} 
        disabled={isLoading}
        style={{
          backgroundColor: '#FEE500',
          border: 'none',
          borderRadius: '6px',
          padding: '12px 24px',
          fontSize: '16px',
          cursor: isLoading ? 'not-allowed' : 'pointer'
        }}
      >
        {isLoading ? '로그인 중...' : '카카오 로그인'}
      </button>
    </div>
  );
}

// OAuth 콜백 처리 컴포넌트
function OAuthCallback() {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const handleCallback = async () => {
      const urlParams = new URLSearchParams(location.search);
      const code = urlParams.get('code');
      const state = urlParams.get('state');
      const error = urlParams.get('error');

      // 에러 처리
      if (error) {
        console.error('OAuth 에러:', error);
        navigate('/login?error=oauth_failed');
        return;
      }

      // State 검증
      const originalState = sessionStorage.getItem('oauth_state');
      if (state !== originalState) {
        console.error('State 불일치 - CSRF 공격 가능성');
        navigate('/login?error=csrf_detected');
        return;
      }

      // 인가 코드로 로그인 처리
      if (code) {
        try {
          const response = await fetch('/api/auth/oauth/login', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({
              provider: 'KAKAO',
              authorizationCode: code,
              redirectUri: window.location.origin + '/oauth/callback'
            })
          });

          if (!response.ok) {
            throw new Error('로그인 실패');
          }

          const data = await response.json();
          
          // 토큰 저장
          localStorage.setItem('accessToken', data.accessToken);
          localStorage.setItem('refreshToken', data.refreshToken);
          
          // State 정리
          sessionStorage.removeItem('oauth_state');
          
          // 메인 페이지로 이동
          navigate('/dashboard');
        } catch (error) {
          console.error('OAuth 로그인 실패:', error);
          navigate('/login?error=login_failed');
        }
      }
    };

    handleCallback();
  }, [location, navigate]);

  return <div>로그인 처리 중...</div>;
}

export { KakaoLogin, OAuthCallback };
```

## 🔄 로그아웃 및 연결 해제

### 로그아웃

```javascript
// 카카오 로그아웃 (세션 종료)
const logout = async () => {
  const accessToken = localStorage.getItem('accessToken');
  
  try {
    await fetch(`/api/auth/oauth/logout?provider=KAKAO&accessToken=${accessToken}`, {
      method: 'POST'
    });
    
    // 로컬 토큰 삭제
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    
    // 로그인 페이지로 이동
    window.location.href = '/login';
  } catch (error) {
    console.error('로그아웃 실패:', error);
  }
};
```

### 연결 해제

```javascript
// 카카오 연결 해제 (앱과 사용자 연결 완전 해제)
const unlink = async () => {
  const accessToken = localStorage.getItem('accessToken');
  
  if (confirm('정말로 연결을 해제하시겠습니까? 모든 데이터가 삭제됩니다.')) {
    try {
      await fetch(`/api/auth/oauth/unlink?provider=KAKAO&accessToken=${accessToken}`, {
        method: 'POST'
      });
      
      // 로컬 토큰 삭제
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      
      alert('연결이 해제되었습니다.');
      window.location.href = '/login';
    } catch (error) {
      console.error('연결 해제 실패:', error);
    }
  }
};
```

## 🐛 에러 처리

### 주요 카카오 OAuth 에러 코드

| 에러 코드 | 설명 | 해결 방법 |
|-----------|------|-----------|
| KOE001 | 잘못된 요청 | 요청 파라미터 확인 |
| KOE002 | 잘못된 클라이언트 | 앱 키 확인 |
| KOE003 | 잘못된 인가 코드 | 새로운 인가 코드 요청 |
| KOE004 | 권한 없는 클라이언트 | 앱 설정 확인 |
| KOE401 | 유효하지 않은 토큰 | 토큰 갱신 또는 재로그인 |

### 에러 처리 예시

```javascript
const handleOAuthError = (error) => {
  if (error.message.includes('KOE001')) {
    alert('요청 파라미터를 확인해주세요.');
  } else if (error.message.includes('KOE002')) {
    alert('앱 키를 확인해주세요.');
  } else if (error.message.includes('KOE003')) {
    alert('인가 코드가 만료되었습니다. 다시 로그인해주세요.');
  } else {
    alert('로그인 중 오류가 발생했습니다. 다시 시도해주세요.');
  }
};
```

## 📋 체크리스트

### 개발 전 확인사항

- [ ] 카카오 디벨로퍼스에서 앱 생성
- [ ] REST API 키 발급
- [ ] 리다이렉트 URI 등록
- [ ] 동의항목 설정
- [ ] 클라이언트 시크릿 설정 (선택사항)

### 구현 확인사항

- [ ] HTTPS 사용
- [ ] State 파라미터 검증
- [ ] 에러 처리 구현
- [ ] 토큰 안전 저장
- [ ] 로그아웃/연결해제 구현

---

이 가이드를 통해 카카오 공식 문서 기준의 안전하고 표준적인 OAuth 2.0 로그인을 구현할 수 있습니다. 추가 질문이나 문제가 있으면 GitHub Issues에 등록해주세요.
