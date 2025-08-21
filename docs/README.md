# Seoul Fit 문서 가이드

[홈으로 돌아가기](../README.md)

> 서울시 공공 데이터를 활용한 실시간 도시 정보 알림 시스템 문서

## 📚 문서 카테고리

### 🚀 API 문서
REST API 엔드포인트 및 기술 문서

- **[API 전체 가이드](api/README.md)** - 모든 API 엔드포인트 개요
- **[인증 API](api/authentication.md)** - OAuth 로그인 및 JWT 토큰 관리
- **[사용자 관리 API](api/users.md)** - 사용자 프로필 및 관심사 관리
- **[알림 API](api/notifications.md)** - 실시간 알림 생성 및 관리
- **[트리거 API](api/triggers.md)** - 알림 트리거 평가 및 설정
- **[지리정보 API](api/geocoding.md)** - 위치 좌표 및 행정구역 변환
- **[위치기반 API](api/location.md)** - 주변 시설 검색 및 추천
- **[검색 API](api/search.md)** - 통합 검색 및 자동완성

#### 서울시 공공데이터 API
- **[대기질 API](api/air-quality.md)** - 실시간 대기환경 정보
- **[문화행사 API](api/cultural-events.md)** - 공연, 전시, 축제 정보
- **[공공시설 API](api/public-facilities.md)** - 도서관, 공원, 체육시설

### 🛠️ 개발 가이드
프로젝트 기여 및 개발 방법

- **[기여 가이드](guides/contributing.md)** - 오픈소스 기여 방법
- **[OAuth 가이드](guides/oauth/README.md)** - 카카오 OAuth 연동 설정
- **[트리거 시스템 가이드](guides/triggers/README.md)** - 트리거 개발 및 사용법

### 🔐 보안 및 정책
보안 정책 및 데이터 사용 정책

- **[보안 정책](security/security-policy.md)** - 보안 취약점 신고 및 정책
- **[데이터 출처](security/data-attribution.md)** - 서울시 공공데이터 이용 조건

### 🧪 예제 및 도구
실행 가능한 예제 및 개발 도구

- **[cURL 예제](examples/curl-examples.md)** - 명령줄에서 API 호출 예제
- **[Postman 컬렉션](examples/postman-collection.json)** - API 테스트용 Postman 컬렉션

### 📋 기타 문서
- **[서울 공원 프로젝트](seoul_park/README.md)** - 서울 공원 관련 구현 문서

## 🎯 빠른 시작

### 새로운 개발자를 위한 추천 순서

1. **[프로젝트 개요](../README.md)** - 메인 README 먼저 읽기
2. **[API 전체 가이드](api/README.md)** - API 구조 파악
3. **[OAuth 설정](guides/oauth/kakao-setup.md)** - 카카오 로그인 설정
4. **[기여 가이드](guides/contributing.md)** - 개발 참여 방법
5. **[cURL 예제](examples/curl-examples.md)** - API 테스트해보기

### API 사용자를 위한 추천 순서

1. **[API 전체 가이드](api/README.md)** - API 개요 및 인증 방법
2. **[인증 API](api/authentication.md)** - 로그인 및 토큰 관리
3. **[관심 있는 기능 API](api/)** - 필요한 기능별 API 문서
4. **[Postman 컬렉션](examples/postman-collection.json)** - 실제 API 테스트

## 📞 문의 및 지원

- **GitHub Issues**: [이슈 등록](https://github.com/your-org/seoul-fit/issues)
- **GitHub Discussions**: [토론 참여](https://github.com/your-org/seoul-fit/discussions)
- **Email**: gmavsks@gmail.com

---

**업데이트**: 2025-08-21  
**버전**: v1.0.0
