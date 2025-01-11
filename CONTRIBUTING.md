# Contributing Guide

## 개발 환경 설정

1. 저장소 클론
```bash
git clone https://github.com/yourusername/spring-template.git
cd spring-template
```

2. 개발 환경 실행
```bash
docker-compose up -d
./gradlew bootRun
```

## 코드 컨벤션

- Google Java Style Guide를 따릅니다
- Spotless를 통한 자동 포맷팅을 사용합니다
- 커밋 전 `./gradlew spotlessApply` 실행을 권장합니다

## Git 워크플로우

1. 이슈 생성
2. 브랜치 생성 (feature/issue-number 또는 fix/issue-number)
3. 개발 완료 후 PR 생성
4. 코드 리뷰 진행
5. main 브랜치에 머지

## 커밋 메시지 규칙

- Conventional Commits를 따릅니다
- 자세한 내용은 .gitmessage 템플릿을 참고해주세요

## PR 가이드라인

- PR 템플릿을 준수해주세요
- 하나의 PR은 하나의 기능 또는 버그 수정을 담당합니다
- PR 생성 전 테스트 코드 작성을 확인해주세요

## 테스트 가이드라인

- 새로운 기능 추가 시 단위 테스트 필수
- 통합 테스트는 필요한 경우에만 작성
- 테스트 커버리지 70% 이상 유지 