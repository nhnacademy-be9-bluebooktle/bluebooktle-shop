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

## 🏗️ 프로젝트 구조 (`bluebooktle-backend` Monorepo)

이 프로젝트는 Maven 멀티 모듈 구조를 따릅니다. 각 모듈은 특정 책임 또는 도메인을 가집니다.

* **`bluebooktle-backend` (Parent):** 최상위 부모 모듈. 공통 설정, 의존성 버전, 모듈 목록 관리 (`packaging: pom`).
    * **`eureka-server`**: 서비스 디스커버리 서버 (Spring Cloud Eureka). 다른 서비스들의 위치 정보를 등록하고 조회할 수 있게 합니다.
    * **`gateway-server`**: API 게이트웨이 (Spring Cloud Gateway). 시스템의 단일 진입점으로, 요청 라우팅, 인증/인가 필터링 등을 수행합니다.
    * **`common-module`**: 공통 코드 모듈. 여러 모듈에서 공유하는 DTO, 유틸리티, 공통 예외 등을 정의합니다. (라이브러리 역할)
    * **`member-service`**: 회원, 인증 지원, 포인트 관리 담당 REST API 서버. (JPA, MySQL 사용)
    * **`book-service`**: 도서, 리뷰 관리 및 검색 기능 제공 REST API 서버. (JPA, MySQL, Elasticsearch 사용)
    * **`coupon-service`**: 쿠폰 정책 및 기능 제공 REST API 서버. (JPA, MySQL 사용)
    * **`order-payment-service`**: 주문 생성/관리 및 결제 연동 처리 REST API 서버. (JPA, MySQL 사용, 필요시 RabbitMQ 연동)
    * **`cart-service`**: 장바구니 관리 REST API 서버. (JPA, MySQL 사용 - DB 기반)
    * **`coverage-report`**: 테스트 커버리지 리포트 집계용 모듈.

## 🚀 시작하기 (Getting Started)

개발 환경을 설정하고 프로젝트를 실행하는 방법입니다.

### 사전 요구 사항

* **Java 21 (JDK):** Corretto JDK 또는 Temurin JDK 권장.
* **Maven 3.8+:** 프로젝트 빌드 도구.
* **IntelliJ IDEA:** 권장 IDE.
* **실행 환경:**
    * MySQL Server (8.x 권장)
    * Redis Server (인증 세션)
    * Elasticsearch Server (8.x 권장 - `book-service` 실행 시 필요)
    * RabbitMQ Server (필요시 - `order-payment-service` 등에서 사용 시)

### 로컬 개발 환경 설정

1. **저장소 복제:**
   ```bash
   git clone <bluebooktle-backend 레포지토리 주소>
   cd bluebooktle-backend
   ```

2. **IDE에서 프로젝트 열기:** IntelliJ IDEA에서 `bluebooktle-backend` 디렉토리를 Maven 프로젝트로 엽니다.

3. **로컬 환경 설정 파일 확인:**
    * 각 서비스 모듈의 `src/main/resources/` 아래에는 `application.yml` (공통), `application-local.yml`, `application-dev.yml`,
      `application-prod.yml` 파일이 있습니다.
    * 로컬 개발 시에는 `local` 프로파일을 사용합니다 (`spring.profiles.active=local`).

4. **Secrets(민감 정보) 설정:**
    * `application-{profile}.yml` 파일 내 DB 비밀번호, 외부 API 키 등 민감 정보는 `${...}` 플레이스홀더로 되어 있습니다. (예:
      `password: ${DB_PASSWORD}`)
    * **이 플레이스홀더에 실제 값을 제공해야 합니다.** 가장 권장되는 방법은 **IntelliJ IDEA의 실행 구성(Run/Debug Configuration)**에서 **환경 변수(Environment
      Variables)**를 설정하는 것입니다.
    * **방법 (IntelliJ):**
        1. Run > Edit Configurations... 선택
        2. 실행할 서비스(예: `MemberServiceApplication`) 선택
        3. 'Environment variables' 필드에 필요한 환경 변수와 로컬 개발용 값을 입력합니다. (세미콜론 `;` 으로 구분 또는 각 줄에 입력)
           ```
           DB_PASSWORD=myLocalDbPassword;TOSS_SECRET_KEY_DEV=sk_dev_xxxxxx;
           ```

5. **기타 인프라 설정:**
    * Elasticsearch, RabbitMQ 등을 로컬에서 사용하는 경우, 관련 접속 정보를 `application-local.yml`에 설정하거나 환경 변수로 제공합니다.

### 빌드 및 실행

1. **프로젝트 빌드:**
    * 프로젝트 루트 디렉토리(`bluebooktle-backend`)에서 Maven을 사용하여 전체 프로젝트를 빌드합니다.
   ```bash
   # 의존성 다운로드 및 컴파일, 테스트 실행, 패키징
   mvn clean install
   # 테스트 스킵하고 빌드만 하려면
   # mvn clean install -DskipTests
   ```

