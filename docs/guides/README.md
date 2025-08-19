# 개발 가이드

> UrbanPing 프로젝트 개발 및 기여를 위한 가이드 모음

## 📋 가이드 목록

### 🤝 프로젝트 기여
- **[기여 가이드](contributing.md)** - 오픈소스 프로젝트 기여 방법
  - 코드 기여 절차
  - 이슈 등록 및 PR 생성
  - 코딩 컨벤션 및 리뷰 프로세스

### 🔐 OAuth 인증 시스템
OAuth 2.0 기반 소셜 로그인 구현 가이드

- **[OAuth 가이드 개요](oauth/README.md)** - OAuth 시스템 전체 개요
- **[카카오 OAuth 설정](oauth/kakao-setup.md)** - 카카오 개발자 콘솔 설정
- **[OAuth 일반 가이드](oauth/general-guide.md)** - OAuth 2.0 구현 방법
- **[인증 체크 API](oauth/authorize-check-api.md)** - 인증 상태 확인 API

### ⚡ 트리거 시스템
실시간 알림 트리거 개발 및 운영 가이드

- **[트리거 시스템 개요](triggers/README.md)** - 트리거 시스템 전체 개요
- **[트리거 개발 가이드](triggers/development.md)** - 새로운 트리거 개발 방법
- **[시스템 분석](triggers/system-analysis.md)** - 트리거 시스템 아키텍처 분석
- **[사용법 가이드](triggers/usage.md)** - 트리거 설정 및 사용 방법

## 🎯 개발 시작하기

### 1. 환경 설정
```bash
# 1. 저장소 클론
git clone https://github.com/your-org/urbanping.git
cd urbanping

# 2. 의존성 설치
mvn clean install

# 3. 환경 변수 설정
export SEOUL_API_KEY=your-seoul-api-key
export JWT_SECRET=your-jwt-secret
```

### 2. 개발 워크플로우
1. **[기여 가이드](contributing.md)** 숙지
2. **이슈 확인** 또는 새 이슈 생성
3. **브랜치 생성** (`feature/your-feature-name`)
4. **개발 및 테스트**
5. **PR 생성** 및 리뷰 요청

### 3. 주요 개발 영역

#### 새로운 트리거 개발
- **[트리거 개발 가이드](triggers/development.md)** 참조
- 대기질, 교통, 문화행사 등 다양한 트리거 구현 가능

#### OAuth 제공자 추가
- **[OAuth 일반 가이드](oauth/general-guide.md)** 참조
- 네이버, 구글 등 추가 OAuth 제공자 연동

#### 새로운 공공데이터 연동
- 서울시 공공데이터 포탈의 다른 API 연동
- 데이터 파싱 및 알림 로직 구현

## 🔧 개발 도구

### 필수 도구
- **Java 21+**: 개발 언어
- **Maven 3.8+**: 빌드 도구
- **IntelliJ IDEA**: 권장 IDE
- **Postman**: API 테스트

### 유용한 리소스
- **[API 문서](../api/README.md)**: REST API 전체 가이드
- **[cURL 예제](../examples/curl-examples.md)**: 명령줄 API 테스트
- **[Postman 컬렉션](../examples/postman-collection.json)**: GUI API 테스트

## 📞 개발 지원

### 질문 및 토론
- **GitHub Discussions**: 기술적 질문 및 아이디어 토론
- **GitHub Issues**: 버그 리포트 및 기능 요청

### 코드 리뷰
- **Pull Request**: 코드 리뷰 및 피드백
- **코딩 컨벤션**: [기여 가이드](contributing.md) 참조

---

**업데이트**: 2025-01-XX  
**버전**: v1.0.0
