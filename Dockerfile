# 멀티 스테이지 빌드를 사용하여 이미지 크기 최적화
# Stage 1: 빌드 단계
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Gradle 캐시를 활용하기 위해 의존성 파일 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# 의존성 다운로드 (캐시 활용)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: 실행 단계
FROM eclipse-temurin:21-jre-alpine

# 보안을 위한 non-root 사용자 생성
RUN addgroup -g 1000 appgroup && \
    adduser -D -u 1000 -G appgroup appuser

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/backend-1.0.0.jar app.jar

# 로그 디렉토리 생성
RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

# non-root 사용자로 전환
USER appuser

# 헬스체크 추가
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 애플리케이션 포트
EXPOSE 8080

# JVM 옵션을 환경변수로 설정 가능
ENV JVM_OPTS="-Xmx512m -Xms256m"

# 실행 (환경변수는 docker run 또는 docker-compose에서 주입)
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]