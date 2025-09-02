#!/bin/bash

# Seoul Fit Backend - Optimized JVM Startup Script
# 성능 최적화된 JVM 옵션으로 애플리케이션 시작

# 환경 변수 설정
export SPRING_PROFILES_ACTIVE=production,performance
export SERVER_PORT=8080

# 애플리케이션 JAR 파일 경로
APP_JAR="build/libs/backend-1.0.0.jar"

# JVM 메모리 설정
HEAP_SIZE="2g"
META_SIZE="256m"
DIRECT_MEM_SIZE="512m"

# GC 로그 디렉토리
GC_LOG_DIR="logs/gc"
mkdir -p $GC_LOG_DIR

# 날짜 포맷
DATE=$(date +%Y%m%d_%H%M%S)

# JVM 옵션
JVM_OPTS=""

# 1. 힙 메모리 설정
JVM_OPTS="$JVM_OPTS -Xms$HEAP_SIZE"    # 초기 힙 크기
JVM_OPTS="$JVM_OPTS -Xmx$HEAP_SIZE"    # 최대 힙 크기
JVM_OPTS="$JVM_OPTS -XX:MetaspaceSize=$META_SIZE"
JVM_OPTS="$JVM_OPTS -XX:MaxMetaspaceSize=$META_SIZE"
JVM_OPTS="$JVM_OPTS -XX:MaxDirectMemorySize=$DIRECT_MEM_SIZE"

# 2. G1GC 설정 (Java 21 최적화)
JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=200"
JVM_OPTS="$JVM_OPTS -XX:G1HeapRegionSize=16m"
JVM_OPTS="$JVM_OPTS -XX:+ParallelRefProcEnabled"
JVM_OPTS="$JVM_OPTS -XX:+UnlockExperimentalVMOptions"
JVM_OPTS="$JVM_OPTS -XX:+AlwaysPreTouch"
JVM_OPTS="$JVM_OPTS -XX:InitiatingHeapOccupancyPercent=45"

# 3. GC 로깅
JVM_OPTS="$JVM_OPTS -Xlog:gc*:file=$GC_LOG_DIR/gc_$DATE.log:time,uptime,level,tags:filecount=10,filesize=50M"
JVM_OPTS="$JVM_OPTS -XX:+PrintGCDetails"
JVM_OPTS="$JVM_OPTS -XX:+PrintGCDateStamps"

# 4. JIT 컴파일러 최적화
JVM_OPTS="$JVM_OPTS -XX:+TieredCompilation"
JVM_OPTS="$JVM_OPTS -XX:TieredStopAtLevel=1"  # 빠른 시작
JVM_OPTS="$JVM_OPTS -XX:ReservedCodeCacheSize=256m"
JVM_OPTS="$JVM_OPTS -XX:InitialCodeCacheSize=64m"

# 5. 문자열 최적화
JVM_OPTS="$JVM_OPTS -XX:+UseStringDeduplication"
JVM_OPTS="$JVM_OPTS -XX:+OptimizeStringConcat"
JVM_OPTS="$JVM_OPTS -XX:+UseCompressedOops"
JVM_OPTS="$JVM_OPTS -XX:+UseCompressedClassPointers"

# 6. 네트워크 최적화
JVM_OPTS="$JVM_OPTS -Djava.net.preferIPv4Stack=true"
JVM_OPTS="$JVM_OPTS -Dsun.net.inetaddr.ttl=60"
JVM_OPTS="$JVM_OPTS -Dsun.net.inetaddr.negative.ttl=10"

# 7. 파일 인코딩
JVM_OPTS="$JVM_OPTS -Dfile.encoding=UTF-8"
JVM_OPTS="$JVM_OPTS -Duser.timezone=Asia/Seoul"

# 8. 보안 랜덤 생성기 (시작 속도 개선)
JVM_OPTS="$JVM_OPTS -Djava.security.egd=file:/dev/./urandom"

# 9. Spring Boot 최적화
JVM_OPTS="$JVM_OPTS -Dspring.jmx.enabled=true"
JVM_OPTS="$JVM_OPTS -Dspring.config.location=classpath:/application.yml,classpath:/application-production.yml,classpath:/application-performance.yml"
JVM_OPTS="$JVM_OPTS -Dspring.backgroundpreinitializer.ignore=true"

# 10. 모니터링 및 관리
JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote"
JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.port=9090"
JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
JVM_OPTS="$JVM_OPTS -Dcom.sun.management.jmxremote.ssl=false"

# 11. 덤프 설정 (OOM 발생 시)
JVM_OPTS="$JVM_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="$JVM_OPTS -XX:HeapDumpPath=logs/heapdump_$DATE.hprof"
JVM_OPTS="$JVM_OPTS -XX:OnOutOfMemoryError='kill -9 %p'"

# 12. 에러 파일
JVM_OPTS="$JVM_OPTS -XX:ErrorFile=logs/hs_err_$DATE.log"

# 13. Flight Recorder (성능 분석용)
JVM_OPTS="$JVM_OPTS -XX:StartFlightRecording=settings=profile,filename=logs/recording_$DATE.jfr,dumponexit=true,maxsize=100m"

echo "========================================="
echo "Starting Seoul Fit Backend"
echo "========================================="
echo "Profile: $SPRING_PROFILES_ACTIVE"
echo "Port: $SERVER_PORT"
echo "Heap Size: $HEAP_SIZE"
echo "Metaspace: $META_SIZE"
echo "GC: G1GC"
echo "========================================="

# 애플리케이션 시작
java $JVM_OPTS -jar $APP_JAR

# 종료 코드 확인
EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
    echo "Application exited with code $EXIT_CODE"
    exit $EXIT_CODE
fi