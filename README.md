# 🏙️ Seoul Fit

> 서울시 공공 데이터를 활용한 실시간 도시 정보 알림 시스템

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Contributions Welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg)](CONTRIBUTING.md)

## 📋 프로젝트 소개

Seoul Fit은 서울시의 다양한 공공 데이터를 실시간으로 모니터링하여 사용자에게 맞춤형 알림을 제공하는 오픈소스 프로젝트입니다.

### ✨ 주요 기능

- 🌡️ **날씨 기반 알림**: 폭염, 한파, 대기질 악화 시 실시간 알림
- 🚲 **따릉이 현황**: 대여소별 자전거 부족/포화 상태 알림
- 🏛️ **문화행사 정보**: 관심 있는 문화행사 및 공연 정보 알림
- 👥 **인구 혼잡도**: 지역별 실시간 혼잡도 정보 제공
- 📍 **위치 기반 서비스**: 사용자 위치 반경 2km 내 맞춤 정보
- ⚙️ **확장 가능한 아키텍처**: 플러그인 형태의 새로운 기능 추가 지원

### 🎯 대상 사용자

- 서울시민 및 방문객
- 실시간 도시 정보가 필요한 모든 사용자
- 공공 데이터 활용 개발자
- 오픈소스 기여자

## 🚀 빠른 시작

### 필수 요구사항

- Java 21 이상
- Maven 3.8 이상
- 서울시 공공데이터 API 키

### 설치 및 실행

```bash
# 1. 저장소 클론
git clone https://github.com/seoul-fit/backend.git
cd seoul-fit

# 2. 의존성 설치
mvn clean install

# 3. 애플리케이션 실행
mvn spring-boot:run

# 4. 브라우저에서 확인
open http://localhost:8080/swagger-ui.html
```

### 🔑 서울시 공공데이터 API 키 발급

Seoul Fit의 모든 기능을 사용하려면 서울시 공공데이터 포탈에서 API 키를 발급받아야 합니다.

