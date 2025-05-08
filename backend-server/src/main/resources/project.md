1. 개요

== 프로젝트 소개

____
Java 기반의 Spring Framework를 학습한 후, 이를 활용하여 온라인에서 도서를 쉽게 구매할 수 있는 직관적이고 종합적인 인터넷 서점 쇼핑몰을 구축하는 것을 목표로 합니다. 이 플랫폼은 사용자가 다양한
카테고리의 도서를 검색하고 구매할 수 있도록 하며, 리뷰와 평점을 통해 신뢰할 수 있는 구매 결정을 지원합니다. 또한, 포인트 적립 및 쿠폰을 통한 할인 혜택, 다양한 이벤트를 통해 매력적인 쇼핑 경험을 제공합니다.
사용자는 대시보드를 통해 주문 및 결제 내역 조회, 포인트 사용 내역 관리, 장바구니 기능 등 편리한 쇼핑 관리 기능을 이용할 수 있습니다.
____

== 학습목표

* *요구사항 분석 및 구현*
  ** 사용자의 요구사항을 체계적으로 분석하고 이를 기반으로 명확한 기능 명세서를 작성하며, 이를 구현하고 통합하는 방법을 학습한다.

* *프로젝트 관리*
  ** 협업 도구를 활용하여 프로젝트 관리 및 팀 협업을 효율적으로 수행하는 방법을 학습한다.

* *지속적인 통합 및 배포*
  ** CI/CD 도구를 사용하여 지속적인 통합 및 배포를 구현하는 방법을 학습한다.

* *마이크로서비스 아키텍처*
  ** Spring Cloud를 활용하여 서비스 디스커버리와 로드 밸런싱을 포함한 마이크로서비스 아키텍처를 구현하는 방법을 학습한다.

* *REST API 개발*
  ** REST API 설계 원칙을 이해하고 이를 적용하여 웹 서비스를 개발하는 방법을 학습한다.

* *인증/인가*
  ** 클라우드 환경에서 JWT 기반의 사용자 인증과 권한 부여를 통해 안전한 애플리케이션을 구축하는 방법을 학습한다.

* *테스트 및 코드 품질*
  ** JUnit을 활용하여 단위 테스트와 통합 테스트를 작성하고 검증하는 방법을 익히며, 정적 코드 분석 도구를 활용하여 코드 품질을 개선할 수 있다.

* *로그 관리 및 모니터링*
  ** 로그를 적절하게 수집하고 모니터링하는 도구를 활용하여 오류 및 경고를 신속하게 감지하고 대응하는 방법을 학습한다.

== 프로젝트 팀 구성

* 5~8 팀 구성
* 팀 구성은 프로젝트 시작 당일 안내할 예정

== 프로젝트 기간

* 8주


2. 기능
   = 도서관리

* 주요 속성들을 참고합니다.

== 도서 카테고리

* 모든 상품(도서)은 반드시 하나이상의 카테고리에 속해야 합니다.
* 최대 10개의 카테고리에 속할 수 있습니다.
* 카테고리는 최소 2단계로 구성됩니다.(적절히 구성합니다.)
  ** ex) 경제 경영 > 경영/비즈니스
  ** ex) IT 모바일 > 웹사이트 > HTML/JavaScript

== 주요 속성

* 책 제목
* 책 목차
* 책 설명
* 카테고리
* 작가
* 출판사
* 출판일시
* ISBN
  ** 세계 방방곡곡에서 출판되는 모든 종류의 책들을 개별적인 고유번호를 부여하여 도서들의 정보와 유통을 효율적으로 관리하는 제도입니다.
  ** https://ko.wikipedia.org/wiki/%EC%9C%84%ED%82%A4%EB%B0%B1%EA%B3%BC:ISBN

* 가격은 정가와 판매가를 등록할 수 있습니다. 정가 대비 판매가의 할인율을 노출합니다.
  ** 정가
  ** 판매가

=== 도서검색 API

* 책 등록 시 다음 API를 활용하여 등록합니다.
  ** https://developers.naver.com/docs/serviceapi/search/book/book.md
  ** https://blog.aladin.co.kr/openapi/popup/6695306

== WYSIWYG Editor

* 책 설명 등록 시 WYSIWYG(위즈윅) Editor를 사용해서 등록합니다.
  ** https://ui.toast.com/tui-editor

== 포장