2. **애플리케이션 실행 (로컬):**
    * MSA는 여러 서비스가 함께 실행되어야 제대로 동작합니다. **실행 순서**가 중요할 수 있습니다.
        * **1순위:** `eureka-server`
        * **2순위 (선택):** `config-service` (현재는 미사용)
        * **3순위:** 핵심 서비스들 (`member-service`, `book-service`, `coupon-service`, `order-payment-service`,
          `cart-service`) - 서로 의존성이 적다면 병렬 실행 가능.
        * **4순위:** `gateway-server` (다른 서비스들이 Eureka에 등록된 후 실행 권장)
    * **IntelliJ IDEA에서 실행:** 각 모듈의 `*Application.java` 파일을 열고 `main` 메소드를 실행합니다. 이때 **반드시 'local' 프로파일이 활성화**되고 **필요한
      환경 변수가 설정된 실행 구성**을 사용해야 합니다.


3. **애플리케이션 접속:**
    * 모든 백엔드 서비스가 정상적으로 실행된 후, 별도로 실행된 **`bluebooktle-frontend` (프론트엔드 BFF 서버)**의 주소로 웹 브라우저를 통해 접속하여 기능을 확인합니다. 백엔드
      API는 주로 API Gateway를 통해 호출됩니다.

## 🏛️ 아키텍처 (Architecture)

* **MSA (Microservices Architecture):** 각 주요 도메인(회원, 도서, 주문 등)을 독립적인 마이크로서비스로 분리하여 개발, 배포, 확장의 유연성을 높입니다.
* **Spring Cloud:** MSA 구축을 위한 주요 기술로 사용됩니다.
    * `Eureka`: Service Discovery (서비스 등록 및 검색)
    * `Spring Cloud Gateway`: API Gateway (단일 진입점, 라우팅, 필터링)
    * `OpenFeign`: 서비스 간 동기식 REST API 호출을 위한 선언적 HTTP 클라이언트
* **Maven Multi-module:** Monorepo 내에서 각 마이크로서비스 및 공통 모듈을 관리합니다.
* **Layered Architecture:** 각 마이크로서비스 내부는 일반적으로 다음과 같은 계층형 구조를 따릅니다.
    * **Controller:** HTTP 요청 처리, 입력값 검증, Service 계층 호출
    * **Service:** 비즈니스 로직 수행, 트랜잭션 관리, Repository 계층 호출
    * **Repository:** 데이터 영속성 처리 (DB 접근), Spring Data JPA 활용
    * **Entity/Model:** 데이터베이스 테이블과 매핑되는 객체 (JPA Entity), DTO (Data Transfer Object)
* **데이터 저장소:** 각 서비스는 필요에 따라 MySQL(JPA), Elasticsearch, Redis 등 적합한 데이터 저장소를 사용합니다.
* **통신:**
    * **동기:** 서비스 간 직접 호출이 필요한 경우 Feign Client를 사용합니다. 모든 외부 요청은 API Gateway를 통합니다.
    * **비동기 (필요시):** 서비스 간 결합도를 낮추거나 특정 작업(예: 포인트 적립, 쿠폰 발급)을 비동기적으로 처리해야 할 때 RabbitMQ 같은 메시지 큐 사용을 고려할 수 있습니다.

## 🛠️ 기술 스택 (Technology Stack)

* **Language:** Java 21
* **Framework:** Spring Boot 3.4.4, Spring Cloud 2024.0.1
* **Data:** Spring Data JPA, MySQL 8.x, Elasticsearch 8.x
* **Cache/Queue (필요시):** Redis, RabbitMQ
* **MSA Components:** Spring Cloud Netflix Eureka, Spring Cloud Gateway, Spring Cloud OpenFeign
* **Build Tool:** Maven 3.8+
* **Testing:** JUnit 5, Mockito, AssertJ, Spring Boot Test
* **Code Coverage:** Jacoco
* **Code Style:** Naver Code Style
* **Commit Convention:** Conventional Commits
* **CI/CD:** GitHub Actions (JAR 배포 + Shell Script)
* **Collaboration:** Git, GitHub Projects

## 📜 API 문서 (API Documentation)

* 각 서비스의 REST API 명세는 추후 **Swagger/SpringDoc**을 적용하여 제공할 예정입니다. ([Swagger UI 링크 - 추가 예정]())
* 또는, 프로젝트 내 `*.http` 파일을 참고할 수 있습니다. (IntelliJ IDEA 등에서 바로 실행 가능) ([API Spec 파일 위치 - 추가 예정]())

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

* **[NAVER CAMPUS HACKDAY Java 코딩 컨벤션](https://naver.github.io/hackday-conventions-java/)** 을 따릅니다.
* **IntelliJ 설정 방법:**
    * `Preferences (Settings)` > `Editor` > `Code Style` > `Java` 로 이동합니다.
    * `Scheme` 옆의 톱니바퀴 아이콘 클릭 > `Import Scheme` > `IntelliJ IDEA code style XML`
      선택 > [naver-intellij-formatter.xml](https://raw.githubusercontent.com/naver/hackday-conventions-java/master/rule-config/naver-intellij-formatter.xml)
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