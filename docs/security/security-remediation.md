# 보안 취약점 해결 가이드

[홈으로 돌아가기](../../README.md) | [보안 정책](../security/README.md)

> Seoul Fit 프로젝트의 노출된 민감정보 처리 및 보안 강화 가이드

## 🚨 긴급 보안 조치 사항

### 발견된 보안 취약점

Seoul Fit 프로젝트에서 다음과 같은 민감정보가 코드에 하드코딩되어 있어 즉시 조치가 필요합니다:

1. **카카오 OAuth 키**: `application.yml`에 노출
2. **서울시 API 키**: `application.yml`에 노출  
3. **Google API 키**: 설정 파일에 노출
4. **데이터베이스 비밀번호**: 설정 파일에 노출

## ⚡ 즉시 조치 사항

### 1. 노출된 API 키 무효화 및 재발급

#### 1.1 카카오 개발자 콘솔
1. [카카오 개발자 콘솔](https://developers.kakao.com/) 접속
2. 해당 앱 선택 → **앱 키** 메뉴
3. **Client Secret 재생성** 클릭
4. 새로운 키를 안전한 곳에 보관

#### 1.2 서울시 공공데이터 포털  
1. [서울시 공공데이터 포털](https://data.seoul.go.kr/) 접속
2. **마이페이지** → **API 키 관리**
3. 기존 키 삭제 후 새 키 발급
4. 새로운 키를 안전한 곳에 보관

#### 1.3 Google Cloud Console
1. [Google Cloud Console](https://console.cloud.google.com/) 접속
2. **API 및 서비스** → **사용자 인증 정보**
3. 노출된 API 키 삭제
4. 새 API 키 생성 및 제한 설정

### 2. Git 히스토리에서 민감정보 제거

```bash
# BFG Repo-Cleaner 사용 (권장)
# https://rtyley.github.io/bfg-repo-cleaner/

# 1. 새 클론 생성
git clone --mirror https://github.com/seoul-fit/backend.git

# 2. 민감정보가 포함된 파일 정리
bfg --replace-text passwords.txt seoul-fit-backend.git

# 3. Git 히스토리 정리
cd seoul-fit-backend.git
git reflog expire --expire=now --all && git gc --prune=now --aggressive

# 4. 강제 푸시 (주의: 협업 시 팀원들과 협의 필요)
git push --force

# 또는 git filter-branch 사용 (더 복잡하지만 정밀한 제어 가능)
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch src/main/resources/application.yml' \
  --prune-empty --tag-name-filter cat -- --all
```

### 3. 협업자 알림

팀원들에게 다음 사항을 알림:

```bash
# 기존 로컬 저장소 백업 후 새로 클론
mv seoul-fit-backend seoul-fit-backend-backup
git clone https://github.com/seoul-fit/backend.git seoul-fit-backend
cd seoul-fit-backend

# 새로운 환경변수 설정
cp .env.local.example .env
# .env 파일에 새로 발급받은 API 키 설정
```

## 🔒 보안 강화 조치

### 1. 환경변수 기반 설정 적용

프로젝트는 이미 환경변수 기반으로 변경되었습니다:

```yaml
# application.yml (보안 강화 완료)
spring:
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET}

seoul-api:
  base-url: 'http://openapi.seoul.go.kr:8088/${SEOUL_API_KEY}/json'

jwt:
  secret: ${JWT_SECRET}
```

### 2. .gitignore 강화

민감정보 파일들이 커밋되지 않도록 설정:

```bash
# .gitignore에 추가됨
### Environment Variables ###
.env
.env.local
.env.development  
.env.production
.env.test

### Application Logs ###
logs/
*.log
```

### 3. 예제 파일 제공

개발자들이 쉽게 설정할 수 있도록 예제 파일 제공:

- `.env.local.example` - 로컬 개발용
- `.env.production.example` - 운영환경용
- `application-example.yml` - 설정 템플릿

## 🛡️ 지속적 보안 모니터링

### 1. Git Hook 설정

커밋 전 민감정보 검사를 위한 pre-commit hook:

```bash
#!/bin/sh
# .git/hooks/pre-commit

# 민감정보 패턴 검사
if git diff --cached --name-only | xargs grep -l -E "(password|secret|key|token).*(=|:).*(\\w{10,})" 2>/dev/null; then
    echo "❌ 민감정보가 포함된 파일이 감지되었습니다!"
    echo "환경변수를 사용하여 민감정보를 외부화하세요."
    exit 1
fi

echo "✅ 보안 검사 통과"
```

### 2. CI/CD 보안 스캔

GitHub Actions를 통한 자동 보안 검사:

```yaml
# .github/workflows/security-scan.yml
name: Security Scan

on: [push, pull_request]

jobs:
  security-scan:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    
    - name: Run secret detection
      uses: trufflesecurity/trufflehog@main
      with:
        path: ./
        base: main
        head: HEAD
```

### 3. 정기적 보안 점검

월 1회 다음 사항들을 점검:

- [ ] API 키 사용량 및 비정상적 접근 패턴
- [ ] 새로운 환경변수 추가 시 보안 검토
- [ ] 의존성 보안 취약점 검사 (`mvn dependency-check:check`)
- [ ] 로그 파일에서 민감정보 노출 여부 확인

## 🚨 사고 대응 절차

### 민감정보 노출 발견 시

1. **즉시 조치**
   - 노출된 키/시크릿 무효화
   - Git 히스토리에서 정보 제거
   - 새로운 키 발급

2. **영향 범위 분석**
   - 노출 기간 확인
   - 접근 로그 분석
   - 피해 범위 평가

3. **복구 작업**
   - 새로운 보안 키 적용
   - 시스템 재배포
   - 관련 서비스 재시작

4. **사후 조치**
   - 보안 정책 강화
   - 모니터링 강화
   - 팀원 교육

## 📞 보안 문의

보안 관련 문의나 취약점 발견 시:

- **이메일**: gmavsks@gmail.com
- **GitHub Security Advisory**: [보안 권고 생성](https://github.com/seoul-fit/backend/security/advisories/new)

---

**중요**: 이 문서의 지침을 즉시 실행하여 보안 취약점을 해결하세요.

**업데이트**: 2025-08-21  
**버전**: v1.0.0