* 도서는 선물 포장이 가능한지 여부를 설정할 수 있습니다.

== Tag

* 0개 이상의 Tag를 지정할 수 있습니다.
* 등록된 Tag는 검색에 이용됩니다.

== 좋아요

* 사용자에게 좋아요. 기능을 제공합니다.
* 좋아요. 선택된 도서는 사용자의 Mypage에서 확인할 수 있습니다.

= 회원

== 회원가입

* 회원 가입할 수 있는 기능을 제공합니다.
* 이름, 연락처, 이메일, 주소, 생일 등을 포함해야 합니다.

== 회원주소

* 주소는 최대 10개까지 등록할 수 있으며 별칭을 설정할 할 수 있습니다.
  ** ex) 별칭:NHN, 주소 : 경기도 성남시 분당구 대왕판교로 645번 길 16
* 주소는 도로명주소를 기준으로 등록합니다. (지번 주소 사용금지)

== 회원인증

* 사용자는 로그인하여 서비스를 이용할 수 있고, 로그아웃하여 종료할 수 있습니다.

* PAYCO 로그인 제공 ( 아래 내용을 참고합니다. )
  ** https://developers.payco.com/guide/development/apply/web
  ** 애플리케이션 등록
  *** https://developers.payco.com/application

*** 웹 서비스 URL, Callback URL은 프로젝트에서 사용할 URL을 등록합니다. +
image:resources/img_0.png[]

*** 해당 URL을 local에서 테스트하기 위해서는 /etc/hosts에 사용할 URL을 등록합니다.

[,text ]
----
vi sudo /etc/hosts
127.0.0.1 test.com
----

=== 회원 등급

* 3개월 이내 순수 주문금액을 기준으로 산정합니다.
* 순수금액 = 주문금액 - (쿠폰 + 배송비 + 취소금액 + 포장비)

* 등급별 포인트 부여
  ** 일반
  *** 10만원이상
  *** 순수금액의 1% 포인트 적립
  ** 로얄
  *** 10만원 ~ 20만원
  *** 순수금액 * 2% 포인트 적립
  ** 골드
  *** 20만원 ~ 30만원
  ** 플래티넘
  *** 30만원 이상
  *** 순수금액 * 3% 포인트 적립

* 회원등급은 mypage에서 확인할 수 있으며 회원 등급별 혜택이 제공되어야 합니다.

=== 회원 탈퇴

* 회원 탈퇴 시 한 번 등록한 아이디는 다시 사용할 수 없습니다.
* mypage에서 회원 탈퇴할 수 있는 기능이 제공되어야 합니다.
* 회원 탈퇴와 함께 로그아웃 처리되어야 합니다.

=== 휴면 회원

* 최근 로그인한 날짜를 기준으로 3달 이전이면 휴면 상태가 됩니다.
* 휴면상태가 되면 로그인 후 인증을 통해서 휴면상태가 해지됩니다. 즉 다시 로그인 하여 서비스를 이용할 수 있습니다.
* 인증은 Dooray Message Sender로 대처 합니다.

= 주문

* 로그인한 회원은 주문할 수 있습니다.
* 로그인하지 않은 비회원은 주문할 수 있습니다.

== 주문 flow

* 일반 주문 flow
  ** 장바구니 담기 -> 주문서 작성 -> 결제
  ** 주문서 작성 -> 결제

== 배송날짜 지정

* 배송받고 싶은 날짜를 지정할 수 있습니다.
* 지정하지 않는다면 가장 빠른 배송일자에 배송 됩니다.

== 포장

* 사용자는 주문시 포장지를 선택할 수 있습니다. 해당 포장지는 주문하는 도서당 포장여부를 선택할 수 있습니다.
* 모든 포장지는 가격이 설정되어 있으며, 포장지마다 가격은 다를 수 있습니다.

== 배송비

* 회원은 30,000원 이상 주문시 배송비 무료
* 배송비 : 건) 5000원
* 배송비 정책은 관리자에 의해서 변경될 수 있습니다.

== 주문상태

* 대기
  ** 주문서가 생성되고 결제가 완료된 상태
* 배송중
  ** 배송이 진행되고 있느 상태
* 완료
  ** 배송이 완료된 상태
* 반품
  ** 반품이 된 상태
* 주문 취소
  ** 주문이 취소된 상태

== 주문처리

