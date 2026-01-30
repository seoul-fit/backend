# 환경별 설정 및 실행 가이드

[홈으로 돌아가기](../../README.md) | [전체 문서](../README.md)

> Seoul Fit 백엔드 서비스를 로컬, 개발, 운영 환경에서 실행하는 방법

## 📋 개요

Seoul Fit은 환경변수를 통해 민감정보를 안전하게 관리하며, 각 환경별로 다른 설정을 사용할 수 있습니다.

### 지원 환경
- **로컬 개발 환경** (local)
- **개발 서버 환경** (development) 
- **운영 서버 환경** (production)

## 🔐 보안 원칙

1. **민감정보 분리**: API 키, 패스워드 등을 코드에서 분리
2. **환경별 설정**: 각 환경마다 다른 보안 수준 적용
3. **버전 관리 제외**: .env 파일은 Git에 커밋하지 않음

## 🚀 1. 로컬 개발환경 설정

### 1.1 환경변수 파일 생성

```bash
# .env 파일 생성
cp .env.local.example .env

# 실제 값으로 수정
nano .env  # 또는 원하는 에디터 사용
```

### 1.2 필수 환경변수 설정

`.env` 파일에서 다음 값들을 실제 값으로 변경:

```bash
# 서울시 공공데이터 API 키 (필수)
SEOUL_API_KEY=실제-서울시-API-키

# 카카오 OAuth (필수)
KAKAO_CLIENT_ID=실제-카카오-클라이언트-ID
KAKAO_CLIENT_SECRET=실제-카카오-시크릿

# JWT 비밀키 (필수)
JWT_SECRET=로컬개발용-보안키-최소32글자이상
```

### 1.3 로컬 실행 방법

#### A. IDE에서 실행
```bash
# IntelliJ IDEA / Eclipse
# Run Configuration에서 Environment Variables 설정
# 또는 .env 파일 플러그인 사용
```

#### B. Maven으로 실행
```bash
# .env 파일이 있는 경우 (spring-dotenv 사용)
mvn spring-boot:run

# 직접 환경변수 전달
mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments=\"\
    -DSEOUL_API_KEY=your-key \
    -DKAKAO_CLIENT_ID=your-id \
    -DKAKAO_CLIENT_SECRET=your-secret \
    -DJWT_SECRET=your-jwt-secret\"
```

#### C. Gradle로 실행
```bash
# .env 파일이 있는 경우
./gradlew bootRun

# 직접 환경변수 전달
SEOUL_API_KEY=your-key \
KAKAO_CLIENT_ID=your-id \
KAKAO_CLIENT_SECRET=your-secret \
JWT_SECRET=your-jwt-secret \
./gradlew bootRun
```

#### D. Java 직접 실행
```bash
# JAR 빌드
mvn clean package

# 환경변수와 함께 실행
SEOUL_API_KEY=your-key \
KAKAO_CLIENT_ID=your-id \
KAKAO_CLIENT_SECRET=your-secret \
JWT_SECRET=your-jwt-secret \
java -jar target/seoul-fit-backend-0.0.1-SNAPSHOT.jar
```

## 🔧 2. 개발 서버 환경

### 2.1 환경변수 설정

```bash
# 서버에서 환경변수 설정
export SPRING_PROFILES_ACTIVE=development
export DATABASE_URL=jdbc:postgresql://dev-db:5432/seoul_fit
export DATABASE_USERNAME=seoul_fit_dev
export DATABASE_PASSWORD=dev-strong-password
export SEOUL_API_KEY=dev-seoul-api-key
export KAKAO_CLIENT_ID=dev-kakao-client-id
export KAKAO_CLIENT_SECRET=dev-kakao-secret
export JWT_SECRET=development-jwt-secret-key-256-bits-minimum
export LOG_LEVEL=DEBUG
```

### 2.2 실행 방법

```bash
# 시스템 서비스로 실행
sudo systemctl start seoul-fit-backend

# 또는 직접 실행
java -jar \
  -Dspring.profiles.active=development \
  seoul-fit-backend.jar
```

## 🏭 3. 운영 환경 설정

### 3.1 Docker로 실행

#### Dockerfile 예제
```dockerfile
FROM openjdk:21-jre-slim

COPY target/seoul-fit-backend.jar app.jar

# 환경변수는 외부에서 주입
EXPOSE 8080

ENTRYPOINT [\"java\", \"-jar\", \"/app.jar\"]
```

#### Docker 실행
```bash
# 환경변수 파일 사용
docker run -d \
  --name seoul-fit-backend \
  --env-file .env.production \
  -p 8080:8080 \
  seoul-fit-backend:latest

# 직접 환경변수 전달
docker run -d \
  --name seoul-fit-backend \
  -e SPRING_PROFILES_ACTIVE=production \
  -e DATABASE_URL=jdbc:postgresql://prod-db:5432/seoul_fit \
  -e DATABASE_USERNAME=seoul_fit_prod \
  -e DATABASE_PASSWORD=prod-secure-password \
  -e SEOUL_API_KEY=prod-seoul-api-key \
  -e KAKAO_CLIENT_ID=prod-kakao-client-id \
  -e KAKAO_CLIENT_SECRET=prod-kakao-secret \
  -e JWT_SECRET=production-super-secure-jwt-secret-512-bits \
  -p 8080:8080 \
  seoul-fit-backend:latest
```

### 3.2 Kubernetes로 실행

