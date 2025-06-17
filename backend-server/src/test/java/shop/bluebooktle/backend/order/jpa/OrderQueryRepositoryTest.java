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
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.order.AdminOrderSearchType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.domain.payment.PaymentStatus;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
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
class OrderQueryRepositoryTest {

	@Autowired
	private OrderRepository orderQueryRepository;

	@Autowired
	private TestEntityManager em;

	Order order;
	User user;
	OrderState orderState;
	String searchKeyword;
	OrderStatus orderStatus;
	LocalDate startDate;
	LocalDate endDate;

	@BeforeEach
	void setUp() {
		MembershipLevel level = em.persist(MembershipLevel.builder()
			.name("기본 등급")
			.rate(1)
			.minNetSpent(BigDecimal.ZERO)
			.maxNetSpent(BigDecimal.valueOf(9999999))
			.build());

		user = em.persist(User.builder()
			.membershipLevel(level)
			.loginId("user1")
			.encodedPassword("pw")
			.name("홍길동")
			.email("test@example.com")
			.nickname("테스터")
			.birth("19900101")
			.phoneNumber("01012345678")
			.lastLoginAt(LocalDateTime.now())
			.build());

		DeliveryRule rule = em.persist(DeliveryRule.builder()
			.ruleName("기본 배송")
			.deliveryFee(BigDecimal.valueOf(3000))
			.freeDeliveryThreshold(BigDecimal.valueOf(10000))
			.region(Region.ALL)
			.isActive(true)
			.build());

		orderState = em.persist(OrderState.builder()
			.state(OrderStatus.PREPARING)
			.build());

		order = em.persist(Order.builder()
			.orderState(orderState)
			.deliveryRule(rule)
			.user(user)
			.orderName("주문1")
			.requestedDeliveryDate(LocalDateTime.now().minusDays(1))
			.deliveryFee(BigDecimal.valueOf(3000))
			.ordererName("홍길동")
			.ordererPhoneNumber("010-1111-2222")
			.receiverName("홍길순")
			.receiverPhoneNumber("010-3333-4444")
			.address("서울")
			.detailAddress("역삼")
			.postalCode("12345")
			.orderKey("ORD123")
			.ordererEmail("aaa@email.com")
			.receiverEmail("bbb@email.com")
			.originalAmount(BigDecimal.valueOf(10000))
			.build());

		PaymentType cardType = em.persist(PaymentType.builder()
			.method("카드")
			.build());

		PaymentDetail detail = em.persist(PaymentDetail.builder()
			.paymentType(cardType)
			.paymentStatus(PaymentStatus.READY)
			.paymentKey("PK1234")
			.requestedAt(LocalDateTime.now())
			.approvedAt(LocalDateTime.now())
			.build());

		Payment payment = em.persist(Payment.builder()
			.paymentDetail(detail)
			.order(order)
			.paidAmount(BigDecimal.valueOf(10000))
			.build());

		searchKeyword = order.getOrdererName();
		orderStatus = order.getOrderState().getState();
		startDate = order.getCreatedAt().toLocalDate().minusDays(1);
		endDate = order.getCreatedAt().toLocalDate().plusDays(1);

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("배송일 이전 상태 주문 조회")
	void findOrdersByStatusAndRequestedDeliveryDateBefore() {
		List<Order> result = orderQueryRepository.findOrdersByStatusAndRequestedDeliveryDateBefore(
			OrderStatus.PREPARING, LocalDateTime.now());
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getOrderKey()).isEqualTo("ORD123");
	}

	@Test
	@DisplayName("ID 기준 전체 주문 상세 조회")
	void findFullOrderDetailsById() {
		Optional<Order> result = orderQueryRepository.findFullOrderDetailsById(order.getId());
		assertThat(result).isPresent();
		assertThat(result.get().getUser().getLoginId()).isEqualTo("user1");
	}

	@Test
	@DisplayName("주문키 기준 전체 주문 상세 조회")
	void findOrderDetailsByOrderKey() {
		Optional<Order> result = orderQueryRepository.findOrderDetailsByOrderKey("ORD123");
		assertThat(result).isPresent();
		assertThat(result.get().getOrdererName()).isEqualTo("홍길동");
	}

	@Test
	@DisplayName("관리자 주문 상세 조회 (orderId)로")
	void findAdminOrderDetailsByOrderId() {
		Optional<Order> result = orderQueryRepository.findAdminOrderDetailsByOrderId(order.getId());
		assertThat(result).isPresent();
		assertThat(result.get().getOrderKey()).isEqualTo("ORD123");
	}