#### 1단계: 회원가입 및 로그인
1. [서울시 공공데이터 포탈](https://data.seoul.go.kr/) 접속
2. 우상단 "회원가입" 클릭하여 계정 생성
3. 이메일 인증 완료 후 로그인

#### 2단계: API 키 신청
1. 로그인 후 인증키 신청
   - 일반 인증키 신청
   - 실시간 지하철 인증키 신청
2. "API 키 신청" 버튼 클릭
3. 다음 정보 입력:
   - **사용URL**: 도메인 입력
   - **관리용 대표 이메일**: 대표자 이메일
   - **활용용도**: 서울시 도시 정보 알림 서비스
   - **내용**: 서울시 도시 정보 알림 서비스

위 내용 입력 후 약관 동의 후 인증키 신청 버튼 클릭시 인증키 발급 완료


#### 3단계: API 키 확인
- 승인 완료 후 "API 키 관리"에서 발급된 키 확인
- 키는 영문자와 숫자로 구성된 32자리 문자열

### ⚙️ API 인증키 환경 설정

> **보안**: 실제 운영에서는 환경변수를 사용하세요. [환경 설정 가이드](docs/guides/environment-setup.md) 참조

#### application.yml 설정
`src/main/resources/application.yml` 파일 수정:

```yaml
# 서울시 공공데이터 API 설정
seoul:
  api:
    key: ${SEOUL_API_KEY:your-seoul-api-key-here}
    base-url: http://openapi.seoul.go.kr:8088
    endpoints:
      air-quality: /api/RealtimeCityAir/json/1/25/
      bike-status: /api/bikeList/json/1/1000/
      cultural-events: /api/culturalEventInfo/json/1/100/
      population: /api/SPOP_LOCAL_RESD_DONG/json/1/100/
```

### 🚨 보안 주의사항
- **API 키 노출 금지**: GitHub 등 공개 저장소에 API 키 업로드 금지
- **환경 변수 사용**: 코드에 직접 하드코딩하지 말고 환경 변수 활용
- **키 순환**: 정기적으로 API 키 재발급 권장
- **.gitignore 확인**: `application-local.yml` 등 로컬 설정 파일 제외


### 환경 설정

```bash
# application.yml 또는 환경 변수로 설정
export SEOUL_API_KEY=your-seoul-api-key
export JWT_SECRET=your-jwt-secret
```

## 📖 API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### 주요 API 엔드포인트

```http
# 사용자 관심사 관리
GET    /api/users/interests
PUT    /api/users/interests

# 알림 히스토리
GET    /api/notifications
POST   /api/notifications
PATCH  /api/notifications/{id}/read

# OAuth 인증
POST   /api/auth/oauth/signup
POST   /api/auth/oauth/login
```

## 🏗️ 아키텍처

### 시스템 구조

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Controllers   │  │   Event Listeners│                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                         │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Use Cases     │  │   Services      │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Entities      │  │   Domain Events │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                 Infrastructure Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │  Repositories   │  │  External APIs  │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

### 핵심 디자인 패턴

- **Hexagonal Architecture**: 도메인 중심 설계
- **Strategy Pattern**: 확장 가능한 트리거 시스템
- **Observer Pattern**: 이벤트 기반 알림 처리
- **Factory Pattern**: 유연한 객체 생성
- **Template Method Pattern**: 공통 로직 재사용

## 🔧 기술 스택

### Backend
- **Java 21**: 최신 LTS 버전
- **Spring Boot 3.x**: 웹 애플리케이션 프레임워크
- **Spring Security**: 인증 및 보안
- **Spring Data JPA**: 데이터 액세스
- **H2 Database**: 개발용 인메모리 데이터베이스

### External APIs
- **서울시 실시간 도시 데이터 API**
- **따릉이 현황 API**
- **대기환경 현황 API**
- **문화행사 정보 API**

### Tools & Libraries
- **Maven**: 빌드 도구
- **Swagger/OpenAPI**: API 문서화
- **Lombok**: 코드 간소화
- **Jackson**: JSON 처리
- **WebClient**: 비동기 HTTP 클라이언트

## 🤝 기여하기

UrbanPing은 오픈소스 프로젝트입니다. 여러분의 기여를 환영합니다!

### 기여 방법

1. **이슈 확인**: [GitHub Issues](https://github.com/your-org/urbanping/issues)에서 해결할 문제를 찾아보세요
2. **포크 & 클론**: 저장소를 포크하고 로컬에 클론하세요
3. **브랜치 생성**: `git checkout -b feature/your-feature-name`
4. **개발 & 테스트**: 기능을 개발하고 테스트를 작성하세요
5. **커밋 & 푸시**: 변경사항을 커밋하고 푸시하세요
6. **Pull Request**: PR을 생성하고 리뷰를 요청하세요

### 쉬운 기여 아이디어

- 🆕 **새로운 트리거 전략 추가**: 강수량, 지하철 혼잡도 등
- 🔧 **설정 개선**: 새로운 임계값이나 옵션 추가
- 📝 **문서 개선**: README, 주석, 가이드 문서 개선
- 🧪 **테스트 추가**: 기존 기능의 테스트 커버리지 향상
- 🐛 **버그 수정**: 발견된 버그 수정

자세한 내용은 [기여 가이드](CONTRIBUTING.md)를 참조하세요.

## 📊 프로젝트 현황

### 구현 완료 ✅
- OAuth 회원가입 시스템
- 사용자 관심사 관리
- 알림 히스토리 시스템
- 트리거 시스템 (온도, 대기질, 따릉이)
- 공공 데이터 API 연동
- 이벤트 기반 알림 처리

### 개발 예정 🚧
- 실시간 푸시 알림
- 위치 기반 서비스 고도화
- 관리자 대시보드
- 모바일 앱 연동

## 📈 모니터링

애플리케이션 실행 후 다음 엔드포인트에서 시스템 상태를 확인할 수 있습니다:

- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics
- **Scheduled Tasks**: http://localhost:8080/actuator/scheduledtasks

## 🔒 보안

- JWT 기반 인증
- OAuth 2.0 소셜 로그인
- API 요청 제한
- 입력 데이터 검증

> **중요**: 기존 코드에 민감정보가 노출되어 있었습니다. [보안 해결 가이드](docs/security/security-remediation.md)를 참조하여 즉시 조치하세요.

보안 이슈 발견 시 [보안 정책](docs/security/security-policy.md)을 참조하여 신고해주세요.

## 📄 라이선스

이 프로젝트는 [MIT 라이선스](LICENSE) 하에 배포됩니다.

```
MIT License

Copyright (c) 2025 Seoul Fit

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

### 데이터 출처 및 이용 조건

이 프로젝트는 **서울시 공공데이터 포탈**의 데이터를 활용합니다:

- **데이터 제공**: 서울특별시
- **출처**: [서울시 공공데이터 포탈](https://data.seoul.go.kr/)
- **이용 조건**: 서울시 공공데이터 포탈 이용약관 준수
- **출처 표시**: "서울특별시에서 제공하는 데이터를 활용하였습니다"

**사용 데이터**:
- 실시간 대기환경 현황 (대기질 알림)
- 서울자전거 따릉이 실시간 대여정보 (따릉이 현황 알림)
- 문화행사 정보 (문화행사 알림)
- 문화공간 정보 (문화공간 알림)
- 문화행사예약 정보 (문화행사예약 알림)
- 공공도서관 정보 (공공도서관 알림)
- 무더위쉼터 정보 (무더위 쉼터 알림)
- 실시간 인구 데이터 (혼잡도 알림)

자세한 데이터 이용 조건은 [데이터 출처 문서](docs/security/data-attribution.md)를 참조하세요.

**중요**: 이 프로젝트를 사용할 때는 서울시 공공데이터 포탈의 이용약관을 반드시 준수해야 합니다.

## 📚 문서 가이드

### 🚀 빠른 시작
- **[전체 문서 가이드](docs/README.md)** - 모든 문서의 통합 인덱스
- **[API 문서](docs/api/README.md)** - REST API 전체 가이드 및 엔드포인트
- **[개발 가이드](docs/guides/README.md)** - 프로젝트 기여 및 개발 방법

### 🔐 인증 및 보안
- **[OAuth 설정 가이드](docs/guides/oauth/README.md)** - 카카오 OAuth 연동 설정
- **[보안 정책](docs/security/README.md)** - 보안 및 데이터 정책

### ⚡ 핵심 기능
- **[트리거 시스템](docs/guides/triggers/README.md)** - 알림 트리거 개발 및 사용
- **[위치 서비스](docs/api/location.md)** - 위치 기반 서비스 API
- **[알림 시스템](docs/api/notifications.md)** - 실시간 알림 관리

### 🏙️ 서울시 공공데이터 API
- **[대기질 API](docs/api/air-quality.md)** - 실시간 대기환경 정보
- **[문화행사 API](docs/api/cultural-events.md)** - 공연, 전시, 축제 정보
- **[공공시설 API](docs/api/public-facilities.md)** - 도서관, 공원, 체육시설

### 🛠️ 개발 도구
- **[cURL 예제](docs/examples/curl-examples.md)** - 명령줄에서 API 호출 예제
- **[Postman 컬렉션](docs/examples/postman-collection.json)** - API 테스트용 Postman 컬렉션

### 📋 기여 및 정책
- **[기여 가이드](CONTRIBUTING.md)** - 오픈소스 기여 방법
- **[데이터 출처](docs/security/data-attribution.md)** - 서울시 공공데이터 이용 조건
- **[보안 정책](docs/security/security-policy.md)** - 보안 취약점 신고 및 정책

## 🙏 감사의 말

- **서울시**: 공공 데이터 제공
- **Spring Team**: 훌륭한 프레임워크 제공
- **오픈소스 커뮤니티**: 지속적인 기여와 피드백

## 📞 연락처

- **GitHub Issues**: [이슈 등록](https://github.com/seoul-fit/backend/issues)
- **GitHub Discussions**: [토론 참여](https://github.com/seoul-fit/backend/discussions)
- **Email**: gmavsks@gmail.com

---

**함께 만들어가는 스마트 시티! 🌆**

Seoul Fit과 함께 더 나은 도시 생활을 경험하세요.
