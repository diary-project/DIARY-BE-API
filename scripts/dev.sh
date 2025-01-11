#!/bin/bash

# 스크립트가 있는 디렉토리의 상위(프로젝트 루트) 디렉토리로 이동
cd "$(dirname "$0")/.." || exit

echo "🚀 Starting application in DEV environment..."

# Gradle 래퍼를 사용하여 스프링 부트 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=dev' 