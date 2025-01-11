#!/bin/bash

# 스크립트가 있는 디렉토리의 상위(프로젝트 루트) 디렉토리로 이동
cd "$(dirname "$0")/.." || exit

echo "⚠️ Starting application in PRODUCTION environment..."
echo "Are you sure you want to start in PRODUCTION mode? (y/n)"
read -r response

if [[ "$response" =~ ^([yY][eE][sS]|[yY])+$ ]]; then
    echo "🚀 Starting application in PRODUCTION environment..."
    
    # Gradle을 사용하여 프로덕션용 JAR 파일 빌드
    ./gradlew clean build -x test
    
    # JAR 파일 실행
    java -jar \
        -Dspring.profiles.active=prod \
        build/libs/*.jar
else
    echo "❌ Startup cancelled"
    exit 1
fi 