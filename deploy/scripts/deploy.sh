#!/bin/bash

# Seoul Fit 배포 스크립트
# 사용법: ./deploy.sh [환경] [배포방식]
# 예시: ./deploy.sh prod docker

set -e  # 에러 발생시 즉시 중단

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 환경 변수 설정
ENV=${1:-dev}
DEPLOY_TYPE=${2:-docker}
APP_NAME="seoulfit"
VERSION=$(git describe --tags --always)

echo -e "${GREEN}🚀 Seoul Fit 배포 시작${NC}"
echo "환경: $ENV"
echo "배포 방식: $DEPLOY_TYPE"
echo "버전: $VERSION"

# 환경별 설정 로드
load_environment() {
    local env_file=".env.$ENV"
    
    if [ ! -f "$env_file" ]; then
        echo -e "${RED}❌ 환경 파일을 찾을 수 없습니다: $env_file${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ 환경 변수 로드: $env_file${NC}"
    
    # .env 파일을 시스템 환경변수로 export
    set -a
    source "$env_file"
    set +a
}

# Gradle 빌드
build_application() {
    echo -e "${YELLOW}📦 애플리케이션 빌드 중...${NC}"
    ./gradlew clean build -x test
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 빌드 성공${NC}"
    else
        echo -e "${RED}❌ 빌드 실패${NC}"
        exit 1
    fi
}

# Docker 배포
deploy_docker() {
    echo -e "${YELLOW}🐳 Docker 배포 시작...${NC}"
    
    # Docker 이미지 빌드
    docker build -t $APP_NAME:$VERSION .
    docker tag $APP_NAME:$VERSION $APP_NAME:latest
    
    # 기존 컨테이너 중지 및 제거
    docker-compose -f deploy/docker-compose.yml down
    
    # 환경변수 파일 복사
    cp .env.$ENV deploy/.env.prod
    
    # 새 컨테이너 시작
    cd deploy
    docker-compose up -d
    cd ..
    
    echo -e "${GREEN}✅ Docker 배포 완료${NC}"
}

# JAR 직접 실행 배포
deploy_jar() {
    echo -e "${YELLOW}☕ JAR 배포 시작...${NC}"
    
    JAR_FILE="build/libs/backend-1.0.0.jar"
    
    if [ ! -f "$JAR_FILE" ]; then
        echo -e "${RED}❌ JAR 파일을 찾을 수 없습니다: $JAR_FILE${NC}"
        exit 1
    fi
    
    # 기존 프로세스 종료
    PID=$(pgrep -f $APP_NAME || true)
    if [ ! -z "$PID" ]; then
        echo "기존 프로세스 종료: $PID"
        kill $PID
        sleep 5
    fi
    
    # 새 프로세스 시작 (백그라운드)
    nohup java -jar \
        -Dspring.profiles.active=$ENV \
        -Xmx512m -Xms256m \
        $JAR_FILE > logs/app.log 2>&1 &
    
    echo $! > app.pid
    echo -e "${GREEN}✅ JAR 배포 완료 (PID: $(cat app.pid))${NC}"
}

# Kubernetes 배포
deploy_k8s() {
    echo -e "${YELLOW}☸️  Kubernetes 배포 시작...${NC}"
    
    # Docker 이미지 빌드 및 푸시 (레지스트리 필요)
    docker build -t $APP_NAME:$VERSION .
    
    # 실제 운영에서는 레지스트리에 푸시
    # docker tag $APP_NAME:$VERSION your-registry.com/$APP_NAME:$VERSION
    # docker push your-registry.com/$APP_NAME:$VERSION
    
    # Secret 생성/업데이트
    kubectl create secret generic seoulfit-secrets \
        --from-env-file=.env.$ENV \
        --dry-run=client -o yaml | kubectl apply -f -
    
    # 배포
    kubectl apply -f deploy/k8s/deployment.yaml
    kubectl set image deployment/seoulfit seoulfit=$APP_NAME:$VERSION
    kubectl rollout status deployment/seoulfit
    
    echo -e "${GREEN}✅ Kubernetes 배포 완료${NC}"
}

# AWS ECS 배포
deploy_ecs() {
    echo -e "${YELLOW}☁️  AWS ECS 배포 시작...${NC}"
    
    # ECR에 이미지 푸시
    aws ecr get-login-password --region ap-northeast-2 | \
        docker login --username AWS --password-stdin $ECR_REPOSITORY
    
    docker build -t $APP_NAME:$VERSION .
    docker tag $APP_NAME:$VERSION $ECR_REPOSITORY/$APP_NAME:$VERSION
    docker push $ECR_REPOSITORY/$APP_NAME:$VERSION
    
    # Task Definition 업데이트
    aws ecs register-task-definition \
        --cli-input-json file://deploy/ecs/task-definition.json \
        --region ap-northeast-2
    
    # 서비스 업데이트
    aws ecs update-service \
        --cluster seoulfit-cluster \
        --service seoulfit-service \
        --force-new-deployment \
        --region ap-northeast-2
    
    echo -e "${GREEN}✅ ECS 배포 완료${NC}"
}

# 시스템드 서비스 배포 (Linux)
deploy_systemd() {
    echo -e "${YELLOW}🐧 Systemd 서비스 배포 시작...${NC}"
    
    # 서비스 파일 생성
    sudo tee /etc/systemd/system/seoulfit.service > /dev/null <<EOF
[Unit]
Description=UrbanPing Application
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/seoulfit
EnvironmentFile=/opt/seoulfit/.env.$ENV
ExecStart=/usr/bin/java -jar /opt/seoulfit/backend-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
    
    # JAR 파일 복사
    sudo cp build/libs/backend-1.0.0.jar /opt/seoulfit/
    sudo cp .env.$ENV /opt/seoulfit/
    
    # 서비스 재시작
    sudo systemctl daemon-reload
    sudo systemctl restart seoulfit
    sudo systemctl enable seoulfit
    
    echo -e "${GREEN}✅ Systemd 서비스 배포 완료${NC}"
}

# 헬스 체크
health_check() {
    echo -e "${YELLOW}🏥 헬스 체크 중...${NC}"
    
    sleep 10  # 애플리케이션 시작 대기
    
    for i in {1..30}; do
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo -e "${GREEN}✅ 애플리케이션이 정상적으로 실행 중입니다${NC}"
            return 0
        fi
        echo "대기 중... ($i/30)"
        sleep 2
    done
    
    echo -e "${RED}❌ 헬스 체크 실패${NC}"
    return 1
}

# 메인 실행 흐름
main() {
    load_environment
    build_application
    
    case $DEPLOY_TYPE in
        docker)
            deploy_docker
            ;;
        jar)
            deploy_jar
            ;;
        k8s|kubernetes)
            deploy_k8s
            ;;
        ecs)
            deploy_ecs
            ;;
        systemd)
            deploy_systemd
            ;;
        *)
            echo -e "${RED}❌ 지원하지 않는 배포 방식: $DEPLOY_TYPE${NC}"
            echo "사용 가능한 옵션: docker, jar, k8s, ecs, systemd"
            exit 1
            ;;
    esac
    
    health_check
    
    echo -e "${GREEN}🎉 배포가 성공적으로 완료되었습니다!${NC}"
    echo "애플리케이션 URL: http://localhost:8080"
}

# 스크립트 실행
main