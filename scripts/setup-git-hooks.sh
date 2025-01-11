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

# 현재 디렉토리가 프로젝트 루트인지 확인
if [ ! -f "build.gradle" ]; then
    error "Please run this script from the project root directory"
fi

# Git hooks 디렉토리 생성
mkdir -p .git/hooks

# pre-commit hook 생성
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash

echo "Running pre-commit checks..."

# Stash any changes not in staging area
git stash -q --keep-index

# Run Spotless check
echo "Running Spotless check..."
./gradlew spotlessCheck
SPOTLESS_RESULT=$?

# Run tests
echo "Running tests..."
./gradlew test
TEST_RESULT=$?

# Restore stashed changes
git stash pop -q

# Check results
if [ $SPOTLESS_RESULT -ne 0 ]; then
    echo "❌ Spotless check failed. Please run './gradlew spotlessApply' and commit again."
    exit 1
fi

if [ $TEST_RESULT -ne 0 ]; then
    echo "❌ Tests failed. Please fix the tests and commit again."
    exit 1
fi

echo "✅ All checks passed!"
exit 0
EOF

# pre-commit hook에 실행 권한 부여
chmod +x .git/hooks/pre-commit

# Spotless 적용
info "Running initial Spotless apply..."
./gradlew spotlessApply || error "Failed to apply Spotless"

success "Git hooks setup completed successfully!"
success "Pre-commit hook is now installed and configured." 