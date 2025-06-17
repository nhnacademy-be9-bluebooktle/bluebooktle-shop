package shop.bluebooktle.frontend.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.domain.order.Region;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.request.OrderItemRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.frontend.config.advice.GlobalUserInfoAdvice;
import shop.bluebooktle.frontend.service.AdminPackagingOptionService;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CategoryService;
import shop.bluebooktle.frontend.service.CouponService;
import shop.bluebooktle.frontend.service.DeliveryRuleService;
import shop.bluebooktle.frontend.service.OrderService;
import shop.bluebooktle.frontend.service.PaymentsService;
import shop.bluebooktle.frontend.service.UserService;

@WebMvcTest(
	controllers = OrderController.class,
	excludeFilters = @ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE,
		classes = GlobalUserInfoAdvice.class
	)
)
@TestPropertySource(properties = {
	"server.gateway-url=http://localhost:8080",
	"minio.endpoint=http://localhost:9000", // 더미값
	"toss.client-key=testClientKey"
})
@Import(RefreshAutoConfiguration.class)
@ActiveProfiles("test")
public class OrderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	private CouponService couponService;
	@MockitoBean
	private CartService cartService;
	@MockitoBean
	private CategoryService categoryService;
	@MockitoBean
	private PaymentsService paymentsService;
	@MockitoBean
	private UserService userService;
	@MockitoBean
	private AdminPackagingOptionService adminPackagingOptionService;
	@MockitoBean
	private DeliveryRuleService deliveryRuleService;
	@MockitoBean
	private OrderService orderService;
	@MockitoBean
	private BookService bookService;

	@Test
	@DisplayName("GET /order/create?bookId=... 단일 도서 주문 폼 (미로그인)")
	void createPage_singleBook_notLoggedIn() throws Exception {
		// given
		given(bookService.getBookCartOrder(10L, 2))
			.willReturn(new BookCartOrderResponse(
				10L,
				"제목",
				BigDecimal.valueOf(2000),
				BigDecimal.valueOf(1000),
				null,
				List.of(),
				false,
				2
			));
		given(adminPackagingOptionService.getPackagingOptions(0, 20, null))
			.willReturn(new PageImpl<>(List.of(
				new PackagingOptionInfoResponse(
					1L,
					"박스",
					BigDecimal.valueOf(500)   // price
				)
			)));
		given(deliveryRuleService.getDefaultDeliveryRule())
			.willReturn(new DeliveryRuleResponse(
				1L,
				"기본 배송",
				BigDecimal.valueOf(3000),
				BigDecimal.valueOf(10000),
				Region.ALL,
				true
			));

		// when / then
		mockMvc.perform(get("/order/create")
				.param("bookId","10")
				.param("quantity","2")
				.requestAttr("isLoggedIn", false)
			)
			.andExpect(status().isOk())
			.andExpect(view().name("order/create_form"))
			.andExpect(model().attributeExists("bookItems"))
			.andExpect(model().attribute("defaultOrderName", "제목"))
			.andExpect(model().attributeExists("packagingOptions"))
			.andExpect(model().attributeExists("deliveryRule"))
			.andExpect(model().attributeExists("coupons"));

	}

	@Test
	@DisplayName("GET /order/create (장바구니 주문 폼, 로그인) – 실제론 비회원 분기)")
	void createPage_cartLoggedIn() throws Exception {
		// given
		var bookIds = List.of(11L, 22L);
		var items = List.of(
			new BookCartOrderResponse(
				11L,                            // bookId
				"A",                            // title
				BigDecimal.valueOf(1),          // price
				BigDecimal.valueOf(100),        // salePrice
				null,                           // thumbnailUrl
				List.of(),                      // categories
				false,                          // isPackable
				1                               // quantity
			),
			new BookCartOrderResponse(
				22L,
				"B",
				BigDecimal.valueOf(1),
				BigDecimal.valueOf(200),
				null,
				List.of(),
				false,
				1
			)
		);
		given(cartService.getSelectedCartItemsForOrder("guest123", bookIds))
			.willReturn(items);

		given(adminPackagingOptionService.getPackagingOptions(0, 20, null))
			.willReturn(new PageImpl<>(List.of()));

		given(deliveryRuleService.getDefaultDeliveryRule())
			.willReturn(new DeliveryRuleResponse(
				1L,
				"기본 배송",
				BigDecimal.valueOf(3000),
				BigDecimal.ZERO,
				Region.ALL,
				true
			));

		given(userService.getUserWithAddresses())
			.willReturn(new UserWithAddressResponse(
				1L,
				"tester",
				"test@example.com",
				"010-1234-5678",
				BigDecimal.valueOf(500),
				List.of()
			));

		UsableUserCouponMapResponse coupons = new UsableUserCouponMapResponse();
		coupons.setUsableUserCouponMap(Map.of());
		given(couponService.getUsableCouponsForOrder(bookIds))
			.willReturn(coupons);

		// when / then
		mockMvc.perform(get("/order/create")
				.param("bookIds", "11","22")
				.requestAttr("isLoggedIn", true)
				.cookie(new Cookie("GUEST_ID","guest123"))
			)
			.andExpect(status().isOk())
			.andExpect(view().name("order/create_form"))
			.andExpect(model().attributeExists("bookItems"))
			.andExpect(model().attributeExists("defaultOrderName"))
			.andExpect(model().attributeExists("packagingOptions"))
			.andExpect(model().attributeExists("deliveryRule"))
			.andExpect(model().attributeExists("coupons"));
	}


	@Test
	@DisplayName("POST /order/create 성공 → /order/{id}/checkout 로 리다이렉트")
	void createOrder_success() throws Exception {
		// given
		OrderItemRequest item = new OrderItemRequest(
			5L,               // bookId
			3,                // bookQuantity
			BigDecimal.valueOf(15000), // salePrice
			null,             // packagingOptionId
			null,             // packagingQuantity
			null              // itemCouponId
		);
		OrderCreateRequest raw = OrderCreateRequest.builder()
			.userId(null)
			.requestedDeliveryDate(LocalDate.now().plusDays(1))
			.orderName("테스트 주문")
			.orderItems(List.of(item))
			.deliveryFee(BigDecimal.ZERO)
			.deliveryRuleId(1L)
			.ordererName("홍길동")
			.ordererEmail("hong@example.com")
			.ordererPhoneNumber("01012345678")
			.receiverName("이영희")
			.receiverEmail("lee@example.com")
			.receiverPhoneNumber("01087654321")
			.postalCode("12345")
			.address("서울시 강남구")
			.detailAddress("역삼동 123-45")
			.couponDiscountAmount(BigDecimal.ZERO)
			.pointUseAmount(BigDecimal.ZERO)
			.saleDiscountAmount(BigDecimal.ZERO)
			.originalAmount(BigDecimal.valueOf(45000))
			.orderKey(null)
			.orderCouponId(null)
			.build();

		given(orderService.createOrder(any(OrderCreateRequest.class), eq("guest")))
			.willReturn(99L);

		// when / then
		mockMvc.perform(post("/order/create")
				.flashAttr("request", raw)
				.cookie(new Cookie("GUEST_ID", "guest"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/order/99/checkout"));
	}

	@Test
	@DisplayName("POST /order/create 실패 → 장바구니/단일 책 화면으로 리다이렉트")
	void createOrder_fail() throws Exception {
		// given
		OrderItemRequest item = new OrderItemRequest(
			7L,                                // bookId
			1,                                 // bookQuantity
			BigDecimal.valueOf(15000),         // salePrice
			null,                              // packagingOptionId
			null,                              // packagingQuantity
			null                               // itemCouponId
		);

		OrderCreateRequest raw = OrderCreateRequest.builder()
			.userId(null)
			.requestedDeliveryDate(LocalDate.now().plusDays(1))
			.orderName("테스트 주문")
			.orderItems(List.of(item))
			.deliveryFee(BigDecimal.ZERO)
			.deliveryRuleId(1L)
			.ordererName("홍길동")
			.ordererEmail("hong@example.com")
			.ordererPhoneNumber("01012345678")
			.receiverName("이영희")
			.receiverEmail("lee@example.com")
			.receiverPhoneNumber("01087654321")
			.postalCode("12345")
			.address("서울시 강남구")
			.detailAddress("역삼동 123-45")
			.couponDiscountAmount(BigDecimal.ZERO)
			.pointUseAmount(BigDecimal.ZERO)
			.saleDiscountAmount(BigDecimal.ZERO)
			.originalAmount(BigDecimal.valueOf(15000))
			.orderKey(null)
			.orderCouponId(null)
			.build();

		// guestId 가 "guest" 가 되도록 stub
		given(orderService.createOrder(any(), anyString()))
			.willThrow(new RuntimeException("oops"));

		// when / then
		mockMvc.perform(post("/order/create")
				.cookie(new Cookie("GUEST_ID", "guest"))
				.flashAttr("orderCreateRequest", raw)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/order/create?bookId=7&quantity=1"));
	}

	@Test
	@DisplayName("GET /order/{id}/checkout 정상 호출")
	void checkoutPage() throws Exception {
		// given
		OrderConfirmDetailResponse detail = OrderConfirmDetailResponse.builder()
			.orderId(5L)
			.orderKey("ORD5")
			.orderName("테스트 주문")
			.receiverName("수신자")
			.receiverPhoneNumber("010-1111-2222")
			.ordererName("주문자")
			.ordererPhoneNumber("010-3333-4444")
			.address("서울시")
			.detailAddress("강남구 역삼동 123-45")
			.postalCode("12345")
			.deliveryFee(BigDecimal.valueOf(3000))
			.orderItems(List.of())
			.appliedCoupons(List.of())
			.userPointBalance(BigDecimal.valueOf(500))
			.subTotal(BigDecimal.valueOf(10000))
			.packagingTotal(BigDecimal.ZERO)
			.couponDiscountTotal(BigDecimal.ZERO)
			.totalAmount(BigDecimal.valueOf(10000))
			.pointUseAmount(BigDecimal.ZERO)
			.paymentKey("PMTKEY")
			.paymentMethod("card")
			.paidAmount(BigDecimal.valueOf(10000))
			.paidAt(LocalDateTime.now())
			.build();

		given(orderService.getOrderConfirmDetail(5L)).willReturn(detail);

		// when / then
		mockMvc.perform(get("/order/5/checkout")
				.requestAttr("isLoggedIn", false)
				.requestAttr("scheme", "http")
				.requestAttr("serverName", "host")
				.requestAttr("serverPort", 8080)
			)
			.andExpect(status().isOk())
			.andExpect(view().name("order/checkout"))
			.andExpect(model().attribute("tossClientKey", "testClientKey"))
			.andExpect(model().attribute("orderIdForPayment", "5"));
	}

	@Test
	@DisplayName("GET /order/point/process 금액≠0 → 에러")
	void processOrder_pointFail() throws Exception {
		mockMvc.perform(get("/order/point/process")
				.param("paymentKey","p")
				.param("orderId","o")
				.param("amount","100")
				.requestAttr("isLoggedIn", false)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	@DisplayName("GET /order/pay/process 정상 결제")
	void processOrder_success() throws Exception {
		// given
		var resp = PaymentConfirmResponse.builder()
			.status("OK")
			.orderId("123")
			.totalAmount(1111)
			.build();
		given(paymentsService.confirm(any(), eq("pay")))
			.willReturn(resp);

		// when / then
		mockMvc.perform(get("/order/pay/process")
				.param("paymentKey", "p")
				.param("orderId", "123")
				.param("amount", "0")
				.requestAttr("isLoggedIn", true)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/order/complete/123"));
	}

	@Test
	@DisplayName("GET /order/pay/process 결제 실패 (비회원 분기)")
	void processOrder_exception() throws Exception {
		given(paymentsService.confirm(any(), eq("pay")))
			.willThrow(new RuntimeException("fail"));

		mockMvc.perform(get("/order/pay/process")
					.param("paymentKey", "p")
					.param("orderId", "o")
					.param("amount", "0")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));  // <-- 여기 수정
	}

	@Test
	@DisplayName("GET /order/complete/{key} 성공")
	void completePage_success() throws Exception {
		var full = OrderConfirmDetailResponse.builder()
			.orderId(123L)
			.orderKey("KEY")
			.orderName("주문명")
			.receiverName("수령인")
			.receiverPhoneNumber("01000000000")
			.ordererName("주문자")
			.ordererPhoneNumber("01011111111")
			.address("주소")
			.detailAddress("상세주소")
			.postalCode("12345")
			.deliveryFee(BigDecimal.ZERO)
			.orderItems(List.of())
			.appliedCoupons(List.of())
			.userPointBalance(BigDecimal.ZERO)
			.subTotal(BigDecimal.ZERO)
			.packagingTotal(BigDecimal.ZERO)
			.couponDiscountTotal(BigDecimal.ZERO)
			.totalAmount(BigDecimal.ZERO)
			.pointUseAmount(BigDecimal.ZERO)
			.paymentKey("PK")
			.paymentMethod("카드")
			.paidAmount(BigDecimal.ZERO)
			.paidAt(LocalDateTime.now())
			.build();

		Mockito.lenient()
			.when(orderService.getOrderByKey("KEY"))
			.thenReturn(full);

		// when / then
		mockMvc.perform(get("/order/complete/KEY")
				.requestAttr("isLoggedIn", false)
			)
			.andExpect(status().isOk())
			.andExpect(view().name("order/complete"))
			.andExpect(model().attribute("orderKey", "KEY"));
	}

	@Test @DisplayName("GET /order/complete/{key} 실패 → 홈으로")
	void completePage_fail() throws Exception {
		given(orderService.getOrderByKey("BAD"))
			.willThrow(new RuntimeException("bad"));

		mockMvc.perform(get("/order/complete/BAD")
				.requestAttr("isLoggedIn", false)
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	@DisplayName("GET /order/fail with message")
	void failPage_withMessage() throws Exception {
		mockMvc.perform(get("/order/fail")
				.param("code","C")
				.param("message","M")
			)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("mypage/orders"));
	}

	@Test
	@DisplayName("GET /order/fail without message")
	void failPage_withoutMessage() throws Exception {
		mockMvc.perform(get("/order/fail"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("mypage/orders"));
	}

	@Test
	@DisplayName("GET /order/{key} detail success")
	void detailPage_success() throws Exception {
		var dto = new OrderDetailResponse(
			1L,
			"KEY",
			LocalDateTime.now(),
			"홍길동",
			BigDecimal.ZERO,
			"카드",
			OrderStatus.PREPARING,
			"수령인",
			"010-0000-0000",
			"email@example.com",
			"서울시 강남구",
			"역삼동 123-45",
			"12345",
			List.of(),
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			"TRACK123",
			BigDecimal.ZERO
		);
		given(orderService.getOrderDetailByOrderKey("KEY")).willReturn(dto);

		mockMvc.perform(get("/order/KEY"))
			.andExpect(status().isOk())
			.andExpect(view().name("mypage/order_detail"))
			.andExpect(model().attribute("isMember", false));
	}

	@Test @DisplayName("GET /order/{key} detail fail → 홈")
	void detailPage_fail() throws Exception {
		given(orderService.getOrderDetailByOrderKey("BAD"))
			.willThrow(new RuntimeException("nope"));

		mockMvc.perform(get("/order/BAD"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

}
