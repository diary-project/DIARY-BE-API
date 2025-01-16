#!/bin/bash

# .env 파일이 존재하는지 확인
if [ ! -f .env ]; then
    echo "❌ .env 파일이 없습니다."
    exit 1
fi

# .env 파일에서 환경 변수 로드 (주석과 빈 줄 무시)
set -a
source .env
set +a

# 필수 환경 변수 확인
if [ -z "${EC2_IP}" ] || [ -z "${DB_PASSWORD}" ] || [ -z "${JWT_SECRET}" ]; then
    echo "❌ 필수 환경 변수가 설정되지 않았습니다. .env 파일을 확인해주세요."
    exit 1
fi

# 상수 설정
KEY_PATH="../diary-api-infra/terraform/diary-key.pem"
APP_PATH="/home/ubuntu/diary-api"

echo "🚀 배포를 시작합니다..."

# 1. 프로젝트 빌드
echo "📦 프로젝트 빌드 중..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패"
    exit 1
fi

# 2. JAR 파일 전송
echo "📤 JAR 파일 전송 중..."
scp -i "${KEY_PATH}" build/libs/server-0.0.1-SNAPSHOT.jar "ubuntu@${EC2_IP}:${APP_PATH}/app.jar"

if [ $? -ne 0 ]; then
    echo "❌ 파일 전송 실패"
    exit 1
fi

# 3. 기존 프로세스 정리
echo "🔄 기존 프로세스 정리 중..."
ssh -i "${KEY_PATH}" "ubuntu@${EC2_IP}" bash << 'EOF'
    # Java 프로세스 ID 찾기
    pgrep -f 'java.*app.jar' | while read -r pid; do
        if [ -n "$pid" ]; then
            echo "기존 프로세스(PID: $pid) 종료 중..."
            kill "$pid" || true

            # 프로세스가 완전히 종료될 때까지 대기
            for i in {1..30}; do
                if ! ps -p "$pid" > /dev/null 2>&1; then
                    echo "프로세스(PID: $pid) 종료 완료"
                    break
                fi
                echo "프로세스(PID: $pid) 종료 대기 중..."
                sleep 1
            done

            # 여전히 실행 중이라면 강제 종료
            if ps -p "$pid" > /dev/null 2>&1; then
                echo "프로세스(PID: $pid) 강제 종료"
                kill -9 "$pid" || true
                sleep 2
            fi
        fi
    done

    # 8080 포트 사용 중인지 확인
    lsof -t -i:8080 | while read -r port_pid; do
        if [ -n "$port_pid" ]; then
            echo "8080 포트 프로세스(PID: $port_pid) 종료"
            kill -9 "$port_pid" || true
        fi
    done
    sleep 2
EOF

# 4. 애플리케이션 시작
echo "🚀 애플리케이션 시작 중..."
JWT_ESCAPED=$(printf %q "${JWT_SECRET}")
DB_ESCAPED=$(printf %q "${DB_PASSWORD}")

ssh -i "${KEY_PATH}" "ubuntu@${EC2_IP}" bash << EOF
    cd ${APP_PATH}
    nohup java -jar -Dspring.profiles.active=dev -DJWT_SECRET=${JWT_ESCAPED} -Ddb_password=${DB_ESCAPED} app.jar > app.log 2>&1 &
    echo \$! > app.pid
EOF

if [ $? -ne 0 ]; then
    echo "❌ 애플리케이션 시작 실패"
    exit 1
fi

# 5. 애플리케이션 상태 확인
echo "🔍 애플리케이션 상태 확인 중..."
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    # 헬스체크 엔드포인트 호출
    HEALTH_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://${EC2_IP}/api/health")

    if [ "$HEALTH_STATUS" = "200" ]; then
        echo "✅ 애플리케이션이 정상적으로 구동되었습니다!"
        exit 0
    fi

    echo "애플리케이션 구동 대기 중... (${RETRY_COUNT}/${MAX_RETRIES})"
    RETRY_COUNT=$((RETRY_COUNT + 1))
    sleep 2
done

echo "❌ 애플리케이션 구동 실패: 타임아웃"
echo "로그 확인:"
ssh -i "${KEY_PATH}" "ubuntu@${EC2_IP}" "tail -n 50 ${APP_PATH}/app.log"
exit 1