	@Test
	@DisplayName("주문 검색 - 주문자명으로 조회")
	void searchOrders_byOrdererName() {
		AdminOrderSearchRequest request = new AdminOrderSearchRequest();
		request.setSearchKeywordType(AdminOrderSearchType.ORDERER_NAME);
		request.setSearchKeyword(searchKeyword); // 공통 사용

		Page<Order> result = orderQueryRepository.searchOrders(request, Pageable.ofSize(10));

		assertThat(result).hasSize(1);
		assertThat(result.getContent().get(0).getOrdererName()).contains(searchKeyword);
	}

	@Test
	@DisplayName("주문 검색 - 주문 상태로 필터링")
	void searchOrders_byOrderStatus() {
		AdminOrderSearchRequest request = new AdminOrderSearchRequest();
		request.setOrderStatusFilter(orderStatus); // 공통 사용

		Page<Order> result = orderQueryRepository.searchOrders(request, Pageable.ofSize(10));

		assertThat(result).hasSize(1);
		assertThat(result.getContent().get(0).getOrderState().getState()).isEqualTo(orderStatus);
	}

	@Test
	@DisplayName("주문 검색 - 기간 필터로 조회")
	void searchOrders_byDateRange() {
		AdminOrderSearchRequest request = new AdminOrderSearchRequest();
		request.setStartDate(startDate);
		request.setEndDate(endDate);

		Page<Order> result = orderQueryRepository.searchOrders(request, Pageable.ofSize(10));

		assertThat(result).hasSize(1);
		assertThat(result.getContent().get(0).getCreatedAt()).isBeforeOrEqualTo(endDate.atTime(23, 59));
	}

	@Test
	@DisplayName("포장 가격 합계 조회 - 결과 없음")
	void findTotalPackagingPriceByOrderId_noPackaging() {
		BigDecimal result = orderQueryRepository.findTotalPackagingPriceByOrderId(order.getId());
		assertThat(result).isEqualTo(BigDecimal.ZERO);
	}

	@Test
	@DisplayName("주문 검색 - 상품명으로 조회")
	void searchOrders_byProductName() {
		// given: Book과 BookOrder를 생성해서 연관관계 설정
		Book book = em.persist(Book.builder()
			.title("테스트 도서")
			.description("설명입니다")
			.isbn("1234567890123")
			.publishDate(LocalDateTime.now())
			.build());

		BookOrder bookOrder = em.persist(BookOrder.builder()
			.book(book)
			.order(order)
			.quantity(1)
			.price(BigDecimal.valueOf(15000))
			.build());

		// flush & clear 해서 실제 쿼리 동작하게
		em.flush();
		em.clear();

		// when
		AdminOrderSearchRequest request = new AdminOrderSearchRequest();

		request.setStartDate(startDate);
		request.setEndDate(endDate);
		Page<Order> result = orderQueryRepository.searchOrders(request, Pageable.ofSize(10));

		// then
		assertThat(result).hasSize(1);
		assertThat(result.getContent().get(0).getOrderKey()).isEqualTo("ORD123");
	}

	@Test
	@DisplayName("AdminOrderSearchType별 검색 키워드 조건 분기 처리 테스트")
	void searchOrders_byEachSearchType() {
		Book book = em.persist(Book.builder()
			.title("테스트 도서")
			.description("설명입니다")
			.isbn("1234567890123")
			.publishDate(LocalDateTime.now())
			.build());

		em.persist(BookOrder.builder()
			.book(book)
			.order(order)
			.quantity(1)
			.price(BigDecimal.valueOf(15000))
			.build());

		for (AdminOrderSearchType type : AdminOrderSearchType.values()) {

			// given
			String keyword = switch (type) {
				case ORDER_KEY -> "ORD123";
				case ORDERER_NAME -> "홍길동";
				case ORDERER_LOGIN_ID -> user.getLoginId(); // setup()에서 저장됨
				case RECEIVER_NAME -> order.getReceiverName();
				case PRODUCT_NAME -> "테스트 도서";
			};

			AdminOrderSearchRequest request = new AdminOrderSearchRequest();
			request.setSearchKeywordType(type);
			request.setSearchKeyword(keyword);
			request.setStartDate(startDate);
			request.setEndDate(endDate);

			// when
			Page<Order> result = orderQueryRepository.searchOrders(request, Pageable.ofSize(10));

			// then
			assertThat(result).hasSize(1);
			assertThat(result.getContent().get(0).getId()).isEqualTo(order.getId());
		}
	}

	@Test
	@DisplayName("주문 검색 - 결제수단 필터링")
	void searchOrders_byPaymentMethod() {
		// given
		AdminOrderSearchRequest request = new AdminOrderSearchRequest();
		request.setPaymentMethodFilter("카드");
		request.setStartDate(startDate);
		request.setEndDate(endDate);
		// when
		Page<Order> result = orderQueryRepository.searchOrders(request, Pageable.ofSize(10));

		// then
		assertThat(result).hasSize(1);
		assertThat(result.getContent().get(0).getPayment().getPaymentDetail().getPaymentType().getMethod()).isEqualTo(
			"카드");
	}
}
