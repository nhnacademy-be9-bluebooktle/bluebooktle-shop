package shop.bluebooktle.backend.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.order.entity.Order;
import shop.bluebooktle.backend.order.entity.OrderState;
import shop.bluebooktle.backend.order.repository.OrderRepository;
import shop.bluebooktle.backend.order.repository.OrderStateRepository;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentCancelResponse;
import shop.bluebooktle.backend.payment.dto.response.GenericPaymentConfirmResponse;
import shop.bluebooktle.backend.payment.dto.response.PaymentStatus;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.entity.PaymentDetail;
import shop.bluebooktle.backend.payment.entity.PaymentType;
import shop.bluebooktle.backend.payment.event.type.PaymentPointEarnEvent;
import shop.bluebooktle.backend.payment.gateway.PaymentGateway;
import shop.bluebooktle.backend.payment.repository.PaymentDetailRepository;
import shop.bluebooktle.backend.payment.repository.PaymentRepository;
import shop.bluebooktle.backend.payment.repository.PaymentTypeRepository;
import shop.bluebooktle.backend.payment.service.impl.PaymentServiceImpl;
import shop.bluebooktle.backend.point.entity.PaymentPointHistory;
import shop.bluebooktle.backend.point.repository.PointHistoryRepository;
import shop.bluebooktle.backend.user.repository.UserRepository;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.payment.request.PaymentCancelRequest;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.entity.point.PointHistory;
import shop.bluebooktle.common.exception.ApplicationException;
import shop.bluebooktle.common.exception.ErrorCode;
import shop.bluebooktle.common.exception.order.OrderNotFoundException;
import shop.bluebooktle.common.exception.order.order_state.OrderInvalidStateException;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private PaymentDetailRepository paymentDetailRepository;
	@Mock
	private PaymentTypeRepository paymentTypeRepository;
	@Mock
	private OrderStateRepository orderStateRepository;
	@Mock
	private OrderService orderService;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PointHistoryRepository pointHistoryRepository;

	@Mock
	private PaymentGateway tossGateway;
	@Mock
	private PaymentGateway pointGateway;

	@BeforeEach
	void setUp() {
		List<PaymentGateway> gatewayList = List.of(tossGateway, pointGateway);
		ReflectionTestUtils.setField(paymentService, "gatewayList", gatewayList);

		when(tossGateway.getGatewayName()).thenReturn("TOSS");
		when(pointGateway.getGatewayName()).thenReturn("POINT");

		paymentService.initializeGateways();
	}

	@Test
	@DisplayName("Toss 결제 승인 성공 (회원)")
	void confirmPayment_toss_member_success() {
		// given
		Long userId = 1L;
		String orderKey = "testOrderKey-1234";
		String paymentKey = "tossPaymentKey-5678";
		BigDecimal amountToPay = new BigDecimal("15000");

		PaymentConfirmRequest request = new PaymentConfirmRequest(paymentKey, orderKey, amountToPay.longValue());

		User mockUser = User.builder().id(userId).build();
		Order mockOrder = Order.builder()
			.user(mockUser)
			.orderKey(orderKey)
			// 최종 결제 금액(15000) = 상품 원가(10100) + 배송비(3000) + 포장비(2000) - 포인트사용(100)
			.originalAmount(new BigDecimal("10100"))
			.deliveryFee(new BigDecimal("3000"))
			.pointUseAmount(new BigDecimal("100"))
			.build();

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(mockOrder));
		when(orderRepository.findTotalPackagingPriceByOrderId(any())).thenReturn(new BigDecimal("2000"));
		when(orderStateRepository.findByState(OrderStatus.PENDING)).thenReturn(
			Optional.of(new OrderState(OrderStatus.PENDING)));
		when(orderStateRepository.findByState(OrderStatus.PREPARING)).thenReturn(
			Optional.of(new OrderState(OrderStatus.PREPARING)));
		when(paymentTypeRepository.findByMethod("TOSS_간편결제")).thenReturn(Optional.of(new PaymentType("TOSS_간편결제")));
		when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GenericPaymentConfirmResponse gatewayResponse = new GenericPaymentConfirmResponse(
			paymentKey, orderKey, amountToPay, PaymentStatus.SUCCESS, "간편결제", null
		);
		when(tossGateway.confirmPayment(any(PaymentConfirmRequest.class))).thenReturn(gatewayResponse);

		// when
		paymentService.confirmPayment(request, "TOSS");

		// then
		verify(orderRepository, times(1)).findByOrderKey(orderKey);
		verify(tossGateway, times(1)).confirmPayment(request);
		verify(paymentDetailRepository, times(1)).save(any(PaymentDetail.class));
		verify(paymentRepository, times(1)).save(any(Payment.class));
		verify(orderRepository, times(1)).save(mockOrder);

		// 회원이므로 포인트 적립 이벤트가 발행되었는지 확인
		verify(eventPublisher, times(1)).publishEvent(any(PaymentPointEarnEvent.class));
	}

	@Test
	@DisplayName("Toss 결제 승인 성공 (비회원)")
	void confirmPayment_toss_non_member_success() {
		// given
		String orderKey = "testOrderKey-non-member-1234";
		String paymentKey = "tossPaymentKey-non-member-5678";
		BigDecimal amountToPay = new BigDecimal("15000");

		PaymentConfirmRequest request = new PaymentConfirmRequest(paymentKey, orderKey, amountToPay.longValue());

		// user를 null로 설정하여 비회원 주문을 시뮬레이션
		Order mockOrder = Order.builder()
			.user(null)
			.orderKey(orderKey)
			.originalAmount(new BigDecimal("10100"))
			.deliveryFee(new BigDecimal("3000"))
			.pointUseAmount(new BigDecimal("100"))
			.build();

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(mockOrder));
		when(orderRepository.findTotalPackagingPriceByOrderId(any())).thenReturn(new BigDecimal("2000"));
		when(orderStateRepository.findByState(OrderStatus.PENDING)).thenReturn(
			Optional.of(new OrderState(OrderStatus.PENDING)));
		when(orderStateRepository.findByState(OrderStatus.PREPARING)).thenReturn(
			Optional.of(new OrderState(OrderStatus.PREPARING)));
		when(paymentTypeRepository.findByMethod("TOSS_간편결제")).thenReturn(Optional.of(new PaymentType("TOSS_간편결제")));
		when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GenericPaymentConfirmResponse gatewayResponse = new GenericPaymentConfirmResponse(
			paymentKey, orderKey, amountToPay, PaymentStatus.SUCCESS, "간편결제", null
		);
		when(tossGateway.confirmPayment(any(PaymentConfirmRequest.class))).thenReturn(gatewayResponse);

		// when
		paymentService.confirmPayment(request, "TOSS");

		// then
		verify(orderRepository, times(1)).findByOrderKey(orderKey);
		verify(tossGateway, times(1)).confirmPayment(request);
		verify(paymentRepository, times(1)).save(any(Payment.class));
		verify(orderRepository, times(1)).save(mockOrder);

		// 비회원이므로 포인트 적립 이벤트가 발행되지 않았음을 검증
		verify(eventPublisher, never()).publishEvent(any(PaymentPointEarnEvent.class));
	}

	@Test
	@DisplayName("Toss 결제 승인 실패 - 금액 불일치")
	void confirmPayment_toss_fail_amountMismatch() {
		// given
		String orderKey = "testOrderKey-1234";
		String paymentKey = "tossPaymentKey-5678";

		// 서비스가 계산할 기대 금액은 15,000원
		Order mockOrder = Order.builder()
			.user(User.builder().id(1L).build())
			.orderKey(orderKey)
			.originalAmount(new BigDecimal("10100"))
			.deliveryFee(new BigDecimal("3000"))
			.pointUseAmount(new BigDecimal("100"))
			.build();

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(mockOrder));
		when(orderRepository.findTotalPackagingPriceByOrderId(any())).thenReturn(new BigDecimal("2000"));

		// 실제 요청 금액은 14,000원으로 설정하여 금액 불일치 유도
		BigDecimal incorrectAmount = new BigDecimal("14000");
		PaymentConfirmRequest requestWithIncorrectAmount = new PaymentConfirmRequest(paymentKey, orderKey,
			incorrectAmount.longValue());

		// when & then
		ApplicationException exception = assertThrows(ApplicationException.class, () -> {
			paymentService.confirmPayment(requestWithIncorrectAmount, "TOSS");
		});

		assertEquals(ErrorCode.PAYMENT_AMOUNT_MISMATCH, exception.getErrorCode());
		verify(paymentRepository, never()).save(any());
		verify(paymentDetailRepository, never()).save(any());
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	@DisplayName("Toss 결제 승인 실패 - 게이트웨이 거절")
	void confirmPayment_toss_fail_gatewayRejection() {
		// given
		String orderKey = "testOrderKey-1234";
		String paymentKey = "tossPaymentKey-5678";
		BigDecimal amountToPay = new BigDecimal("15000");

		PaymentConfirmRequest request = new PaymentConfirmRequest(paymentKey, orderKey, amountToPay.longValue());

		Order mockOrder = Order.builder()
			.user(User.builder().id(1L).build())
			.orderKey(orderKey)
			.originalAmount(new BigDecimal("10100"))
			.deliveryFee(new BigDecimal("3000"))
			.pointUseAmount(new BigDecimal("100"))
			.build();

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(mockOrder));
		when(orderRepository.findTotalPackagingPriceByOrderId(any())).thenReturn(new BigDecimal("2000"));
		when(orderStateRepository.findByState(OrderStatus.PENDING)).thenReturn(
			Optional.of(new OrderState(OrderStatus.PENDING)));

		// Toss Gateway가 '실패' 응답을 반환하도록 설정
		GenericPaymentConfirmResponse gatewayFailureResponse = new GenericPaymentConfirmResponse(
			null, orderKey, amountToPay, PaymentStatus.FAILURE,
			"TOSS_API_ERROR", Map.of("errorMessage", "잔액이 부족합니다.")
		);
		when(tossGateway.confirmPayment(any(PaymentConfirmRequest.class))).thenReturn(gatewayFailureResponse);

		// when & then
		ApplicationException exception = assertThrows(ApplicationException.class, () -> {
			paymentService.confirmPayment(request, "TOSS");
		});

		assertEquals(ErrorCode.PAYMENT_CONFIRMATION_FAILED, exception.getErrorCode());
		assertTrue(exception.getMessage().contains("잔액이 부족합니다."));

		// 게이트웨이 실패 시, 결제 관련 데이터는 저장되면 안 됨
		verify(paymentRepository, never()).save(any());
		verify(paymentDetailRepository, never()).save(any());
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	@DisplayName("결제 취소 성공 (회원, 포인트 적립 회수)")
	void cancelPayment_toss_member_success() {
		// given
		Long userId = 1L;
		String orderKey = "testOrderKey-to-cancel";
		String paymentKey = "tossPaymentKey-to-cancel";
		String cancelReason = "단순 변심";
		BigDecimal earnedPoint = new BigDecimal("100"); // 결제 시 100포인트 적립 가정
		BigDecimal initialPoint = new BigDecimal("1000");

		PaymentCancelRequest request = new PaymentCancelRequest(orderKey, cancelReason);

		User mockUser = User.builder().id(userId).pointBalance(initialPoint).build();
		OrderState preparingState = new OrderState(OrderStatus.PREPARING);
		PointHistory mockPointHistory = PointHistory.builder().value(earnedPoint).build();
		PaymentPointHistory mockPaymentPointHistory = new PaymentPointHistory(null, mockPointHistory);
		PaymentType mockPaymentType = new PaymentType("TOSS_간편결제");
		PaymentDetail mockPaymentDetail = PaymentDetail.builder()
			.paymentKey(paymentKey)
			.paymentType(mockPaymentType)
			.paymentStatus(shop.bluebooktle.common.domain.payment.PaymentStatus.DONE)
			.build();

		Payment mockPayment = Payment.builder()
			.paymentDetail(mockPaymentDetail)
			.paidAmount(new BigDecimal("15000"))
			.build();
		ReflectionTestUtils.setField(mockPayment, "paymentPointHistory", mockPaymentPointHistory);

		Order mockOrder = Order.builder()
			.user(mockUser)
			.orderKey(orderKey)
			.orderState(preparingState)
			.build();
		ReflectionTestUtils.setField(mockOrder, "payment", mockPayment);
		mockPayment.setOrder(mockOrder);

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(mockOrder));

		GenericPaymentCancelResponse gatewayResponse = new GenericPaymentCancelResponse(
			"transactionKey-cancel", orderKey, new BigDecimal("15000"), PaymentStatus.SUCCESS, "간편결제", null);
		when(tossGateway.cancelPayment(any())).thenReturn(gatewayResponse);

		// when
		paymentService.cancelPayment(request, userId);

		// then
		verify(tossGateway, times(1)).cancelPayment(any());
		verify(orderService, times(1)).cancelOrderInternal(mockOrder);

		// 포인트가 정상적으로 차감(회수)되었는지 검증
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository, times(1)).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();
		assertEquals(0, initialPoint.subtract(earnedPoint).compareTo(savedUser.getPointBalance()));

		verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
		ArgumentCaptor<PaymentDetail> paymentDetailCaptor = ArgumentCaptor.forClass(PaymentDetail.class);
		verify(paymentDetailRepository, times(1)).save(paymentDetailCaptor.capture());
		assertEquals(shop.bluebooktle.common.domain.payment.PaymentStatus.CANCELED,
			paymentDetailCaptor.getValue().getPaymentStatus());
	}

	@Test
	@DisplayName("결제 취소 성공 (비회원)")
	void cancelPayment_toss_non_member_success() {
		// given
		Long userId = null; // 비회원이므로 userId는 null
		String orderKey = "testOrderKey-non-member-to-cancel";
		String paymentKey = "tossPaymentKey-non-member-to-cancel";
		String cancelReason = "단순 변심";

		PaymentCancelRequest request = new PaymentCancelRequest(orderKey, cancelReason);
		OrderState preparingState = new OrderState(OrderStatus.PREPARING);
		PaymentType mockPaymentType = new PaymentType("TOSS_간편결제");
		PaymentDetail mockPaymentDetail = PaymentDetail.builder()
			.paymentKey(paymentKey)
			.paymentType(mockPaymentType)
			.paymentStatus(shop.bluebooktle.common.domain.payment.PaymentStatus.DONE)
			.build();

		Payment mockPayment = Payment.builder()
			.paymentDetail(mockPaymentDetail)
			.paidAmount(new BigDecimal("15000"))
			.build();

		Order mockOrder = Order.builder().user(null).orderKey(orderKey).build();
		ReflectionTestUtils.setField(mockOrder, "orderState", preparingState);
		ReflectionTestUtils.setField(mockOrder, "payment", mockPayment);
		mockPayment.setOrder(mockOrder);

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(mockOrder));

		GenericPaymentCancelResponse gatewayResponse = new GenericPaymentCancelResponse(
			"transactionKey-cancel", orderKey, new BigDecimal("15000"), PaymentStatus.SUCCESS, "간편결제", null);
		when(tossGateway.cancelPayment(any())).thenReturn(gatewayResponse);

		// when
		paymentService.cancelPayment(request, userId);

		// then
		verify(tossGateway, times(1)).cancelPayment(any());
		verify(orderService, times(1)).cancelOrderInternal(mockOrder);

		// 비회원이므로 포인트 관련 로직이 호출되지 않았음을 검증
		verify(userRepository, never()).save(any());
		verify(pointHistoryRepository, never()).save(any());

		ArgumentCaptor<PaymentDetail> paymentDetailCaptor = ArgumentCaptor.forClass(PaymentDetail.class);
		verify(paymentDetailRepository, times(1)).save(paymentDetailCaptor.capture());
		assertEquals(shop.bluebooktle.common.domain.payment.PaymentStatus.CANCELED,
			paymentDetailCaptor.getValue().getPaymentStatus());
	}

	@Test
	@DisplayName("결제 취소 실패 - 취소 불가능한 주문 상태")
	void cancelPayment_fail_invalidOrderState() {
		// given
		Long userId = 1L;
		String orderKey = "testOrderKey-shipping";
		PaymentCancelRequest request = new PaymentCancelRequest(orderKey, "단순 변심");

		// 주문 상태를 '배송중(SHIPPING)'으로 설정하여 취소 불가능한 조건 생성
		OrderState shippingState = new OrderState(OrderStatus.SHIPPING);
		PaymentType mockPaymentType = new PaymentType("TOSS_간편결제");
		PaymentDetail mockPaymentDetail = PaymentDetail.builder().paymentType(mockPaymentType).build();
		Payment mockPayment = Payment.builder().paymentDetail(mockPaymentDetail).build();
		Order mockOrderInShipping = Order.builder()
			.user(User.builder().id(userId).build())
			.orderKey(orderKey)
			.orderState(shippingState)
			.build();
		ReflectionTestUtils.setField(mockOrderInShipping, "payment", mockPayment);

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(mockOrderInShipping));

		// when & then
		assertThrows(OrderInvalidStateException.class, () -> {
			paymentService.cancelPayment(request, userId);
		});

		// 취소 불가능한 상태이므로, 게이트웨이 호출이나 다른 로직은 실행되지 않아야 함
		verify(tossGateway, never()).cancelPayment(any());
		verify(pointGateway, never()).cancelPayment(any());
		verify(paymentRepository, never()).save(any());
	}

	@Test
	@DisplayName("결제 취소 실패 - 사용자 불일치")
	void cancelPayment_fail_userMismatch() {
		// given
		Long orderOwnerId = 1L;
		Long requestingUserId = 2L; // 주문 소유자와 요청자를 다르게 설정
		String orderKey = "testOrderKey-user-mismatch";
		PaymentCancelRequest request = new PaymentCancelRequest(orderKey, "타인에 의한 취소 시도");

		User orderOwner = User.builder().id(orderOwnerId).build();
		OrderState preparingState = new OrderState(OrderStatus.PREPARING);
		PaymentType mockPaymentType = new PaymentType("TOSS_간편결제");
		PaymentDetail mockPaymentDetail = PaymentDetail.builder().paymentType(mockPaymentType).build();
		Payment mockPayment = Payment.builder().paymentDetail(mockPaymentDetail).build();
		Order mockOrder = Order.builder().user(orderOwner).orderKey(orderKey).build();
		ReflectionTestUtils.setField(mockOrder, "orderState", preparingState);
		ReflectionTestUtils.setField(mockOrder, "payment", mockPayment);

		when(orderRepository.findByOrderKey(orderKey)).thenReturn(Optional.of(mockOrder));

		// when & then
		// 사용자 권한이 없으므로 OrderNotFoundException 발생을 기대
		assertThrows(OrderNotFoundException.class, () -> {
			paymentService.cancelPayment(request, requestingUserId);
		});

		verify(tossGateway, never()).cancelPayment(any());
		verify(paymentRepository, never()).save(any());
		verify(orderService, never()).cancelOrderInternal(any());
	}
}