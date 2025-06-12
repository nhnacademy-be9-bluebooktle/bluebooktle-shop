package shop.bluebooktle.backend.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.bluebooktle.backend.cart.service.CartService;
import shop.bluebooktle.backend.order.service.OrderService;
import shop.bluebooktle.common.domain.auth.UserStatus;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.order.PaymentMethod;
import shop.bluebooktle.common.dto.auth.UserDto;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.request.OrderItemRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.common.dto.order.response.OrderItemResponse;
import shop.bluebooktle.common.entity.auth.User;
import shop.bluebooktle.common.security.AuthUserLoader;
import shop.bluebooktle.common.security.UserPrincipal;
import shop.bluebooktle.common.util.JwtUtil;

@ActiveProfiles("test")
@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private CartService cartService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private JwtUtil jwtUtil;

	@MockitoBean
	private AuthUserLoader authUserLoader;

	private UserPrincipal setupUserPrincipal(Long userId, String loginId, UserType userType) {
		UserDto userDto = UserDto.builder()
			.id(userId)
			.loginId(loginId)
			.nickname("testnickname")
			.type(userType)
			.status(UserStatus.ACTIVE)
			.build();
		return new UserPrincipal(userDto);
	}

	@Test
	@DisplayName("주문 확인 및 결제 정보 조회 (orderId) - 로그인 사용자 성공")
	void getOrderConfirmationDetails_byOrderId_loggedInUser_success() throws Exception {
		Long orderId = 1L;
		Long userId = 10L;
		UserPrincipal userPrincipal = setupUserPrincipal(userId, "testuser", UserType.USER);

		OrderConfirmDetailResponse response = OrderConfirmDetailResponse.builder()
			.orderId(orderId)
			.orderName("테스트 주문명")
			.orderKey("ORD-123")
			.receiverName("수령인 홍길동")
			.receiverPhoneNumber("010-1111-2222")
			.ordererName("주문자 김철수")
			.ordererPhoneNumber("010-3333-4444")
			.address("서울시 강남구")
			.detailAddress("역삼동 123-45")
			.postalCode("06130")
			.deliveryFee(new BigDecimal("3000"))
			.orderItems(List.of(
				OrderItemResponse.builder()
					.bookOrderId(101L)
					.bookId(201L)
					.bookTitle("테스트 책 1")
					.bookThumbnailUrl("http://example.com/thumb1.jpg")
					.quantity(1)
					.price(new BigDecimal("10000"))
					.packagingOptions(Collections.emptyList())
					.appliedItemCoupons(Collections.emptyList())
					.finalItemPrice(new BigDecimal("10000"))
					.build()
			))
			.appliedCoupons(Collections.emptyList())
			.userPointBalance(new BigDecimal("5000"))
			.subTotal(new BigDecimal("10000"))
			.packagingTotal(new BigDecimal("0"))
			.couponDiscountTotal(new BigDecimal("0"))
			.totalAmount(new BigDecimal("13000"))
			.pointUseAmount(new BigDecimal("0"))
			.paymentKey("test_payment_key_123")
			.paymentMethod("CARD")
			.paidAmount(new BigDecimal("13000"))
			.paidAt(LocalDateTime.now())
			.build();

		given(orderService.getOrderById(eq(orderId), eq(userId))).willReturn(response);

		mockMvc.perform(get("/api/orders/{orderId}", orderId)
				.with(principal(
					new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderKey").value("ORD-123"))
			.andExpect(jsonPath("$.data.orderName").value("테스트 주문명"))
			.andExpect(jsonPath("$.data.orderItems[0].bookTitle").value("테스트 책 1"));

		verify(orderService).getOrderById(eq(orderId), eq(userId));
	}

	@Test
	@DisplayName("주문 확인 및 결제 정보 조회 (orderId) - 비로그인 사용자 성공")
	void getOrderConfirmationDetails_byOrderId_nonLoggedInUser_success() throws Exception {
		Long orderId = 1L;

		OrderConfirmDetailResponse response = OrderConfirmDetailResponse.builder()
			.orderId(orderId)
			.orderName("비회원 주문명")
			.orderKey("ORD-123-GUEST")
			.receiverName("수령인 비회원")
			.receiverPhoneNumber("010-9999-8888")
			.ordererName("주문자 비회원")
			.ordererPhoneNumber("010-7777-6666")
			.address("경기도 성남시")
			.detailAddress("분당구 판교동")
			.postalCode("13487")
			.deliveryFee(new BigDecimal("3000"))
			.orderItems(List.of(
				OrderItemResponse.builder()
					.bookOrderId(102L)
					.bookId(202L)
					.bookTitle("비회원 책")
					.bookThumbnailUrl("http://example.com/thumb2.jpg")
					.quantity(1)
					.price(new BigDecimal("8000"))
					.packagingOptions(Collections.emptyList())
					.appliedItemCoupons(Collections.emptyList())
					.finalItemPrice(new BigDecimal("8000"))
					.build()
			))
			.appliedCoupons(Collections.emptyList())
			.userPointBalance(new BigDecimal("0"))
			.subTotal(new BigDecimal("8000"))
			.packagingTotal(new BigDecimal("0"))
			.couponDiscountTotal(new BigDecimal("0"))
			.totalAmount(new BigDecimal("11000"))
			.pointUseAmount(new BigDecimal("0"))
			.paymentKey("guest_payment_key_456")
			.paymentMethod("BANK_TRANSFER")
			.paidAmount(new BigDecimal("11000"))
			.paidAt(LocalDateTime.now())
			.build();

		given(orderService.getOrderById(eq(orderId), isNull())).willReturn(response);

		mockMvc.perform(get("/api/orders/{orderId}", orderId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderKey").value("ORD-123-GUEST"))
			.andExpect(jsonPath("$.data.orderName").value("비회원 주문명"))
			.andExpect(jsonPath("$.data.orderItems[0].bookTitle").value("비회원 책"));

		verify(orderService).getOrderById(eq(orderId), isNull());
	}

	@Test
	@DisplayName("주문 확인 및 결제 정보 조회 (orderKey) - 로그인 사용자 성공")
	void getOrderConfirmationDetails_byOrderKey_loggedInUser_success() throws Exception {
		String orderKey = "ORD-ABC";
		Long userId = 10L;
		UserPrincipal userPrincipal = setupUserPrincipal(userId, "testuser", UserType.USER);

		OrderConfirmDetailResponse response = OrderConfirmDetailResponse.builder()
			.orderId(2L)
			.orderName("키로 조회 주문")
			.orderKey(orderKey)
			.receiverName("수령인 이순신")
			.receiverPhoneNumber("010-5555-6666")
			.ordererName("주문자 강감찬")
			.ordererPhoneNumber("010-7777-8888")
			.address("부산시 해운대구")
			.detailAddress("마린시티")
			.postalCode("48058")
			.deliveryFee(new BigDecimal("2500"))
			.orderItems(List.of(
				OrderItemResponse.builder()
					.bookOrderId(103L)
					.bookId(203L)
					.bookTitle("테스트 책 2")
					.bookThumbnailUrl("http://example.com/thumb3.jpg")
					.quantity(1)
					.price(new BigDecimal("20000"))
					.packagingOptions(Collections.emptyList())
					.appliedItemCoupons(Collections.emptyList())
					.finalItemPrice(new BigDecimal("20000"))
					.build()
			))
			.appliedCoupons(Collections.emptyList())
			.userPointBalance(new BigDecimal("10000"))
			.subTotal(new BigDecimal("20000"))
			.packagingTotal(new BigDecimal("0"))
			.couponDiscountTotal(new BigDecimal("0"))
			.totalAmount(new BigDecimal("22500"))
			.pointUseAmount(new BigDecimal("0"))
			.paymentKey("test_payment_key_789")
			.paymentMethod("KAKAO_PAY")
			.paidAmount(new BigDecimal("22500"))
			.paidAt(LocalDateTime.now())
			.build();

		given(orderService.getOrderByKey(eq(orderKey), eq(userId))).willReturn(response);

		mockMvc.perform(get("/api/orders/key/{orderKey}", orderKey)
				.with(principal(
					new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderKey").value("ORD-ABC"))
			.andExpect(jsonPath("$.data.orderName").value("키로 조회 주문"))
			.andExpect(jsonPath("$.data.orderItems[0].bookTitle").value("테스트 책 2"));

		verify(orderService).getOrderByKey(eq(orderKey), eq(userId));
	}

	@Test
	@DisplayName("주문 확인 및 결제 정보 조회 (orderKey) - 비로그인 사용자 성공")
	void getOrderConfirmationDetails_byOrderKey_nonLoggedInUser_success() throws Exception {
		String orderKey = "ORD-ABC-GUEST";

		OrderConfirmDetailResponse response = OrderConfirmDetailResponse.builder()
			.orderId(2L)
			.orderName("비회원 키 조회 주문")
			.orderKey(orderKey)
			.receiverName("수령인 비회원2")
			.receiverPhoneNumber("010-1010-2020")
			.ordererName("주문자 비회원2")
			.ordererPhoneNumber("010-3030-4040")
			.address("대구시 중구")
			.detailAddress("동성로")
			.postalCode("41908")
			.deliveryFee(new BigDecimal("2500"))
			.orderItems(List.of(
				OrderItemResponse.builder()
					.bookOrderId(104L)
					.bookId(204L)
					.bookTitle("비회원 책 2")
					.bookThumbnailUrl("http://example.com/thumb4.jpg")
					.quantity(1)
					.price(new BigDecimal("12000"))
					.packagingOptions(Collections.emptyList())
					.appliedItemCoupons(Collections.emptyList())
					.finalItemPrice(new BigDecimal("12000"))
					.build()
			))
			.appliedCoupons(Collections.emptyList())
			.userPointBalance(new BigDecimal("0"))
			.subTotal(new BigDecimal("12000"))
			.packagingTotal(new BigDecimal("0"))
			.couponDiscountTotal(new BigDecimal("0"))
			.totalAmount(new BigDecimal("14500"))
			.pointUseAmount(new BigDecimal("0"))
			.paymentKey("guest_payment_key_888")
			.paymentMethod("CREDIT_CARD")
			.paidAmount(new BigDecimal("14500"))
			.paidAt(LocalDateTime.now())
			.build();

		given(orderService.getOrderByKey(eq(orderKey), isNull())).willReturn(response);

		mockMvc.perform(get("/api/orders/key/{orderKey}", orderKey))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderKey").value("ORD-ABC-GUEST"))
			.andExpect(jsonPath("$.data.orderName").value("비회원 키 조회 주문"))
			.andExpect(jsonPath("$.data.orderItems[0].bookTitle").value("비회원 책 2"));

		verify(orderService).getOrderByKey(eq(orderKey), isNull());
	}

	@Test
	@DisplayName("주문 생성 - 로그인 사용자 성공")
	@WithMockUser(roles = "USER")
	void createOrder_loggedInUser_success() throws Exception {
		Long userId = 10L;
		UserPrincipal userPrincipal = setupUserPrincipal(userId, "testuser", UserType.USER);

		// OrderItemRequest DTO의 변경사항 반영
		List<OrderItemRequest> orderItemRequests = List.of(
			new OrderItemRequest(1L, 2, new BigDecimal("10000"), null, null, null)
			// bookId, bookQuantity, salePrice, packagingOptionId, packagingQuantity, itemCouponId
		);

		OrderCreateRequest request = new OrderCreateRequest(
			"수령인", "010-1111-2222", "user@example.com", "12345", "서울시", "강남구", "문앞에 놔주세요",
			orderItemRequests, // 수정된 OrderItemRequest 리스트
			PaymentMethod.CARD, new BigDecimal("15000"), new BigDecimal("3000"), new BigDecimal("18000"),
			null, null, null, null, null
		);

		given(orderService.createOrder(any(OrderCreateRequest.class))).willReturn(1L);
		given(cartService.findUserEntityById(eq(userId))).willReturn(
			User.builder().id(userId).loginId("testuser").build());
		doNothing().when(cartService).removeSelectedBooksFromUserCart(any(User.class), anyList());

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(
					principal(new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value(1L));

		verify(orderService).createOrder(any(OrderCreateRequest.class));
		verify(cartService).removeSelectedBooksFromUserCart(any(User.class), anyList());
	}

	@Test
	@DisplayName("주문 생성 - 비로그인 사용자 성공")
	void createOrder_nonLoggedInUser_success() throws Exception {
		String guestId = "testGuest123";
		// OrderItemRequest DTO의 변경사항 반영
		List<OrderItemRequest> orderItemRequests = List.of(
			new OrderItemRequest(1L, 2, new BigDecimal("8000"), null, null, null)
			// bookId, bookQuantity, salePrice, packagingOptionId, packagingQuantity, itemCouponId
		);

		OrderCreateRequest request = new OrderCreateRequest(
			"수령인", "010-1111-2222", "guest@example.com", "12345", "서울시", "강남구", "문앞에 놔주세요",
			orderItemRequests, // 수정된 OrderItemRequest 리스트
			PaymentMethod.BANK_TRANSFER, new BigDecimal("10000"), new BigDecimal("2500"), new BigDecimal("12500"),
			null, null, null, null, null
		);

		given(orderService.createOrder(any(OrderCreateRequest.class))).willReturn(2L);
		doNothing().when(cartService).removeSelectedBooksFromGuestCart(eq(guestId), anyList());

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.header("GUEST_ID", guestId)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value(2L));

		verify(orderService).createOrder(any(OrderCreateRequest.class));
		verify(cartService).removeSelectedBooksFromGuestCart(eq(guestId), anyList());
	}

	@Test
	@DisplayName("내 주문 전체 조회 - 로그인 사용자 성공")
	@WithMockUser(roles = "USER")
	void getOrderHistory_loggedInUser_success() throws Exception {
		Long userId = 10L;
		UserPrincipal userPrincipal = setupUserPrincipal(userId, "testuser", UserType.USER);

		OrderHistoryResponse historyResponse = new OrderHistoryResponse(
			1L, "ORD-HISTORY-001", OrderStatus.COMPLETED, LocalDateTime.now(), new BigDecimal("50000"), "상품명 외 1개",
			List.of(OrderItemResponse.builder()
				.bookTitle("Sample Book")
				.quantity(1)
				.finalItemPrice(new BigDecimal("50000"))
				.build())
		);
		Page<OrderHistoryResponse> page = new PageImpl<>(List.of(historyResponse), PageRequest.of(0, 10), 1);

		given(orderService.getUserOrders(eq(userId), isNull(), any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/api/orders/history")
				.with(principal(
					new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.content[0].orderCode").value("ORD-HISTORY-001"));

		verify(orderService).getUserOrders(eq(userId), isNull(), any(Pageable.class));
	}

	@Test
	@DisplayName("내 주문 전체 조회 - 비로그인 사용자 실패 (401)")
	void getOrderHistory_nonLoggedInUser_failure() throws Exception {
		mockMvc.perform(get("/api/orders/history"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("주문 취소 - 로그인 사용자 성공")
	@WithMockUser(roles = "USER")
	void cancelOrder_loggedInUser_success() throws Exception {
		String orderKey = "CANCEL-ORD-123";
		Long userId = 10L;
		UserPrincipal userPrincipal = setupUserPrincipal(userId, "testuser", UserType.USER);

		doNothing().when(orderService).cancelOrderMember(eq(orderKey), eq(userId));

		mockMvc.perform(post("/api/orders/{orderKey}/cancel", orderKey)
				.with(
					principal(new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities())))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(orderService).cancelOrderMember(eq(orderKey), eq(userId));
	}

	@Test
	@DisplayName("주문 취소 - 비로그인 사용자 성공")
	void cancelOrder_nonLoggedInUser_success() throws Exception {
		String orderKey = "CANCEL-GUEST-456";

		doNothing().when(orderService).cancelOrderNonMember(eq(orderKey));

		mockMvc.perform(post("/api/orders/{orderKey}/cancel", orderKey)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"));

		verify(orderService).cancelOrderNonMember(eq(orderKey));
	}

	@Test
	@DisplayName("주문 상세 조회 - 로그인 사용자 성공")
	@WithMockUser(roles = "USER")
	void getMemberOrderDetail_loggedInUser_success() throws Exception {
		String orderKey = "DETAIL-ORD-789";
		Long userId = 10L;
		UserPrincipal userPrincipal = setupUserPrincipal(userId, "testuser", UserType.USER);

		OrderDetailResponse detailResponse = new OrderDetailResponse(
			3L, // orderId
			orderKey, // orderKey
			LocalDateTime.now(), // orderDate
			"주문자 로그인 사용자", // ordererName
			new BigDecimal("25000"), // paidAmount
			"CARD", // paidMethod
			OrderStatus.COMPLETED, // orderStatus
			"수령인 로그인 사용자", // receiverName
			"010-1234-5678", // receiverPhoneNumber
			"receiver@example.com", // receiverEmail
			"서울시 강서구", // address
			"화곡동 123", // detailAddress
			"07789", // postalCode
			List.of(
				OrderItemResponse.builder()
					.bookOrderId(201L)
					.bookId(301L)
					.bookTitle("상세 조회 책 1")
					.bookThumbnailUrl("http://example.com/detail_thumb1.jpg")
					.quantity(1)
					.price(new BigDecimal("20000"))
					.packagingOptions(Collections.emptyList())
					.appliedItemCoupons(Collections.emptyList())
					.finalItemPrice(new BigDecimal("20000"))
					.build()
			), // orderItemResponses
			new BigDecimal("20000"), // originalAmount
			new BigDecimal("0"), // pointUserAmount
			new BigDecimal("0"), // couponDiscountAmount
			new BigDecimal("2500"), // deliveryFee
			"TRACK123456", // trackingNumber
			new BigDecimal("0") // totalPackagingFee
		);

		given(orderService.getOrderDetailByUserId(eq(orderKey), eq(userId))).willReturn(detailResponse);

		mockMvc.perform(get("/api/orders/{orderKey}/detail", orderKey)
				.with(principal(
					new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderKey").value(orderKey))
			.andExpect(jsonPath("$.data.ordererName").value("주문자 로그인 사용자"))
			.andExpect(jsonPath("$.data.orderItemResponses").isArray())
			.andExpect(jsonPath("$.data.orderItemResponses[0].bookTitle").value("상세 조회 책 1"));

		verify(orderService).getOrderDetailByUserId(eq(orderKey), eq(userId));
	}

	@Test
	@DisplayName("주문 상세 조회 - 비로그인 사용자 성공")
	void getMemberOrderDetail_nonLoggedInUser_success() throws Exception {
		String orderKey = "DETAIL-GUEST-101";

		OrderDetailResponse detailResponse = new OrderDetailResponse(
			4L, // orderId
			orderKey, // orderKey
			LocalDateTime.now(), // orderDate
			"주문자 비회원", // ordererName
			new BigDecimal("15000"), // paidAmount
			"BANK_TRANSFER", // paidMethod
			OrderStatus.PENDING, // orderStatus
			"수령인 비회원", // receiverName
			"010-9876-5432", // receiverPhoneNumber
			"guest@example.com", // receiverEmail
			"제주도 제주시", // address
			"첨단로 333", // detailAddress
			"63309", // postalCode
			List.of(
				OrderItemResponse.builder()
					.bookOrderId(202L)
					.bookId(302L)
					.bookTitle("비회원 상세 책")
					.bookThumbnailUrl("http://example.com/detail_thumb2.jpg")
					.quantity(1)
					.price(new BigDecimal("12000"))
					.packagingOptions(Collections.emptyList())
					.appliedItemCoupons(Collections.emptyList())
					.finalItemPrice(new BigDecimal("12000"))
					.build()
			), // orderItemResponses
			new BigDecimal("12000"), // originalAmount
			new BigDecimal("0"), // pointUserAmount
			new BigDecimal("0"), // couponDiscountAmount
			new BigDecimal("3000"), // deliveryFee
			null, // trackingNumber
			new BigDecimal("0") // totalPackagingFee
		);

		given(orderService.getOrderDetailByUserId(eq(orderKey), isNull())).willReturn(detailResponse);

		mockMvc.perform(get("/api/orders/{orderKey}/detail", orderKey))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderKey").value(orderKey))
			.andExpect(jsonPath("$.data.ordererName").value("주문자 비회원"))
			.andExpect(jsonPath("$.data.orderItemResponses").isArray())
			.andExpect(jsonPath("$.data.orderItemResponses[0].bookTitle").value("비회원 상세 책"));

		verify(orderService).getOrderDetailByUserId(eq(orderKey), isNull());
	}
}