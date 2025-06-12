package shop.bluebooktle.backend.point.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import shop.bluebooktle.backend.config.JpaAuditingConfiguration;
import shop.bluebooktle.backend.config.QueryDslConfig;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.repository.DeliveryRuleRepository;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.repository.PaymentDetailRepository;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.payment.repository.PaymentTypeRepository;
import shop.bluebooktle.backend.point.entity.PaymentPointHistory;
import shop.bluebooktle.backend.user.repository.MembershipLevelRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.converter.PointSourceTypeConverter;
import shop.bluebooktle.common.converter.ProfileAwareStringCryptoConverter;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.domain.payment.PaymentStatus;
import shop.bluebooktle.common.domain.point.PointSourceTypeEnum;
import shop.bluebooktle.common.entity.auth.MembershipLevel;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.util.CryptoUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfiguration.class, CryptoUtils.class,
	ProfileAwareStringCryptoConverter.class, PointSourceTypeConverter.class})
@EntityScan(basePackages = {"shop.bluebooktle.common.entity", "shop.bluebooktle.backend"})
@EnableJpaRepositories(basePackages = "shop.bluebooktle.backend")
public class PaymentPointHistoryRepositoryTest {

	@Autowired
	private PaymentPointHistoryRepository paymentPointHistoryRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private PointHistoryRepository pointHistoryRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderStateRepository orderStateRepository;

	@Autowired
	private MembershipLevelRepository membershipLevelRepository;

	@Autowired
	private DeliveryRuleRepository deliveryRuleRepository;

	@Autowired
	private PaymentTypeRepository paymentTypeRepository;

	private Payment savedPayment;
	private PointHistory savedPointHistory;

	private User createUser() {
		MembershipLevel ml = membershipLevelRepository.save(
			MembershipLevel.builder()
				.name("BASIC")
				.rate(1)
				.minNetSpent(BigDecimal.ZERO)
				.maxNetSpent(BigDecimal.valueOf(100_000))
				.build()
		);

		return userRepository.save(
			User.builder()
				.membershipLevel(ml)
				.loginId("user" + System.nanoTime()) // 중복 방지를 위해 매번 유니크한 값을 넣어줌
				.encodedPassword("pwd123")
				.name("테스터")
				.nickname("nick" + System.nanoTime())
				.email("test" + System.nanoTime() + "@example.com")
				.birth("2003-02-02")
				.phoneNumber("01012345678")
				.type(UserType.USER)
				.status(UserStatus.ACTIVE)
				.build()
		);
	}

	private OrderState createOrderState(OrderStatus status) {
		return orderStateRepository.save(new OrderState(status));
	}

	private DeliveryRule createDeliveryRule() {
		return deliveryRuleRepository.save(
			new DeliveryRule("test-rule", BigDecimal.ZERO, BigDecimal.ZERO, Region.ALL, true)
		);
	}

	private Order createOrder(User user, OrderState orderState, DeliveryRule deliveryRule) {
		return orderRepository.save(Order.builder()
			.orderName("test-order-name")
			.user(user)
			.orderState(orderState)
			.deliveryRule(deliveryRule)
			.orderKey(UUID.randomUUID().toString())
			.originalAmount(BigDecimal.valueOf(100_000))
			.ordererEmail(user.getEmail())
			.ordererName(user.getName())
			.ordererPhoneNumber(user.getPhoneNumber())
			.address("address")
			.detailAddress("detail-address")
			.receiverName(user.getName())
			.receiverPhoneNumber("111111111")
			.receiverEmail("2@2")
			.postalCode("11111")
			.saleDiscountAmount(BigDecimal.ZERO)
			.couponDiscountAmount(BigDecimal.ZERO)
			.deliveryFee(BigDecimal.ZERO)
			.build());
	}

	private PaymentType createPaymentType() {
		return paymentTypeRepository.save(PaymentType.builder()
			.method("CARD")
			.build()
		);
	}

	private PaymentDetail createPaymentDetail(PaymentType paymentType) {
		return paymentDetailRepository.save(PaymentDetail.builder()
			.paymentStatus(PaymentStatus.DONE)
			.paymentType(paymentType)
			.requestedAt(LocalDateTime.now())
			.build()
		);
	}

	private Payment createPayment(Order order, PaymentDetail paymentDetail) {
		return paymentRepository.save(Payment.builder()
			.order(order)
			.paymentDetail(paymentDetail)
			.paidAmount(order.getOriginalAmount())
			.build());
	}

	private PointHistory createPointHistory(User user, PointSourceTypeEnum sourceType, BigDecimal value) {
		return pointHistoryRepository.save(PointHistory.builder()
			.user(user)
			.sourceType(sourceType)
			.value(value)
			.build());
	}

	@BeforeEach
	void setUp() {
		User user = createUser();
		OrderState orderState = createOrderState(OrderStatus.PENDING);
		DeliveryRule deliveryRule = createDeliveryRule();
		Order order = createOrder(user, orderState, deliveryRule);
		PaymentType paymentType = createPaymentType();
		PaymentDetail paymentDetail = createPaymentDetail(paymentType);

		savedPayment = createPayment(order, paymentDetail);
		savedPointHistory = createPointHistory(user, PointSourceTypeEnum.PAYMENT_EARN, new BigDecimal("100"));
	}

	@Test
	@Transactional
	@DisplayName("PaymentPointHistory 저장 테스트")
	void saveTest() {
		PaymentPointHistory paymentPointHistory = PaymentPointHistory.builder()
			.payment(savedPayment)
			.pointHistory(savedPointHistory)
			.build();

		PaymentPointHistory savedPph = paymentPointHistoryRepository.save(paymentPointHistory);

		assertThat(savedPph.getId()).isNotNull();
		assertThat(savedPph.getPayment()).isEqualTo(savedPayment);
		assertThat(savedPph.getPointHistory()).isEqualTo(savedPointHistory);
		assertThat(savedPph.getCreatedAt()).isNotNull();
	}
}
