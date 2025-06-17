package shop.bluebooktle.backend.order.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.request.OrderItemRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.common.dto.order.response.OrderItemResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;
import shop.bluebooktle.common.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

	private final String GUEST_ID = "testGuestId123";
	@Mock
	private OrderService orderService;
	@Mock
	private CartService cartService;
	@InjectMocks
	private OrderController orderController;
	private UserPrincipal principal;
	private User user;

	@BeforeEach
	void setUp() {
		UserDto userDto = UserDto.builder()
			.id(1L)
			.loginId("testuser")
			.nickname("테스트닉네임")
			.type(UserType.USER)
			.status(UserStatus.ACTIVE)
			.build();

		principal = new UserPrincipal(userDto);
		user = mock(User.class);
	}

	@Test
	@DisplayName("주문 확인 및 결제 정보 조회 (orderId) - 로그인 사용자 성공")
	void getOrderConfirmationDetails_byOrderId_loggedInUser_success() {
		Long orderId = 1L;
		OrderConfirmDetailResponse expectedResponse = createOrderConfirmDetailResponse(orderId, "MEMBER-ORD-123", true);

		given(orderService.getOrderById(eq(orderId), eq(principal.getUserId()))).willReturn(expectedResponse);

		ResponseEntity<JsendResponse<OrderConfirmDetailResponse>> responseEntity =
			orderController.getOrderConfirmationDetails(orderId, principal);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isEqualTo(expectedResponse);

		verify(orderService).getOrderById(eq(orderId), eq(principal.getUserId()));
	}

	@Test
	@DisplayName("주문 확인 및 결제 정보 조회 (orderId) - 비로그인 사용자 성공")
	void getOrderConfirmationDetails_byOrderId_nonLoggedInUser_success() {
		Long orderId = 1L;
		OrderConfirmDetailResponse expectedResponse = createOrderConfirmDetailResponse(orderId, "GUEST-ORD-123", false);

		given(orderService.getOrderById(eq(orderId), isNull())).willReturn(expectedResponse);

		ResponseEntity<JsendResponse<OrderConfirmDetailResponse>> responseEntity =
			orderController.getOrderConfirmationDetails(orderId, null); // principal에 null 전달

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isEqualTo(expectedResponse);

		verify(orderService).getOrderById(eq(orderId), isNull());
	}

	@Test
	@DisplayName("주문 확인 및 결제 정보 조회 (orderKey) - 로그인 사용자 성공")
	void getOrderConfirmationDetails_byOrderKey_loggedInUser_success() {
		String orderKey = "MEMBER-ORD-KEY-456";
		OrderConfirmDetailResponse expectedResponse = createOrderConfirmDetailResponse(2L, orderKey, true);

		given(orderService.getOrderByKey(eq(orderKey), eq(principal.getUserId()))).willReturn(expectedResponse);

		ResponseEntity<JsendResponse<OrderConfirmDetailResponse>> responseEntity =
			orderController.getOrderConfirmationDetails(orderKey, principal);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isEqualTo(expectedResponse);

		verify(orderService).getOrderByKey(eq(orderKey), eq(principal.getUserId()));
	}

	@Test
	@DisplayName("주문 확인 및 결제 정보 조회 (orderKey) - 비로그인 사용자 성공")
	void getOrderConfirmationDetails_byOrderKey_nonLoggedInUser_success() {
		String orderKey = "GUEST-ORD-KEY-789";
		OrderConfirmDetailResponse expectedResponse = createOrderConfirmDetailResponse(3L, orderKey, false);

		given(orderService.getOrderByKey(eq(orderKey), isNull())).willReturn(expectedResponse);

		ResponseEntity<JsendResponse<OrderConfirmDetailResponse>> responseEntity =
			orderController.getOrderConfirmationDetails(orderKey, null);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isEqualTo(expectedResponse);

		verify(orderService).getOrderByKey(eq(orderKey), isNull());
	}

	@Test
	@DisplayName("주문 생성 - 로그인 사용자 성공")
	void createOrder_loggedInUser_success() {
		Long createdOrderId = 10L;
		List<OrderItemRequest> orderItemRequests = List.of(
			new OrderItemRequest(1L, 2, BigDecimal.valueOf(10000), null, null, null));
		OrderCreateRequest request = OrderCreateRequest.builder()
			.userId(principal.getUserId())
			.orderItems(orderItemRequests)
			.build();

		given(orderService.createOrder(any(OrderCreateRequest.class))).willReturn(createdOrderId);
		given(cartService.findUserEntityById(eq(principal.getUserId()))).willReturn(user);
		doNothing().when(cartService).removeSelectedBooksFromUserCart(eq(user), anyList());

		ResponseEntity<JsendResponse<Long>> responseEntity =
			orderController.createOrder(principal, request, null);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isEqualTo(createdOrderId);

		verify(orderService).createOrder(any(OrderCreateRequest.class));
		verify(cartService).removeSelectedBooksFromUserCart(eq(user),
			eq(orderItemRequests.stream().map(OrderItemRequest::bookId).toList()));
	}

	@Test
	@DisplayName("주문 생성 - 비로그인 사용자 성공")
	void createOrder_nonLoggedInUser_success() {
		Long createdOrderId = 11L;
		List<OrderItemRequest> orderItemRequests = List.of(
			new OrderItemRequest(2L, 1, BigDecimal.valueOf(15000), null, null, null));
		OrderCreateRequest request = OrderCreateRequest.builder()
			.userId(null)
			.orderItems(orderItemRequests)
			.build();

		given(orderService.createOrder(any(OrderCreateRequest.class))).willReturn(createdOrderId);
		doNothing().when(cartService).removeSelectedBooksFromGuestCart(eq(GUEST_ID), anyList());

		ResponseEntity<JsendResponse<Long>> responseEntity =
			orderController.createOrder(null, request, GUEST_ID);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isEqualTo(createdOrderId);

		verify(orderService).createOrder(any(OrderCreateRequest.class));
		verify(cartService).removeSelectedBooksFromGuestCart(eq(GUEST_ID),
			eq(orderItemRequests.stream().map(OrderItemRequest::bookId).toList()));
	}

	@Test
	@DisplayName("내 주문 전체 조회 - 로그인 사용자 성공")
	void getOrderHistory_loggedInUser_success() {
		OrderHistoryResponse historyResponse = new OrderHistoryResponse(
			1L, LocalDateTime.now(), "히스토리 주문명", BigDecimal.valueOf(50000),
			"ORD-HISTORY-001", OrderStatus.COMPLETED, "http://example.com/thumb.jpg"
		);
		Page<OrderHistoryResponse> mockPage = new PageImpl<>(List.of(historyResponse), PageRequest.of(0, 10), 1);

		given(orderService.getUserOrders(eq(principal.getUserId()), isNull(), any(Pageable.class))).willReturn(
			mockPage);

		ResponseEntity<JsendResponse<PaginationData<OrderHistoryResponse>>> responseEntity =
			orderController.getOrderHistory(principal, null, PageRequest.of(0, 10));

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data().getContent()).hasSize(1);
		assertThat(responseEntity.getBody().data().getContent().getFirst().orderKey()).isEqualTo("ORD-HISTORY-001");

		verify(orderService).getUserOrders(eq(principal.getUserId()), isNull(), any(Pageable.class));
	}

	@Test
	@DisplayName("내 주문 전체 조회 - 비로그인 사용자 실패 (InvalidTokenException 발생)")
	void getOrderHistory_nonLoggedInUser_failure() {
		assertThatThrownBy(() -> orderController.getOrderHistory(null, null, PageRequest.of(0, 10)))
			.isInstanceOf(InvalidTokenException.class);

		verify(orderService, times(0)).getUserOrders(anyLong(), any(), any());
	}

	@Test
	@DisplayName("주문 취소 - 로그인 사용자 성공")
	void cancelOrder_loggedInUser_success() {
		String orderKey = "CANCEL-MEMBER-123";

		doNothing().when(orderService).cancelOrderMember(eq(orderKey), eq(principal.getUserId()));

		ResponseEntity<JsendResponse<Void>> responseEntity =
			orderController.cancelOrder(orderKey, principal);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");

		verify(orderService).cancelOrderMember(eq(orderKey), eq(principal.getUserId()));
	}

	@Test
	@DisplayName("주문 취소 - 비로그인 사용자 성공")
	void cancelOrder_nonLoggedInUser_success() {
		String orderKey = "CANCEL-GUEST-456";

		doNothing().when(orderService).cancelOrderNonMember(eq(orderKey));

		ResponseEntity<JsendResponse<Void>> responseEntity =
			orderController.cancelOrder(orderKey, null);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");

		verify(orderService).cancelOrderNonMember(eq(orderKey));
	}

	@Test
	@DisplayName("주문 상세 조회 - 로그인 사용자 성공")
	void getMemberOrderDetail_loggedInUser_success() {
		String orderKey = "DETAIL-MEMBER-789";
		OrderDetailResponse expectedResponse = createOrderDetailResponse(4L, orderKey, true);

		given(orderService.getOrderDetailByUserId(eq(orderKey), eq(principal.getUserId()))).willReturn(
			expectedResponse);

		ResponseEntity<JsendResponse<OrderDetailResponse>> responseEntity =
			orderController.getOrderDetail(orderKey, principal);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isEqualTo(expectedResponse);

		verify(orderService).getOrderDetailByUserId(eq(orderKey), eq(principal.getUserId()));
	}

	@Test
	@DisplayName("주문 상세 조회 - 비로그인 사용자 성공")
	void getMemberOrderDetail_nonLoggedInUser_success() {
		String orderKey = "DETAIL-GUEST-101";
		OrderDetailResponse expectedResponse = createOrderDetailResponse(5L, orderKey, false);

		given(orderService.getOrderDetailByUserId(eq(orderKey), isNull())).willReturn(expectedResponse);

		ResponseEntity<JsendResponse<OrderDetailResponse>> responseEntity =
			orderController.getOrderDetail(orderKey, null);

		assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
		assertThat(responseEntity.getBody()).isNotNull();
		assertThat(responseEntity.getBody().status()).isEqualTo("success");
		assertThat(responseEntity.getBody().data()).isEqualTo(expectedResponse);

		verify(orderService).getOrderDetailByUserId(eq(orderKey), isNull());
	}

	private OrderConfirmDetailResponse createOrderConfirmDetailResponse(Long orderId, String orderKey,
		boolean isMember) {
		return OrderConfirmDetailResponse.builder()
			.orderId(orderId)
			.orderName("테스트 주문명 " + orderId)
			.orderKey(orderKey)
			.receiverName("수령인 " + orderId)
			.receiverPhoneNumber("010-1111-" + String.format("%04d", orderId))
			.ordererName("주문자 " + (isMember ? "회원" : "비회원"))
			.ordererPhoneNumber("010-2222-" + String.format("%04d", orderId))
			.address("테스트 주소")
			.detailAddress("상세 주소")
			.postalCode("12345")
			.deliveryFee(new BigDecimal("3000"))
			.orderItems(List.of(
				OrderItemResponse.builder()
					.bookOrderId(100L + orderId)
					.bookId(200L + orderId)
					.bookTitle("테스트 책 " + orderId)
					.bookThumbnailUrl("http://example.com/thumb" + orderId + ".jpg")
					.quantity(1)
					.price(new BigDecimal("10000"))
					.packagingOptions(Collections.emptyList())
					.appliedItemCoupons(Collections.emptyList())
					.finalItemPrice(new BigDecimal("10000"))
					.build()
			))
			.appliedCoupons(Collections.emptyList())
			.userPointBalance(isMember ? new BigDecimal("5000") : BigDecimal.ZERO)
			.subTotal(new BigDecimal("10000"))
			.packagingTotal(BigDecimal.ZERO)
			.couponDiscountTotal(BigDecimal.ZERO)
			.totalAmount(new BigDecimal("13000"))
			.pointUseAmount(BigDecimal.ZERO)
			.paymentKey("payment_key_" + orderId)
			.paymentMethod("CARD")
			.paidAmount(new BigDecimal("13000"))
			.paidAt(LocalDateTime.now())
			.build();
	}

	private OrderDetailResponse createOrderDetailResponse(Long orderId, String orderKey, boolean isMember) {
		return new OrderDetailResponse(
			orderId,
			orderKey,
			LocalDateTime.now(),
			"주문자 " + (isMember ? "회원" : "비회원"),
			new BigDecimal("25000"),
			"CARD",
			OrderStatus.COMPLETED,
			"수령인 " + (isMember ? "회원" : "비회원"),
			"010-1234-5678",
			"test@" + (isMember ? "member" : "guest") + ".com",
			"테스트 주소",
			"상세 주소",
			"12345",
			List.of(
				OrderItemResponse.builder()
					.bookOrderId(200L + orderId)
					.bookId(300L + orderId)
					.bookTitle("상세 조회 책 " + orderId)
					.bookThumbnailUrl("http://example.com/detail_thumb" + orderId + ".jpg")
					.quantity(1)
					.price(new BigDecimal("20000"))
					.packagingOptions(Collections.emptyList())
					.appliedItemCoupons(Collections.emptyList())
					.finalItemPrice(new BigDecimal("20000"))
					.build()
			),
			new BigDecimal("20000"),
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			new BigDecimal("2500"),
			"TRACK" + orderId,
			BigDecimal.ZERO
		);
	}
}