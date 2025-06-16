# <img src="https://github.com/nhnacademy-be9-bluebooktle/bluebooktle-shop/blob/master/frontend-server/src/main/resources/static/images/logo.png?raw=true" width="300" alt="BlueBooktle Logo" />


## 📌 프로젝트 정보

- **프로젝트명**: 블루북틀 (BlueBooktle)
- **목적**: 도서를 검색하고 구매할 수 있는 온라인 서점 플랫폼 구현
- **기술스택**: Spring Boot, MySQL, Redis, Minio 등
- **개발 기간**: 2025.04.28 ~ 2025.06.13
- **웹사이트**: [https://bluebooktle.shop](https://bluebooktle.shop)

---

<h3 align="left">👥 팀 소개</h3>

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/marf3927.png" width="100" /><br/>
      <sub><b>박태희</b></sub><br/>
      <a href="https://github.com/marf3927" target="_blank">@marf3927</a>
    </td>
    <td align="center">
      <img src="https://github.com/Shin-Gunsu.png" width="100" /><br/>
      <sub><b>신건수</b></sub><br/>
      <a href="https://github.com/Shin-Gunsu" target="_blank">@gunsurius</a>
    </td>
    <td align="center">
      <img src="https://github.com/yoplenople.png" width="100" /><br/>
      <sub><b>이용준</b></sub><br/>
      <a href="https://github.com/yoplenople" target="_blank">@yoplenople</a>
    </td>
    <td align="center">
      <img src="https://github.com/oonsy99.png" width="100" /><br/>
      <sub><b>문소연</b></sub><br/>
      <a href="https://github.com/oonsy99" target="_blank">@oonsy99</a>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/sondonghun97.png" width="100" /><br/>
      <sub><b>손동훈</b></sub><br/>
      <a href="https://github.com/sondonghun97" target="_blank">@sondonghun97</a>
    </td>
    <td align="center">
      <img src="https://github.com/chossa.png" width="100" /><br/>
      <sub><b>조수아</b></sub><br/>
      <a href="https://github.com/chossa" target="_blank">@chossa</a>
    </td>
    <td align="center">
      <img src="https://github.com/Combro-Kim.png" width="100" /><br/>
      <sub><b>김재혁</b></sub><br/>
      <a href="https://github.com/Combro-Kim" target="_blank">@Combro-Kim</a>
    </td>
    <td align="center">
      <img src="https://github.com/sslynn22.png" width="100" /><br/>
      <sub><b>이서인</b></sub><br/>
      <a href="https://github.com/sslynn22" target="_blank">@sslynn22</a>
    </td>
  </tr>
</table>


---

## 📝 프로젝트 소개

**BlueBooktle**은 사용자에게 도서 검색부터 장바구니, 주문, 결제까지 전자상거래의 핵심 기능을 제공하는 통합형 온라인 서점입니다.  
Spring Boot 기반의 RESTful API를 중심으로 구현되었으며, MySQL, Redis, Minio 등을 활용해 백엔드 인프라를 구축했습니다.  
회원 인증, 포인트 적립/사용, 쿠폰 발급, 도서 찜, 리뷰 작성 등의 기능을 통해 실사용자 중심의 경험을 구현했습니다.  

---

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
