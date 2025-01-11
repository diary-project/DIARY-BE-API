#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 함수: 에러 메시지 출력
error() {
    echo -e "${RED}Error: $1${NC}" >&2
    exit 1
}

# 함수: 성공 메시지 출력
success() {
    echo -e "${GREEN}$1${NC}"
}

# 함수: 정보 메시지 출력
info() {
    echo -e "${YELLOW}$1${NC}"
}

# 함수: 파스칼 케이스로 변환
to_pascal_case() {
    echo "$1" | awk -F'[^[:alnum:]]' '{for(i=1;i<=NF;i++)printf "%s", toupper(substr($i,1,1)) substr($i,2)}' 
}

# 필수 인자 체크
if [ "$#" -lt 2 ]; then
    error "Usage: $0 <new-package-name> <new-project-name>"
fi

NEW_PACKAGE_NAME=$1
NEW_PROJECT_NAME=$2
OLD_PACKAGE_NAME="org.daejoeng"
OLD_PROJECT_NAME="spring-template"

# 프로젝트명을 파스칼 케이스로 변환 (예: my-project -> MyProject)
NEW_PROJECT_NAME_PASCAL=$(to_pascal_case "$NEW_PROJECT_NAME")
OLD_PROJECT_NAME_PASCAL="SpringTemplate"

# 현재 디렉토리가 프로젝트 루트인지 확인
if [ ! -f "build.gradle" ]; then
    error "Please run this script from the project root directory"
fi

info "Initializing new project with:"
info "Package name: $NEW_PACKAGE_NAME"
info "Project name: $NEW_PROJECT_NAME"
info "Application name: ${NEW_PROJECT_NAME_PASCAL}Application"

# 1. build.gradle 수정
info "Updating build.gradle..."
sed -i '' "s/rootProject.name = 'template'/rootProject.name = '$NEW_PROJECT_NAME'/g" settings.gradle
sed -i '' "s/group = '$OLD_PACKAGE_NAME'/group = '$NEW_PACKAGE_NAME'/g" build.gradle
sed -i '' "s/mainClass = '$OLD_PACKAGE_NAME.SpringTemplateApplication'/mainClass = '$NEW_PACKAGE_NAME.${NEW_PROJECT_NAME_PASCAL}Application'/g" build.gradle

# 2. 패키지 디렉토리 구조 변경
info "Updating package structure..."
OLD_PACKAGE_PATH="src/main/java/org/daejoeng"
NEW_PACKAGE_PATH="src/main/java/$(echo $NEW_PACKAGE_NAME | tr '.' '/')"

# 새 패키지 디렉토리 생성
mkdir -p $NEW_PACKAGE_PATH

# 기존 파일들을 새 패키지로 이동
cp -r $OLD_PACKAGE_PATH/* $NEW_PACKAGE_PATH/

# 3. 패키지명 변경
info "Updating package names in source files..."
find $NEW_PACKAGE_PATH -type f -name "*.java" -exec sed -i '' "s/package $OLD_PACKAGE_NAME/package $NEW_PACKAGE_NAME/g" {} +
find $NEW_PACKAGE_PATH -type f -name "*.java" -exec sed -i '' "s/import $OLD_PACKAGE_NAME/import $NEW_PACKAGE_NAME/g" {} +

# 테스트 파일도 함께 업데이트
info "Updating test files..."
TEST_PACKAGE_PATH="src/test/java/$(echo $NEW_PACKAGE_NAME | tr '.' '/')"
mkdir -p $TEST_PACKAGE_PATH
if [ -f "src/test/java/org/daejoeng/SpringTemplateApplicationTests.java" ]; then
    mv "src/test/java/org/daejoeng/SpringTemplateApplicationTests.java" "$TEST_PACKAGE_PATH/${NEW_PROJECT_NAME_PASCAL}ApplicationTests.java"
    sed -i '' "s/package org.daejoeng/package $NEW_PACKAGE_NAME/g" "$TEST_PACKAGE_PATH/${NEW_PROJECT_NAME_PASCAL}ApplicationTests.java"
    sed -i '' "s/SpringTemplateApplicationTests/${NEW_PROJECT_NAME_PASCAL}ApplicationTests/g" "$TEST_PACKAGE_PATH/${NEW_PROJECT_NAME_PASCAL}ApplicationTests.java"
fi

# Spotless 적용
info "Applying Spotless formatting..."
./gradlew spotlessApply

# 4. Application 클래스 이름 변경
info "Updating application class name..."
mv "$NEW_PACKAGE_PATH/SpringTemplateApplication.java" "$NEW_PACKAGE_PATH/${NEW_PROJECT_NAME_PASCAL}Application.java"
sed -i '' "s/SpringTemplateApplication/${NEW_PROJECT_NAME_PASCAL}Application/g" "$NEW_PACKAGE_PATH/${NEW_PROJECT_NAME_PASCAL}Application.java"

# 5. 기존 패키지 디렉토리 삭제
rm -rf src/main/java/org

# application.yml 파일 처리
info "Setting up configuration files..."
# application.yml을 example로 복사
if [ -f "src/main/resources/application.yml" ]; then
    cp "src/main/resources/application.yml" "src/main/resources/application.yml.example"
fi

# .gitignore에 모든 실제 설정 파일 추가
echo "# Application properties" >> .gitignore
echo "src/main/resources/application.yml" >> .gitignore
echo "src/main/resources/application-*.yml" >> .gitignore

# 6. Git 초기화
info "Initializing new Git repository..."
rm -rf .git
git init

# 7. Git hooks 설정
info "Setting up Git hooks..."
chmod +x scripts/setup-git-hooks.sh
./scripts/setup-git-hooks.sh

success "Project initialization completed successfully!"
success "New project '$NEW_PROJECT_NAME' is ready to use." 