* 관리자가 주문요청서를 확인하여 배송중으로 상태를 변경할 수 있어야 합니다.
* 배송중으로 변경된 주문은 일정 시간경과후 완료 처리 됩니다.

== 반품

* 출고일로부터 10일 이내 미사용 시 반품택배비 차감 후 반품이 가능합니다.
* 파손,파본에 의한 반품은 출고일 기준 30일까지 가능합니다.
* 반품시 결제금액은 포인트로 적립됩니다.
* 주문상태가 반품으로 변경됩니다.

== 결제취소

* 사용자는 배송전 도서에 대해서 결제 취소할 수 있습니다.
* 주문상태가 취소로 변경됩니다.

= 검색

* 도서 명, 도서 설명, 저자.. 등을 을 기준으로 검색이 가능해야 합니다.
* 검색기준은 추가할 수 있습니다.

== 검색 가중치

* 검색 우선순위의 가중치를 적절히 설정합니다.(예시일 뿐 입니다. 검색 가중치를 적절히 설정하세요.)

** ex) 도서 명 * 100
** ex) 도서 설명 * 10
** ex) Tag * 50

* 정렬기준

** 인기도
*** 검색 횟수, 도서 상세페이지 (조횟수)

** 신상품
*** 발행일 기준으로 정렬

** 최저가
*** 최저가 기준으로 오름차순 정렬

** 최고가
*** 최고가 기준으로 내림차순 정렬

** 평점
*** 최소 100(건)이상의 평점평균 순으로 내림차순 정렬

** 리뷰
*** 평점에 상관없이 리뷰 수를 기준으로 내림차순 정렬

== 동의어 or 유의어 검색

* https://ko.wikipedia.org/wiki/%EB%8F%99%EC%9D%98%EC%96%B4[동의어, window=_blank]
* https://ko.wikipedia.org/wiki/%EC%9C%A0%EC%9D%98%EC%96%B4[유의어, window=_blank]

[,text]
----
아기, 유아
학생, 제자
구입, 구매
예쁜, 아름다운
슬픈, 우울한
기질, 특성
LA == 로스엔젤레스
----

= 포인트

== 포인트 적립

* 포인트는 전체 도서에 대해서 기본 적립률을 설정할 수 있습니다.
* 도서 결제시 기본 적립률 기준으로 적립됩니다.
* 회원가입시 5,000 적립됩니다.
* 리뷰작성시 500 적립됩니다.
* 회원가입,리뷰작성, 전체 도서에 대한 기본 적립율을 관리자가 수정할 수 있습니다.

== 포인트 적립/사용 이력

* 회원은 mypage에서 포인트 적립/사용 내역을 확인할 수 있습니다.

== 포인트 결제

* 포인트는 현금처럼 사용하여 결제할 수 있습니다.
* 포인트는 전체 주문금액 또는 일부 금액을 결제할 수 있습니다.
  == 쿠폰

* 쿠폰은 사용자의 쿠폰함에서 발급/사용 내역을 확인할 수 있습니다.
* 쿠폰은 사용기간이 정해져 있습니다.
* 쿠폰은 쿠폰 정책에 의해서 생성됩니다.

=== 쿠폰 정책

* 쿠폰 정책
  ** 할인
  *** ex) 50,000 이상 구매 시 10,000 할인
  *** ex) 20,000 이상 구매 시 3,000 할인
  *** ex) 20,000 이상 구매 시 20% 할인, 최대 10,000원 할인
  **** 30,000 구매 -&gt; 6,000 할인
  **** 100,000 구매 -&gt; 10,000 할인

=== 생일 쿠폰

* 사용자의 생일인 달에 매월 1일에 생일 쿠폰을 제공합니다.
  ** ex) {5월 1일, 5월 5일, 5월 20일} 생일자 : 5월 1일에 쿠폰을 제공합니다.
  ** 쿠폰사용기간 : 5월 1일 ~ 5월 31일(말 일)
* 생일쿠폰의 정책은 '쿠폰 정책'을 기준으로 생성합니다.

=== 특정 도서를 대상으로 사용할 수 았는 쿠폰

* 특정 도서를 대상으로 사용할 수 있는 쿠폰
  ** ex) '대량살상 수학무기'에 사용할 수 있는 쿠폰

=== 특정 카테고리를 대상으로 사용할 수 있는 쿠폰

