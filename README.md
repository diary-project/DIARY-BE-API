# Spring Boot Template

Spring Boot 기반의 REST API 프로젝트 템플릿입니다.

## 기능

- Spring Security + JWT 기반 인증
- Swagger UI 3.0 API 문서화
- 글로벌 예외 처리 및 커스텀 예외
- 환경별(local, dev, prod) 설정 분리
- H2 인메모리 데이터베이스 (개발용)
- Git hooks를 통한 코드 품질 관리

## 시작하기

### 방법 1: GitHub Template 사용

1. GitHub에서 "Use this template" 버튼 클릭
2. 새 repository 이름 입력 후 생성
3. 생성된 프로젝트를 로컬로 clone
4. 초기화 스크립트 실행:
   ```bash
   ./scripts/init-project.sh <새로운패키지명> <새로운프로젝트명>
   # 예: ./scripts/init-project.sh com.example myproject
   ```

### 방법 2: 직접 다운로드

1. 이 프로젝트를 ZIP으로 다운로드
2. 원하는 위치에 압축 해제
3. 초기화 스크립트 실행:
   ```bash
   ./scripts/init-project.sh <새로운패키지명> <새로운프로젝트명>
   ```

### Git Hooks 설정

Git hooks 설정:
```bash
./scripts/setup-git-hooks.sh
```

이 설정으로 커밋 전에 자동으로 다음 검사가 실행됩니다:
- 코드 스타일 검사 (Spotless)
- 단위 테스트 실행

## 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── org/daejoeng/
│   │       ├── common/
│   │       │   ├── config/
│   │       │   ├── exception/
│   │       │   ├── response/
│   │       │   └── security/
│   │       └── domain/
│   │           ├── auth/
│   │           └── user/
│   └── resources/
│       ├── application.yml
│       ├── application-local.yml
│       ├── application-dev.yml
│       └── application-prod.yml
```

## 환경 설정

### 프로파일

- `local`: 로컬 개발 환경 (H2 DB, Swagger UI 허용)
- `dev`: 개발 서버 환경
- `prod`: 운영 서버 환경

프로파일 설정:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Security 설정

- JWT 시크릿 키는 환경변수 또는 설정 파일에서 설정
- 각 환경별로 다른 보안 설정 적용 가능

## API 문서

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

## 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details 