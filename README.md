# Diary API

일기 작성 및 관리를 위한 REST API 서비스입니다.

## 기능

- Spring Security + JWT 기반 인증
- Swagger UI 3.0 API 문서화
- 글로벌 예외 처리 및 커스텀 예외
- 환경별(local, dev, prod) 설정 분리
- MySQL RDS 데이터베이스 연동
- AWS 인프라 (EC2, RDS) Terraform 관리
- 자동화된 배포 스크립트

## 시작하기

### 로컬 개발 환경 설정

1. 프로젝트 클론:
   ```bash
   git clone [repository-url]
   cd diary-api
   ```

2. 환경 변수 설정:
   ```bash
   cp .env.example .env
   # .env 파일을 적절히 수정
   ```

3. 애플리케이션 실행:
   ```bash
   ./scripts/local.sh  # 로컬 환경
   ./scripts/dev.sh    # 개발 환경
   ./scripts/prod.sh   # 운영 환경
   ```

### 배포

1. AWS 인프라 생성:
   ```bash
   cd diary-api-infra/terraform
   terraform init
   terraform apply
   ```

2. 애플리케이션 배포:
   ```bash
   ./deploy.sh
   ```

## 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/diary/
│   │       ├── common/
│   │       │   ├── config/
│   │       │   ├── exception/
│   │       │   ├── response/
│   │       │   └── security/
│   │       └── domain/
│   │           ├── auth/
│   │           ├── user/
│   │           └── diary/
│   └── resources/
│       ├── application.yml
│       ├── application-local.yml
│       ├── application-dev.yml
│       └── application-prod.yml
```

## API 문서

- Swagger UI: `http://{server-ip}:8080/swagger-ui.html`
- API Docs: `http://{server-ip}:8080/v3/api-docs`

## 환경 설정

### 프로파일

- `local`: 로컬 개발 환경 (H2 DB, Swagger UI 허용)
- `dev`: 개발 서버 환경 (MySQL RDS, Swagger UI 허용)
- `prod`: 운영 서버 환경 (MySQL RDS, Swagger UI 비활성화)

### 필수 환경 변수

- `JWT_SECRET`: JWT 토큰 암호화 키
- `DB_PASSWORD`: 데이터베이스 비밀번호
- `EC2_IP`: EC2 인스턴스 IP 주소

## 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details 