* 특정 카테고리에 사용할 수 있는 쿠폰
  ** ex) 'IT 모바일' 카테고리에 사용할 수 있는 쿠폰
  ** ex) 'IT 모바일  > 컴퓨터 교육' 카테고리에서 사용할 수 있는 쿠폰

=== Welcome 쿠폰

* 회원가입 시 Welcome 쿠폰을 지급합니다.
* 회원가입일 기준으로 30일 내에 사용할 수 있습니다.
* 정책 : 50,000 이상 구매 시 10,000 할인 쿠폰
* 회원가입 시 쿠폰발급이 실패하더라도 회원가입은 정상 처리 됩니다.
  ** 쿠폰 발급 실패에 대해서 적절한 처리를 합니다.(팀 내에서 고민)

= 장바구니

* 사용자는 주문할 도서를 장바구니에 담고, 필요에 따라 수량을 조절하거나 삭제할 수 있어야 합니다.
* 빈번하게 일어나는 장바구니 조회, 즉 성능을 고려하여 개발합니다.
* 장바구니는 영구적으로 유지되어야 합니다.

== 구현

* 장바구니 구현시 2가지 방법을 선택하여 구현할 수 있습니다. 적절히 장단점을 고려하여 구현합니다.
  ** Session
  ** DB

== 도서 주문

* 도서를 주문할 수 없는 상태라면 장바구니에 담긴 도서는 주문할 수 없습니다.
* 주문할 수 없는 상태의 예는 다음과 같습니다.

** 수량 부족
** 판매가 종료된 도서
** 삭제된 도서

= 리뷰

* 사용자는 주문한 도서에 대해서 리뷰를 작성할 수 있어야 합니다.
* 평가 점수와 사진을 첨부하여 리뷰를 남길 수 있어야 합니다.
  ** 평가 점수는 1~5점을 부여합니다.
* 리뷰 작성 포인트가 적립됩니다.
  ** 리뷰 : 200포인트 적립
  ** 리뷰 + 사진 첨부 : 500포인트 적립
* 리뷰 및 평점 표시
  ** 도서 상세보기 페이지에서 사용자들의 리뷰와 평점이 표시되어야 합니다.
* 작성된 리뷰는 수정할 수 있지만 삭제할 수 없습니다.

== 리뷰 UI 참고

* https://www.yes24.com/Product/Goods/122968067
* https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=326564514
  = 결제

== 카드 결제

* 토스페이먼트 : 다음 문서를 참고하여 개발 환경에서 결제할 수 있도록 구현합니다.
  ** https://docs.tosspayments.com/reference

* 페이코, 네이버페이 등 다양한 결제 수단이 추가될 수 있도록 확장성을 고려하여 개발합니다.

== 정책

* 쿠폰 정책을 반영
* 포인트 정책을 반영

3. 개발환경
   = 개발환경

== 개발도구

* Intellij IDEA - Ultimate
    * https://www.jetbrains.com/ko-kr/idea/download
* 라이선스 만료되면 TA에게 별도 문의하세요

== Language

* java 21 LTS
* Eclipse Temurin
  ** https://adoptium.net/temurin/releases/?version=21

== 빌드도구

* Maven

== 개발 환경

* Spring Framework
  ** 6.x
* Spring Boot
  ** 3.3.x
* Spring Cloud
  ** Spring Cloud Gateway
  *** https://spring.io/projects/spring-cloud-gateway
  ** Spring Cloud Netflex(eureka)
  *** https://spring.io/projects/spring-cloud-netflix
  ** Spring Cloud Config
  *** https://spring.io/projects/spring-cloud-config
  ** Spring cloud OpenFeign
  *** https://spring.io/projects/spring-cloud-openfeign

* JPA
  ** QueryDSL
  *** http://querydsl.com

* Connection Pool
  ** Apache Commons DBCP2
  *** https://commons.apache.org/proper/commons-dbcp/

== 테스트 환경

* Junit5
  ** https://junit.org/junit5/docs/current/user-guide/
* AssertJ
  ** https://assertj.github.io/doc/
* Mokito
  ** https://site.mockito.org/
* SonarQube
  ** http://133.186.241.167:9000/
  ** Test Coverage : 80% 이상

----
id : nhnacademy
pw : nhnacademy123!
----


== 데이터베이스