#### Secret 생성
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: seoul-fit-secrets
type: Opaque
data:
  seoul-api-key: <base64-encoded-key>
  kakao-client-id: <base64-encoded-id>
  kakao-client-secret: <base64-encoded-secret>
  jwt-secret: <base64-encoded-jwt-secret>
  database-password: <base64-encoded-password>
```

#### Deployment 예제
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: seoul-fit-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: seoul-fit-backend
  template:
    metadata:
      labels:
        app: seoul-fit-backend
    spec:
      containers:
      - name: seoul-fit-backend
        image: seoul-fit-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: \"production\"
        - name: DATABASE_URL
          value: \"jdbc:postgresql://postgres-service:5432/seoul_fit\"
        - name: DATABASE_USERNAME
          value: \"seoul_fit_prod\"
        - name: DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: seoul-fit-secrets
              key: database-password
        - name: SEOUL_API_KEY
          valueFrom:
            secretKeyRef:
              name: seoul-fit-secrets
              key: seoul-api-key
        - name: KAKAO_CLIENT_ID
          valueFrom:
            secretKeyRef:
              name: seoul-fit-secrets
              key: kakao-client-id
        - name: KAKAO_CLIENT_SECRET
          valueFrom:
            secretKeyRef:
              name: seoul-fit-secrets
              key: kakao-client-secret
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: seoul-fit-secrets
              key: jwt-secret
```

### 3.3 systemd 서비스로 실행

#### 서비스 파일 생성
```bash
sudo nano /etc/systemd/system/seoul-fit-backend.service
```

```ini
[Unit]
Description=Seoul Fit Backend Service
After=network.target

[Service]
Type=simple
User=seoul-fit
WorkingDirectory=/opt/seoul-fit
ExecStart=/usr/bin/java -jar seoul-fit-backend.jar
Restart=always
RestartSec=10

# 환경변수 설정
Environment=SPRING_PROFILES_ACTIVE=production
Environment=DATABASE_URL=jdbc:postgresql://localhost:5432/seoul_fit
Environment=DATABASE_USERNAME=seoul_fit_prod
Environment=DATABASE_PASSWORD=prod-secure-password
Environment=SEOUL_API_KEY=prod-seoul-api-key
Environment=KAKAO_CLIENT_ID=prod-kakao-client-id
Environment=KAKAO_CLIENT_SECRET=prod-kakao-secret
Environment=JWT_SECRET=production-super-secure-jwt-secret

# 또는 환경변수 파일 사용
# EnvironmentFile=/opt/seoul-fit/.env.production

[Install]
WantedBy=multi-user.target
```

#### 서비스 실행
```bash
# 서비스 등록 및 시작
sudo systemctl daemon-reload
sudo systemctl enable seoul-fit-backend
sudo systemctl start seoul-fit-backend

# 상태 확인
sudo systemctl status seoul-fit-backend

# 로그 확인
sudo journalctl -u seoul-fit-backend -f
```

## 🔍 4. 환경변수 검증

### 4.1 필수 환경변수 체크

애플리케이션 시작 시 다음 환경변수들이 설정되어 있는지 확인:

```bash
# 필수 환경변수 목록
echo \"DATABASE_URL: $DATABASE_URL\"
echo \"SEOUL_API_KEY: ${SEOUL_API_KEY:0:10}...\"  # 일부만 표시
echo \"KAKAO_CLIENT_ID: ${KAKAO_CLIENT_ID:0:10}...\"
echo \"JWT_SECRET: ${JWT_SECRET:+SET}\"  # 설정 여부만 표시
```

### 4.2 설정 확인 엔드포인트

```bash
# 애플리케이션 설정 확인 (민감정보 제외)
curl http://localhost:8080/actuator/info

# 헬스 체크
curl http://localhost:8080/actuator/health
```

## ⚠️ 5. 보안 고려사항

### 5.1 환경변수 보안

1. **파일 권한**: .env 파일은 600 권한으로 설정
   ```bash
   chmod 600 .env
   ```

2. **Git 제외**: .env 파일은 반드시 .gitignore에 추가
   ```bash
   echo \".env*\" >> .gitignore
   ```

3. **로깅 주의**: 환경변수 값이 로그에 출력되지 않도록 주의

### 5.2 운영환경 권장사항

1. **시크릿 관리**: AWS Secrets Manager, HashiCorp Vault 등 사용
2. **정기적 순환**: API 키와 비밀키를 정기적으로 변경
3. **접근 제한**: 환경변수 접근 권한을 최소한으로 제한
4. **모니터링**: 비정상적인 API 사용 패턴 모니터링

## 🆘 6. 문제 해결

### 6.1 자주 발생하는 문제

#### 환경변수 인식 안됨
```bash
# 환경변수 설정 확인
env | grep SEOUL_API_KEY

# Spring Boot 설정 확인
java -jar app.jar --debug
```

#### API 키 오류
```bash
# API 키 유효성 확인
curl \"http://openapi.seoul.go.kr:8088/$SEOUL_API_KEY/json/RealtimeCityAir/1/5/\"
```

#### OAuth 리다이렉트 오류
- 카카오 개발자 콘솔에서 리다이렉트 URI 확인
- HTTPS/HTTP 프로토콜 일치 여부 확인

### 6.2 로그 확인

```bash
# 애플리케이션 로그
tail -f logs/seoul-fit-backend.log

# 시스템 로그 (systemd 사용 시)
sudo journalctl -u seoul-fit-backend -f

# Docker 로그
docker logs -f seoul-fit-backend
```

---

**업데이트**: 2025-08-21  
**버전**: v1.0.0