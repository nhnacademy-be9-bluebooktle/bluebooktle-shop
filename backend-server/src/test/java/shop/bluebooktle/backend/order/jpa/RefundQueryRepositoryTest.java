package shop.bluebooktle.backend.order.jpa;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import shop.bluebooktle.backend.book.entity.Book;
import shop.bluebooktle.backend.book_order.entity.BookOrder;
import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.entity.Refund;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.RefundQueryRepository;
import shop.bluebooktle.backend.order.repository.RefundRepository;
import shop.bluebooktle.backend.order.repository.impl.RefundQueryRepositoryImpl;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.domain.payment.PaymentStatus;
import shop.bluebooktle.common.domain.refund.RefundReason;
import shop.bluebooktle.common.domain.refund.RefundSearchType;
import shop.bluebooktle.common.domain.refund.RefundStatus;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({
	QueryDslConfig.class,
	JpaAuditingConfiguration.class,
	CryptoUtils.class, ProfileAwareStringCryptoConverter.class,
})
class RefundQueryRepositoryTest {

	@Autowired
	private RefundRepository refundQueryRepository;

	@Autowired
	private TestEntityManager em;

	private Order order;
	private Refund refund1, refund2;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		// 공통 엔티티 세팅
		MembershipLevel level = em.persist(MembershipLevel.builder()
			.name("기본")
			.rate(1)
			.minNetSpent(BigDecimal.ZERO)
			.maxNetSpent(BigDecimal.valueOf(999_999))
			.build());

		User user = em.persist(User.builder()
			.membershipLevel(level)
			.loginId("user1")
			.encodedPassword("pw")
			.name("홍길동")
			.email("test@example.com")
			.nickname("tester")
			.birth("19900101")
			.phoneNumber("01012345678")
			.lastLoginAt(LocalDateTime.now())
			.build());

		DeliveryRule rule = em.persist(DeliveryRule.builder()
			.ruleName("ruleA")
			.deliveryFee(BigDecimal.valueOf(3000))
			.freeDeliveryThreshold(BigDecimal.valueOf(20000))
			.region(Region.ALL)
			.isActive(true)
			.build());

		OrderState state = em.persist(OrderState.builder()
			.state(OrderStatus.PREPARING)
			.build());

		// 첫 번째 주문 (order1)
		order = em.persist(Order.builder()
			.orderState(state)
			.deliveryRule(rule)
			.user(user)
			.orderName("주문1")
			.requestedDeliveryDate(LocalDateTime.now().minusDays(2))
			.deliveryFee(BigDecimal.valueOf(3000))
			.ordererName("홍길동")
			.ordererPhoneNumber("010-1111-2222")
			.receiverName("홍길순")
			.receiverPhoneNumber("010-3333-4444")
			.address("서울")
			.detailAddress("역삼")
			.postalCode("12345")
			.orderKey("ORD123")
			.ordererEmail("aaa@test.com")
			.receiverEmail("bbb@test.com")
			.originalAmount(BigDecimal.valueOf(10000))
			.build());

		// 두 번째 주문 (order2) — refund2 을 위해
		Order order2 = em.persist(Order.builder()
			.orderState(state)
			.deliveryRule(rule)
			.user(user)
			.orderName("주문2")
			.requestedDeliveryDate(LocalDateTime.now().minusDays(1))
			.deliveryFee(BigDecimal.valueOf(3000))
			.ordererName("김철수")
			.ordererPhoneNumber("010-2222-3333")
			.receiverName("이영희")
			.receiverPhoneNumber("010-4444-5555")
			.address("부산")
			.detailAddress("해운대")
			.postalCode("54321")
			.orderKey("ORD456")
			.ordererEmail("ccc@test.com")
			.receiverEmail("ddd@test.com")
			.originalAmount(BigDecimal.valueOf(8000))
			.build());

		now = LocalDateTime.now();

		// refund1 → order1 에 매핑
		refund1 = em.persist(Refund.builder()
			.order(order)
			.date(now)
			.reason(RefundReason.CHANGE_OF_MIND)
			.reasonDetail("detail1")
			.status(RefundStatus.COMPLETE)
			.price(BigDecimal.valueOf(5000))
			.build());

		// refund2 → order2 에 매핑
		refund2 = em.persist(Refund.builder()
			.order(order2)
			.date(now.plusDays(5))
			.reason(RefundReason.CHANGE_OF_MIND)
			.reasonDetail("detail2")
			.status(RefundStatus.PENDING)
			.price(BigDecimal.valueOf(7000))
			.build());

		em.flush();
		em.clear();
	}


	@Test
	@DisplayName("환불 검색 - 필터 없이 전체 조회")
	void searchRefunds_all() {
		RefundSearchRequest req = new RefundSearchRequest(null, null, null, null, null);
		Page<Refund> page = refundQueryRepository.searchRefunds(req, Pageable.ofSize(10));
		assertThat(page.getTotalElements()).isEqualTo(2);
	}

	@Test
	@DisplayName("환불 검색 - 날짜 범위 필터")
	void searchRefunds_byDateRange() {
		RefundSearchRequest req = new RefundSearchRequest(
			now.toLocalDate().minusDays(1),
			now.toLocalDate().plusDays(1),
			null, null, null);
		Page<Refund> page = refundQueryRepository.searchRefunds(req, Pageable.ofSize(10));
		assertThat(page.getTotalElements()).isEqualTo(1);
		assertThat(page.getContent().get(0).getId()).isEqualTo(refund1.getId());
	}

	@Test
	@DisplayName("환불 검색 - 상태 필터")
	void searchRefunds_byStatus() {
		// APPROVED 만
		RefundSearchRequest req1 = new RefundSearchRequest(null, null, RefundStatus.COMPLETE, null, null);
		assertThat(refundQueryRepository.searchRefunds(req1, Pageable.ofSize(10)).getTotalElements()).isEqualTo(1);

		// REQUESTED 만
		RefundSearchRequest req2 = new RefundSearchRequest(null, null, RefundStatus.PENDING, null, null);
		assertThat(refundQueryRepository.searchRefunds(req2, Pageable.ofSize(10)).getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("환불 검색 - 주문번호(ORDER_KEY) 키워드 필터")
	void searchRefunds_byOrderKey() {
		RefundSearchRequest req = new RefundSearchRequest(
			null, null, null,
			RefundSearchType.ORDER_KEY, "ORD123");
		Page<Refund> page = refundQueryRepository.searchRefunds(req, Pageable.ofSize(10));
		// ORD123 주문에 매핑된 환불은 1건
		assertThat(page.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("환불 검색 - 주문자명(ORDERER_NAME) 키워드 필터")
	void searchRefunds_byOrdererName() {
		RefundSearchRequest req = new RefundSearchRequest(
			null, null, null,
			RefundSearchType.ORDERER_NAME, "홍길동");
		Page<Refund> page = refundQueryRepository.searchRefunds(req, Pageable.ofSize(10));

		// 주문자명이 홍길동인 환불이 2건
		assertThat(page.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("단건 조회 by ID - 상세Joined 조회")
	void findRefundDetailsById() {
		Optional<Refund> opt = refundQueryRepository.findRefundDetailsById(refund1.getId());
		assertThat(opt).isPresent();
		Refund r = opt.get();
		assertThat(r.getOrder().getOrderKey()).isEqualTo("ORD123");
		assertThat(r.getStatus()).isEqualTo(RefundStatus.COMPLETE);
	}
}