* MySQL : 8.x
  ** admin tool
  *** WEB : http://133.186.241.167:18080
  *** GUI : https://dev.mysql.com/downloads/workbench/
  ** MYSQL 계정은 TA에게 요청하세요.

----
ip : 133.186.241.167
port : 3306
----

* Redis
  ** Redis database no 는 팀별로 제공될 예정 입니다. TA에게 요청하세요.
  ** 팀별 10개씩 제공 합니다.

----
ip : 133.186.241.167
port : 6379
공통 password : *N2vya7H@muDTwdNMR!
----


== 검색엔진

* Elastic Search : 8.x

** kibana
*** http://115.94.72.197:5601/

** plugin 제공

*** jaso
**** https://github.com/netcrazy/elasticsearch-jaso-analyzer

*** Korean (nori) analysis plugin
**** https://www.elastic.co/guide/en/elasticsearch/plugins/current/analysis-nori.html

*** icu
**** https://www.elastic.co/guide/en/elasticsearch/plugins/current/analysis-icu.html

----
id : elastic
pw : nhnacademy123!

- port list -
  5044: Logstash Beats input
  50000: Logstash TCP input
  9600: Logstash monitoring API
  9200: Elasticsearch HTTP
  9300: Elasticsearch TCP transport
  5601: Kibana

----

== 모델링 (ERD)

* ERDCloud
    - https://www.erdcloud.com
* marco@nhnacademy.com 관리자 맴버 초대하기

== Message Queue

* RabbitMQ
  ** Official
  *** https://www.rabbitmq.com

** WEB Management
*** http://133.186.241.167:15672/

----
id : admin
pw : nhnacademy123!

- port list -
  AMQP : 5672
  WEB Management : 15672

----


== 협업 도구

* GitHub Project
  ** https://docs.github.com/ko/issues/planning-and-tracking-with-projects/learning-about-projects/quickstart-for-projects
* 브랜치 전략
  ** Git Flow
  *** https://nhnacademy.dooray.com/share/pages/iV0jBBZeTIy1tIC-m9gmRg/3375804732742205244

== CI/CD

* Continuous Integration
  ** github
  *** Contributor로 초대할 예정
* Continuous (Delivery, Deployment)
  ** GitHub Action
  *** https://docs.github.com/ko/actions

// ** Jenkins
// *** Cloud에 직접 Jenkins 설치 후 진행 합니다.
** Docker
*** NHN Container Registry (NCR)
*** https://docs.nhncloud.com/ko/Container/NCR/ko/overview/

== UI 개발

* BootStrap 사용
  ** https://getbootstrap.com
  ** free template
  *** https://tabler.io/
  *** https://adminlte.io/
  *** https://startbootstrap.com/theme/sb-admin-2
  *** 기타 free template 사용가능

== NHN Cloud

=== Instance

* https://docs.nhncloud.com/ko/Compute/Instance/ko/overview/
* 인스턴스는 가상의 CPU, 메모리, 기본 디스크로 구성된 가상 서버

=== Secure Key Manager

* https://docs.nhncloud.com/ko/Security/Secure%20Key%20Manager/ko/overview/
* 기밀 데이터, 대칭키, 비대칭키 저장

=== Object Storage

* https://docs.nhncloud.com/ko/Storage/Object%20Storage/ko/Overview/
* 다양한 유형의 데이터를 원하는 만큼 저장하고 필요할 때마다 가져올 수 있는 객체 스토리지 서비스

=== Image Manager

* https://docs.nhncloud.com/ko/Contents%20Delivery/Image%20Manager/ko/Overview/
* 이미지의 저장, 편집 그리고 전송 기능까지 한번에 제공하는 서비스

=== Container Registry

* https://docs.nhncloud.com/ko/Container/NCR/ko/overview/
* Docker 컨테이너 이미지를 쉽고 안전하게 저장, 관리하고 배포할 수 있는 컨테이너 레지스트리 서비스

=== Load Balancer

* https://docs.nhncloud.com/ko/Network/Load%20Balancer/ko/overview/
* 로드 밸런서, 부하 분산

=== Log & Crash Search

* 로그를 수집하여 원하는 로그를 검색하고 조회하는 시스템
  ** https://docs.nhncloud.com/ko/Data%20&%20Analytics/Log%20&%20Crash%20Search/ko/Overview/

== 기타
=== Dooray Hook Sender

