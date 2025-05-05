# BlueBooktle - 온라인 서점 백엔드 API

온라인 도서 판매 플랫폼 'BlueBooktle'(<https://bluebooktle.shop/>)의 백엔드 마이크로서비스 시스템입니다. Spring Boot, Spring Cloud, JPA 기술 스택을
기반으로 MSA(Microservices Architecture) 및 Multi-Module 구조를 사용하여 개발되었습니다.

## 주요 기능 (API 제공 범위)

본 백엔드 시스템은 다음과 같은 핵심 기능들을 위한 REST API를 제공합니다.

* **회원 관리:** 회원 가입, 로그인(Ouat), 정보 수정, 주소 관리, 회원 등급, 휴면/탈퇴 처리
* **인증/인가:** API Gateway를 통한 요청 인증 및 서비스 접근 제어
* **포인트:** 회원 활동(가입, 리뷰, 구매)에 따른 포인트 적립/사용 및 내역 관리
* **도서 관리:** 도서 정보(상세, 카테고리, 태그 등), 재고 관리, 도서 '좋아요' 기능
* **리뷰 관리:** 도서별 리뷰(평점, 내용, 사진) 작성/조회/수정
* **검색:** 도서 정보 통합 검색 (Elasticsearch 기반, 가중치/정렬 적용)
* **쿠폰 관리:** 쿠폰 정책, 종류별(Welcome, 생일 등) 쿠폰 발급/사용/내역 관리
* **장바구니:** 사용자별 장바구니 상품 추가/조회/수정/삭제
* **주문 관리:** 주문 생성, 상태 관리(배송 추적 포함), 반품/취소 처리
* **결제 관리:** 외부 결제 시스템(Toss Payments 등) 연동 및 결제 처리

## 🏗️ 프로젝트 구조 (`bluebooktle-shop` Monorepo)

이 프로젝트는 Maven 멀티 모듈 구조를 따릅니다. 주요 모듈과 역할은 다음과 같습니다.

* **`bluebooktle-shop` (Parent):** 최상위 부모 모듈. 공통 설정, 의존성 버전, 모듈 목록 관리 (`packaging: pom`).
    * **`common-module`**: 여러 모듈에서 공유하는 공통 코드(DTO, Util, Exception 등) 정의. (라이브러리)
    * **`eureka-server`**: 서비스 디스커버리 서버 (Spring Cloud Eureka). 실행 중인 다른 서버(Gateway, Auth, Backend)들의 위치를 등록/관리합니다.
    * **`gateway-server`**: API 게이트웨이 (Spring Cloud Gateway). 시스템의 단일 진입점. 외부(`frontend-server`) 요청을 받아 인증 확인 후(
      `auth-server` 연동) 적절한 내부 서버(`auth-server`, `backend-server`)로 라우팅합니다.
    * **`auth-server`**: 인증/인가 서버. 사용자 로그인(OAuth/JWT 등), 토큰 발급/검증 등 인증/인가 관련 기능을 전담합니다. (JPA, MySQL 사용)
    * **`backend-server`**: **통합 비즈니스 로직 서버.** 인증을 제외한 모든 핵심 백엔드 기능(도서, 회원, 주문, 결제, 쿠폰, 장바구니, 포인트, 리뷰, 검색 등)의 API를
      제공합니다. (JPA, MySQL, Elasticsearch 등 사용)
    * **`coverage-report`**: 테스트 커버리지 리포트 집계용 유틸리티 모듈.

## 🚀 시작하기 (Getting Started)

로컬 개발 환경을 설정하고 프로젝트를 실행하는 방법입니다.

### 사전 요구 사항

* **Java 21 (JDK):** Corretto JDK 또는 Temurin JDK 권장.
* **Maven 3.8+:** 프로젝트 빌드 도구.
* **IntelliJ IDEA:** 권장 IDE (Ultimate Edition 권장).
* **실행 환경 (Local):**
    * **H2 Database:** 기본적으로 로컬 프로파일은 H2 인메모리 DB를 사용합니다. (별도 DB 서버 불필요)
    * **Elasticsearch Server (8.x 권장):** `backend-server`의 검색 기능 실행 시 필요.
    * **Redis Server:** (필요시 - 예: Gateway 에서 Rate Limiting 등 다른 용도)
    * **RabbitMQ Server:** (필요시 - 예: `backend-server` 비동기 처리)
    * *참고: 로컬에서 실제 MySQL을 테스트하고 싶다면, `application-local.yml`의 datasource 설정을 수정하고 로컬 MySQL 서버를 실행해야 합니다.*

### 로컬 개발 환경 설정

1. **저장소 복제 (`bluebooktle-shop`):**
   ```bash
   git clone <bluebooktle-shop 레포지토리 주소>
   cd bluebooktle-shop
   ```
2. **IDE에서 프로젝트 열기:** IntelliJ IDEA에서 `bluebooktle-shop` 디렉토리를 Maven 프로젝트로 엽니다(Open). Maven이 의존성을 다운로드하고 프로젝트 구조를 인식할
   때까지 기다립니다.
3. **(별도) Frontend 저장소 복제:** `bluebooktle-frontend` 레포지토리도 별도로 클론합니다.
4. **로컬 프로파일 확인 (`local`):**
    * 각 실행 모듈(`eureka-server`, `gateway-server`, `auth-server`, `backend-server`)의 `src/main/resources/` 아래
      `application-local.yml` 파일이 로컬 개발용 설정 파일입니다.
    * 이 파일들은 로컬 H2 DB, `localhost` 기준의 서비스 URL(Gateway, Feign 설정) 등을 사용하도록 기본 설정되어 있습니다.
    * **`application-local.yml`은 `.gitignore`에 포함되어 Git 추적에서 제외됩니다.**
5. **Secrets(민감 정보) 설정:**
    * `application-local.yml` 내 민감 정보(예: `DB_PASSWORD_LOCAL`, `JWT_SECRET_KEY_LOCAL`, `TOSS_SECRET_KEY_DEV` 등)는 `${...}`
      플레이스홀더로 되어 있습니다.
    * **IntelliJ IDEA 실행 구성(Run/Debug Configuration)**에서 **환경 변수(Environment Variables)**를 설정하여 이 플레이스홀더에 **로컬 개발용 값**을
      제공해야 합니다.
    * **방법 (IntelliJ):** Run > Edit Configurations... > 해당 Application 선택 > Environment variables 필드에 `변수명1=값1;변수명2=값2`
      형식으로 입력.
        ```
        # 예시: H2 비밀번호가 필요하다면
        DB_PASSWORD_LOCAL=localH2Password;JWT_SECRET_KEY_LOCAL=your-very-secure-local-jwt-secret;TOSS_SECRET_KEY_DEV=sk_test_xxxx;PAYCO_CLIENT_ID_LOCAL=dummyid;PAYCO_CLIENT_SECRET_LOCAL=dummysecret
        ```
    * 팀 내 `DEV_SECRETS_GUIDE.md` (생성 필요) 문서를 참고하여 필요한 변수와 형식을 확인하세요.
6. **데이터베이스 초기화 (로컬 H2 사용 시):**
    * `application-local.yml`에 `ddl-auto: create-drop` 또는 `create`가 설정되어 있으므로, 앱 실행 시 H2 DB 스키마가 자동으로 생성/삭제됩니다. 별도 DDL
      실행 불필요.

### 빌드 및 실행

1. **프로젝트 빌드:**
    * `bluebooktle-shop` 루트 디렉토리에서 전체 백엔드 모듈을 빌드합니다.
   ```bash
   mvn clean install -DskipTests
   ```

2. **애플리케이션 실행 (로컬):**
    * **실행 순서:** Eureka -> Auth -> Backend -> Gateway -> Frontend 순서로 실행하는 것이 안전합니다.
    * **IntelliJ IDEA에서 실행:** 각 실행 모듈(`eureka-server`, `gateway-server`, `auth-server`, `backend-server`)의
      `*Application.java` 에서 `main` 메소드를 실행합니다. **반드시 'local' 프로파일이 활성화되고 필요한 환경 변수가 설정된 실행 구성**을 사용해야 합니다. 할당된 로컬 포트는
      다음과 같습니다.
        * `EurekaServerApplication` (Port: 9761)
        * `AuthServerApplication` (Port: 9001)
        * `BackendServerApplication` (Port: 9002)
        * `GatewayServerApplication` (Port: 9000)
    * **(별도 프로젝트) `bluebooktle-frontend` 실행:** Frontend 프로젝트의 실행 가이드에 따라 Frontend 서버(BFF)를 실행합니다. (Port: 9080)

3. **애플리케이션 접속:**
    * 모든 서버가 정상 실행된 후, 웹 브라우저에서 **`bluebooktle-frontend` 서버 주소 (예: `http://localhost:9080`)** 로 접속하여 서비스를 이용합니다. 백엔드
      API는 주로 API Gateway(로컬 포트: 9000)를 통해 호출됩니다.

## 🏛️ 아키텍처 (Architecture)

* **주요 구성:** API Gateway, Eureka Server, 인증 서버(`auth-server`), 통합 백엔드 서버(`backend-server`)로 구성된 백엔드 시스템과 별도의 프론트엔드 BFF
  서버(`frontend-server`).
* **Spring Cloud:** 시스템 구성 및 연동을 위해 사용됩니다.
    * `Eureka`: Service Discovery
    * `Spring Cloud Gateway`: API Gateway 역할 (라우팅, 인증 연동 필터링 등)
    * `OpenFeign`: 프론트엔드 BFF 서버와 백엔드 내부(Gateway -> Auth/Backend) 간 동기식 REST API 호출에 사용.
* **Maven Multi-module:** `bluebooktle-shop` 백엔드 시스템 내 모듈들을 관리합니다.
* **Layered Architecture:** `auth-server`와 `backend-server` 내부 코드는 역할(Controller, Service, Repository, Entity/DTO) 기반의
  계층형 구조를 따릅니다. `backend-server` 내부는 추가적으로 도메인(`book`, `member` 등)별 패키지로 구성됩니다.
* **데이터 저장소:** MySQL(JPA), Elasticsearch, H2(로컬), Redis(필요시) 등을 사용합니다.
* **통신:**
    * **Frontend -> Backend:** Frontend BFF가 API Gateway를 호출합니다.
    * **Gateway -> Internal:** Gateway는 Eureka를 통해 Auth 서버 또는 Backend 서버를 찾아 라우팅합니다. 요청 전 Auth 서버를 통해 인증 토큰 검증 등을 수행할 수
      있습니다.
    * **Backend -> Auth:** Backend 서버는 필요시 Feign Client를 사용하여 Auth 서버에 사용자 정보 등을 요청할 수 있습니다.
    * **비동기 (필요시):** RabbitMQ 등을 이용한 이벤트 기반 통신을 도입할 수 있습니다.

## 🛠️ 기술 스택 (Technology Stack)

* **Language:** Java 21
* **Framework:** Spring Boot 3.4.4, Spring Cloud 2024.0.1 (⚠️ 버전 호환성 최종 확인 필수)
* **Data:** Spring Data JPA, MySQL 8.x, Elasticsearch 8.x, H2 Database
* **Cache/Queue (필요시):** Redis, RabbitMQ
* **Infra Components:** Spring Cloud Netflix Eureka, Spring Cloud Gateway
* **Communication:** Spring Cloud OpenFeign (HTTP Client)
* **Security:** Spring Security, JWT (Json Web Token), OAuth2 (Client for Payco)
* **Build Tool:** Maven 3.8+
* **Testing:** JUnit 5, Mockito, AssertJ, Spring Boot Test, Spring Security Test
* **Code Coverage:** Jacoco
* **Utilities:** Lombok
* **Code Style:** Naver Code Style
* **Commit Convention:** Conventional Commits
* **CI/CD:** GitHub Actions (JAR 배포 + Shell Script)
* **Collaboration:** Git, GitHub Projects

## 📜 API 문서 (API Documentation)

* API 명세는 추후 **Swagger/SpringDoc**을 적용하여 제공할 예정입니다. ([Swagger UI 링크 - 추가 예정]())
* 또는, 프로젝트 내 `*.http` 파일을 참고할 수 있습니다. ([API Spec 파일 위치 - 추가 예정]())

## 🌳 Git Flow 및 PR 전략 (Git Flow & PR Strategy)

이 프로젝트는 Git Flow를 기반으로 한 브랜치 전략과 Pull Request(PR)를 통한 코드 통합 방식을 따릅니다.

### 브랜치 종류

* `master`: 운영 환경에 배포되는 최종 안정 버전 브랜치. 직접 커밋 금지.
* `develop`: 다음 릴리즈를 위한 개발 내용이 통합되는 브랜치. Feature 브랜치의 주 타겟.
* `feature/<feature-name>`: 새로운 기능 개발 또는 리팩토링을 위한 브랜치. `develop` 브랜치에서 생성. (예: `feature/signup`, `feature/book-details`)
* `hotfix/<issue-id>`: `master` 브랜치에서 발생한 긴급 버그 수정을 위한 브랜치. `master`에서 생성하여 수정 후 `master`과 `develop`에 병합.

### PR 작업 흐름

1. **저장소 Clone 및 업데이트:**
   ```bash
   git clone <bluebooktle-backend 레포지토리 주소>
   cd bluebooktle-backend
   git checkout develop  # 개발 브랜치로 이동
   git pull origin develop # 최신 상태로 업데이트
   ```
2. **기능 브랜치 생성:** `develop` 브랜치 기반으로 새로운 기능 브랜치 생성.
   ```bash
   # 예시: 회원가입 기능 개발
   git checkout -b feature/signup develop
   ```
3. **개발 및 커밋:** 기능 개발 후, 커밋 메시지 컨벤션에 맞춰 커밋.
   ```bash
   git add .
   git commit -m "feat(member): add user signup feature"
   ```
4. **원격 저장소에 푸시 (Rebase 후 푸시):**
    * 다른 사람의 작업 내용을 반영하고 깔끔한 히스토리를 위해 `develop` 브랜치로 Rebase 후 푸시해야 합니다. <-- **중요**
   ```bash
   # develop 브랜치 최신화
   git checkout develop
   git pull origin develop

   # 작업 브랜치로 이동
   git checkout feature/signup

   # 최신 develop 기준으로 rebase
   git rebase develop

   # --- Rebase 중 충돌 발생 시 해결 후 git rebase --continue ---

   # 원격 저장소에 푸시 (rebase 후에는 force-with-lease 권장)
   git push origin feature/signup --force-with-lease
   ```
5. **Pull Request 생성:** GitHub에서 `feature/signup` 브랜치를 `develop` 브랜치로 향하는 PR 생성.
6. **PR 설명 작성:** 변경 목적, 주요 내용, 테스트 방법, 관련 이슈 번호(`Closes #이슈번호`) 등을 상세히 작성. (PR 템플릿 활용 권장)
7. **코드 리뷰 요청:** 팀원들에게 리뷰 요청 (Reviewers 지정).
8. **피드백 반영:** 리뷰 내용을 확인하고 수정 사항 반영 후 다시 커밋/푸시.
9. **PR 병합 (Merge):** 리뷰 승인 및 CI 통과 후 `develop` 브랜치에 병합 (Squash and merge 사용).
10. **브랜치 정리:** 병합 완료 후 로컬 및 원격의 기능 브랜치 삭제.
    ```bash
    # 로컬 브랜치 삭제
    git checkout develop
    git pull origin develop
    git branch -d feature/signup

    # 원격 브랜치 삭제
    git push origin --delete feature/signup
    ```

### 리뷰 가이드라인

* **Reviewers:** 최소 1명 이상의 승인(Approve) 필요.
* **CI Checks:** 모든 자동화된 CI 검사(빌드, 테스트 등) 통과 필요.
* **컨벤션 준수:** 코드 스타일 및 커밋 메시지 컨벤션 준수.

## ✨ 코드 컨벤션 (Code Convention)

* **[NAVER CAMPUS HACKDAY Java 코딩 컨벤션](https://naver.github.io/hackday-conventions-java/)** 을 따릅니다. 자세한 정보는 [문서](https://docs.google.com/document/d/1Spp44rc09BkJpojqlVp9ic_5h9FzlaKH_enXqGSuYGk/edit?usp=sharing)를 참고해주세요.
* **IntelliJ 설정 방법:**
    * `Preferences (Settings)` > `Editor` > `Code Style` > `Java` 로 이동합니다.
    * `Scheme` 옆의 톱니바퀴 아이콘 클릭 > `Import Scheme` > `IntelliJ IDEA code style XML`
      선택 > [naver-intellij-formatter.xml](https://github.com/naver/hackday-conventions-java/blob/master/rule-config/naver-intellij-formatter.xml)
      파일 URL을 사용하거나 다운로드하여 임포트합니다.
    * Scheme 이름(예: `Naver`)을 지정하고 적용합니다.

## 💬 커밋 메시지 컨벤션 (Commit Message Convention)

* **[Conventional Commits](https://www.conventionalcommits.org/)** 명세를 따릅니다.
* **형식:** `type(scope): subject`
    * **`type`**: feat, fix, docs, style, refactor, test, chore 등
    * **`scope` (선택)**: 변경 범위 (예: `gateway`, `member`, `book`, `orderpayment`, `common` 등 모듈 이름)
    * **`subject`**: 변경 내용 요약 (한글로 작성)
* **예시:** `feat(member): 회원가입 api 추가`

## 🔗 기타 문서 링크 (Other Docs)

* **API 명세서:** [Swagger UI]() / [API 문서 경로]() (링크/경로 추가 필요)
* **ERD (개념/논리/물리):** [ERD 이미지 또는 ERDCloud 링크]() (링크 추가 필요)
