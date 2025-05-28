package shop.bluebooktle.frontend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.domain.CouponTypeTarget;
import shop.bluebooktle.common.dto.book.BookSaleInfoState;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.frontend.service.AdminPackagingOptionService;
import shop.bluebooktle.frontend.service.PaymentsService;
import shop.bluebooktle.frontend.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

	private final PaymentsService paymentsService;
	private final UserService userService;
	private final AdminPackagingOptionService adminPackagingOptionService;

	@Value("${toss.client-key}")
	private String clientKey;

	@GetMapping("/checkout")
	public ModelAndView checkoutPage() {
		ModelAndView mav = new ModelAndView("order/checkout");

		// 1) 가짜 주문 항목 (BookCartOrderResponse) 들
		List<BookCartOrderResponse> cartItems = List.of(
			BookCartOrderResponse.builder()
				.id(101L)
				.title("코딩 대모험")
				.price(new BigDecimal("28000"))
				.salePrice(new BigDecimal("25200"))
				.stock(10)
				.salePercentage(new BigDecimal("10"))
				.thumbnailUrl("https://picsum.photos/70/105?random=101")
				.categories(List.of("판타지", "어드벤처"))
				.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
				.build()
				.withQuantity(1),    // withQuantity() 는 아래 유틸 메서드 참고
			BookCartOrderResponse.builder()
				.id(102L)
				.title("픽셀 아트로 배우는 알고리즘")
				.price(new BigDecimal("35000"))
				.salePrice(new BigDecimal("31500"))
				.stock(5)
				.salePercentage(new BigDecimal("10"))
				.thumbnailUrl("https://picsum.photos/70/105?random=102")
				.categories(List.of("교육", "IT"))
				.bookSaleInfoState(BookSaleInfoState.AVAILABLE)
				.build()
				.withQuantity(2)
		);
		mav.addObject("cartItems", cartItems);
		
		Page<PackagingOptionInfoResponse> page = adminPackagingOptionService.getPackagingOptions(0, 20, null);
		List<PackagingOptionInfoResponse> packagingOptions = page.getContent();
		mav.addObject("packagingOptions", packagingOptions);
		mav.addObject("packagingOptions", packagingOptions);

		Map<Long, List<CouponResponse>> itemCoupons = createMockItemCoupons();
		mav.addObject("itemCoupons", itemCoupons);

		List<CouponResponse> orderCoupons = createMockOrderCoupons();
		mav.addObject("orderCoupons", orderCoupons);

		mav.addObject("availablePoints", 3500);

		mav.addObject("checkoutRequest", new CheckoutRequest());

		// 토스페이먼츠용 공통값
		mav.addObject("clientKey", clientKey);
		mav.addObject("orderId", UUID.randomUUID().toString());
		mav.addObject("orderName", "테스트 주문");
		mav.addObject("successUrl", "/order/complete");
		mav.addObject("failUrl", "/order/fail");
		mav.addObject("customerEmail", "test@fake.com");
		mav.addObject("customerName", "테스트유저");
		mav.addObject("amount", 0); // JS에서 계산 후 동적으로 세팅

		return mav;
	}

	@GetMapping("/process")
	public String processOrder(
		@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam Integer amount,
		RedirectAttributes redirectAttributes
	) {
		PaymentConfirmRequest req = new PaymentConfirmRequest(paymentKey, orderId, amount);
		PaymentConfirmResponse resp = paymentsService.confirm(req);
		redirectAttributes.addFlashAttribute("orderData", resp);
		return "redirect:/order/complete";
	}

	@GetMapping("/complete")
	public String orderCompletePage(@Valid @ModelAttribute("orderData") PaymentConfirmResponse data,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "order/fail";
		}

		return "order/complete";
	}

	@GetMapping("/fail")
	public String orderFailPage() {

		return "order/fail";
	}

	private Map<Long, List<CouponResponse>> createMockItemCoupons() {
		return Map.of(
			101L, List.of(
				new CouponResponse(
					1L,
					"키치코딩 1,000원 할인",          // couponName
					CouponTypeTarget.BOOK,          // target
					"금액 할인",                     // couponTypeName
					BigDecimal.ZERO,                // minimumPayment (제한 없음)
					LocalDateTime.now().minusDays(10), // createdAt
					null,                           // categoryName (전체 카테고리)
					"코딩 대모험"                     // bookName
				),
				new CouponResponse(
					2L,
					"키치코딩 5% 할인",
					CouponTypeTarget.BOOK,
					"퍼센트 할인",
					BigDecimal.ZERO,
					LocalDateTime.now().minusDays(8),
					null,
					"코딩 대모험"
				)
			),
			102L, List.of(
				new CouponResponse(
					3L,
					"픽셀아트 전용 3,000원 할인",
					CouponTypeTarget.BOOK,
					"금액 할인",
					BigDecimal.ZERO,
					LocalDateTime.now().minusDays(5),
					"교육",                         // categoryName: 교육 카테고리 한정
					"픽셀 아트로 배우는 알고리즘"
				)
			)
		);
	}

	private List<CouponResponse> createMockOrderCoupons() {
		return List.of(
			new CouponResponse(
				10L,
				"WELCOME! 5,000원 할인",
				CouponTypeTarget.ORDER,
				"금액 할인",
				BigDecimal.valueOf(20000),         // 최소 결제액 20,000원 이상일 때
				LocalDateTime.now().minusDays(30),
				null,
				null
			),
			new CouponResponse(
				11L,
				"VIP 전용 10% 할인",
				CouponTypeTarget.ORDER,
				"퍼센트 할인",
				BigDecimal.valueOf(50000),         // 최소 결제액 50,000원 이상일 때
				LocalDateTime.now().minusDays(15),
				null,
				null
			)
		);
	}
}