* 알림 관련된 메시지는 Dooray Hook Sender를 이용합니다.
  ** https://nhnent.dooray.com/share/pages/zbJkAsWKRWiiLFiAxI7Jtw/3250156109508415493

= 애자일 스크럼을 활용한 프로젝트 관리

____
애자일 스크럼(Scrum)은 반복적인 작업을 통해 프로젝트를 관리하고 팀의 협업을 강화하는 프레임워크입니다. 스프린트(Sprint)라고 불리는 짧은 작업 주기를 통해 목표를 달성하고, 정기적인 회의로 진행 상황을
점검합니다.
____

== 주요 요소

=== 스크럼 팀

* 제품 소유자(Product Owner)
  ** 프로젝트의 요구사항과 우선순위를 관리하고, 백로그를 작성합니다.

* 스크럼 마스터(Scrum Master)
  ** 팀이 스크럼 프로세스를 올바르게 따르도록 돕고, 장애물을 제거합니다.

* 개발 팀(Development Team)
  ** 실제 작업을 수행하고, 제품을 개발합니다.

=== 스프린트(Sprint)

* 정의
  ** 스프린트는 보통 1~4주간의 짧은 작업 주기로, 목표를 달성하기 위해 집중적으로 작업을 수행합니다.

* 목표
  ** 각 스프린트는 명확한 목표를 가지며, 목표를 달성하기 위한 작업 항목이 포함됩니다.

** **프로젝트 기간을 고려해서 1~2주 기준으로 진행 합니다.**

=== 백로그(Backlog)

* **제품 백로그(Product Backlog)**
  ** 제품 개발의 모든 요구사항을 포함한 목록으로, 제품 소유자가 관리합니다.

* **스프린트 백로그(Sprint Backlog)**
  ** 특정 스프린트 동안 수행할 작업 항목의 목록으로, 팀이 선정합니다.

=== 스프린트 회의

* **데일리 스크럼(Daily Scrum)**
  ** 매일 짧은 시간(10~15분) 동안 진행 상황을 점검하고, 이슈를 공유합니다.
  ** 팀에서 모두가 참여할 수 있는 시간을 정합니다.

* **스프린트 리뷰(Sprint Review)**
  ** 스프린트 종료 시, 팀이 모여 작업 결과물을 검토하고 피드백을 받습니다.

* **스프린트 회고(Sprint Retrospective)**
  ** 스프린트 종료 후, 팀이 모여 작업 과정에서의 문제점과 개선점을 논의합니다.

== GitHub Projects 활용

=== Github Prject 생성

* organizations 생성
  ** 네이밍 규칙 : nhnacademy-be{기수}-{team-name}
  *** ex) nhnacademy-be3-begopa

* Owner권한으로 초대하기
  ** nhn-academy-marco
  ** 담당TA

=== 백로그 관리

* **GitHub Issues**
  ** 각 작업 항목을 이슈로 생성하고, 제품 백로그와 스프린트 백로그에 추가합니다.

* **우선순위 설정**
  ** 이슈에 우선순위 라벨을 추가하여 작업 항목의 중요도를 표시합니다.

image:resources/image1.png[]

=== 로드맵 작성

* 프로젝트의 장기적인 계획을 시각적으로 표현합니다. 주요 마일스톤과 목표를 설정하여 프로젝트의 전체 진행 방향을 명확히 합니다.
  image:resources/image2.png[]

=== 칸반 보드 사용

* **칸반 보드 정의**
  ** 작업의 상태(예: 할 일, 진행 중, 지연됨, 완료됨)를 나타내는 컬럼과 카드를 사용하여 작업을 관리합니다.

* **Status**
  ** "할 일 (To Do)"
  ** "진행 중 (In Progress)"
  ** "지연됨 (Delayed)"
  ** "완료됨 (Done)"

image:resources/image3.png[]

=== 데일리 스크럼

* 매일 진행 상황을 GitHub Issues에 업데이트하고, 필요한 경우 스크럼 마스터가 이슈를 조정합니다.
  ** ** 1주 단위로 돌아가면서 스크럼 마스터 역할을 합니다. **

image:resources/image4.png[]

ERD 검토 회의
MSA관련 문의 내용 확인 후 전달 필요

CI/CD 서버 연동 완료(프로젝트 빌드 후 연결 1-2주 이내로 붙이기 가능)


