# 🌳 서울시 공원 정보 시스템

## 📋 개요

서울시 공공 데이터 API를 활용하여 서울시 주요 공원 현황 정보를 수집하고 관리하는 시스템입니다.

## 🔗 API 정보

- **API URL**: https://data.seoul.go.kr/dataList/OA-394/S/1/datasetView.do
- **서비스명**: SearchParkInfoService
- **데이터 형식**: JSON

## 🏗️ 아키텍처

헥사고날 아키텍처(Hexagonal Architecture)를 준수하여 구현되었습니다.

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Controllers   │  │   Batch Jobs    │                  │
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
│  │   Entities      │  │   Repositories  │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                 Infrastructure Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │  JPA Repository │  │  External APIs  │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 주요 기능

### 1. 데이터 수집 및 저장
- 서울시 공원 정보 API 호출
- 데이터베이스에 공원 정보 저장
- 일일 배치 작업으로 자동 업데이트

### 2. 데이터 관리
- 3일치 데이터 보관 정책
- 날짜별 데이터 버전 관리
- 자동 데이터 정리

### 3. 배치 처리
- 하루 한 번 자동 실행
- 날짜 기반 데이터 저장 (YYYYMMDD 형식)
- 실패 시 재시도 로직

## 📊 데이터 구조

### Request Parameters
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| KEY | String | 필수 | OpenAPI 인증키 |
| TYPE | String | 필수 | 요청파일타입 (json) |
| SERVICE | String | 필수 | 서비스명 (SearchParkInfoService) |
| START_INDEX | Integer | 필수 | 요청시작위치 |
| END_INDEX | Integer | 필수 | 요청종료위치 |
| P_IDX | Number | 선택 | 연번 |

### Response Fields
| 필드명 | 설명 |
|--------|------|
| P_IDX | 연번(공원번호) |
| P_PARK | 공원명 |
| P_LIST_CONTENT | 공원개요 |
| AREA | 면적 |
| OPEN_DT | 개원일 |
| MAIN_EQUIP | 주요시설 |
| MAIN_PLANTS | 주요식물 |
| GUIDANCE | 안내도 |
| VISIT_ROAD | 오시는길 |
| USE_REFER | 이용시참고사항 |
| P_IMG | 이미지 |
| P_ZONE | 지역 |
| P_ADDR | 공원주소 |
| P_NAME | 관리부서 |
| P_ADMINTEL | 전화번호 |
| G_LONGITUDE | X좌표(GRS80TM) |
| G_LATITUDE | Y좌표(GRS80TM) |
| LONGITUDE | X좌표(WGS84) |
| LATITUDE | Y좌표(WGS84) |
| TEMPLATE_URL | 바로가기 |

## 🔧 설정

### application.yml 설정
```yaml
seoul-api:
  base-url: 'http://openapi.seoul.go.kr:8088/666e634468776c7339314668766844/json'
  park:
    service-name: SearchParkInfoService
    batch-size: 100
    retention-days: 3

urbanping:
  scheduler:
    park-batch-cron: "0 0 2 * * ?" # 매일 새벽 2시 실행
```

## 📁 패키지 구조

```
com.seoulfit.backend.park/
├── adapter/
│   ├── in/
│   │   └── web/           # REST Controllers
│   └── out/
│       ├── api/           # External API Adapters
│       └── persistence/   # JPA Repository Adapters
├── application/
│   ├── port/
│   │   ├── in/           # Use Case Interfaces
│   │   └── out/          # Repository Interfaces
│   └── service/          # Application Services
├── domain/               # Domain Entities
└── infrastructure/
    └── batch/           # Batch Job Configurations
```

## 🔄 배치 작업 흐름

1. **스케줄러 실행** (매일 새벽 2시)
2. **API 호출** (서울시 공원 정보 조회)
3. **데이터 변환** (DTO → Entity)
4. **데이터 저장** (날짜별 저장)
5. **이전 데이터 정리** (3일 이전 데이터 삭제)
6. **결과 로깅**

## 🧪 테스트

### 단위 테스트
- Domain Entity 테스트
- Application Service 테스트
- Repository 테스트

### 통합 테스트
- API 호출 테스트
- 배치 작업 테스트
- 데이터베이스 연동 테스트

## 📈 모니터링

- 배치 작업 실행 상태
- API 호출 성공/실패 로그
- 데이터 저장 통계
- 에러 알림

## 🚨 에러 처리

- API 호출 실패 시 재시도
- 데이터 파싱 오류 처리
- 데이터베이스 연결 오류 처리
- 배치 작업 실패 알림
