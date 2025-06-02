package shop.bluebooktle.frontend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.domain.coupon.CouponTypeTarget;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.frontend.service.AdminPackagingOptionService;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.DeliveryRuleService;
import shop.bluebooktle.frontend.service.OrderService;
import shop.bluebooktle.frontend.service.PaymentsService;
import shop.bluebooktle.frontend.service.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

	@Value("{toss.client-key}")
	private String tossPaymentClientKey;

	private final PaymentsService paymentsService;
	private final UserService userService;
	private final AdminPackagingOptionService adminPackagingOptionService;
	private final DeliveryRuleService deliveryRuleService;
	private final OrderService orderService;
	private final BookService bookService;
	private final CartService cartService;

	@GetMapping("/create")
	public ModelAndView createPage(
		@RequestParam(required = false) Long bookId,
		@RequestParam(defaultValue = "1", required = false) Integer quantity,
		@RequestParam(required = false) List<Long> bookIds,
		@CookieValue(value = "GUEST_ID", required = false) String guestId
	) {
		ModelAndView mav = new ModelAndView("order/create_form");

		UserWithAddressResponse user = userService.getUserWithAddresses();
		mav.addObject("user", user);

		List<BookCartOrderResponse> bookItems;
		if (bookId != null) {
			BookCartOrderResponse bookInfo = bookService.getBookCartOrder(bookId, quantity);
			bookItems = List.of(bookInfo);
		} else {
			bookItems = cartService.getSelectedCartItemsForOrder(guestId, bookIds);
		}
		mav.addObject("bookItems", bookItems);

		String defaultOrderName;
		if (bookItems.size() == 1) {
			defaultOrderName = bookItems.getFirst().title();
		} else {
			defaultOrderName = bookItems.getFirst().title()
				+ " 외 "
				+ (bookItems.size() - 1)
				+ "권";
		}
		mav.addObject("defaultOrderName", defaultOrderName);

		Page<PackagingOptionInfoResponse> page = adminPackagingOptionService.getPackagingOptions(0, 20, null);
		List<PackagingOptionInfoResponse> packagingOptions = page.getContent();
		mav.addObject("packagingOptions", packagingOptions);

		List<CouponResponse> orderCoupons = createMockOrderCoupons();
		mav.addObject("coupons", orderCoupons);

		DeliveryRuleResponse deliveryRule = deliveryRuleService.getDefaultDeliveryRule();
		mav.addObject("deliveryRule", deliveryRule);

		return mav;
	}

	@PostMapping("/create")
	public String createOrder(@ModelAttribute OrderCreateRequest request) {
		Long orderId = orderService.createOrder(request);
		log.info("주문 생성 :{}", orderId);
		return "redirect:/order/" + orderId + "/checkout";
	}

	@GetMapping("/{orderId}/checkout")
	public String finalConfirmationAndCheckoutPage(
		@PathVariable Long orderId,
		Model model,
		HttpServletRequest request
	) {
		OrderConfirmDetailResponse orderDetails = orderService.getOrderConfirmDetail(orderId);
		model.addAttribute("order", orderDetails);

		UserWithAddressResponse currentUser = userService.getUserWithAddresses();
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("userPointBalance", orderDetails.getUserPointBalance());

		model.addAttribute("tossClientKey", tossPaymentClientKey);
		model.addAttribute("orderIdForPayment", orderDetails.getOrderId().toString());
		model.addAttribute("orderNameForPayment", orderDetails.getOrderName());

		model.addAttribute("customerNameForPayment", orderDetails.getOrdererName());

		String baseUrl = String.format("%s://%s:%d", request.getScheme(), request.getServerName(),
			request.getServerPort());
		model.addAttribute("successUrl", baseUrl + "/order/process");
		model.addAttribute("failUrl", baseUrl + "/order/fail");

		model.addAttribute("initialSubTotal", orderDetails.getSubTotal());
		model.addAttribute("initialPackagingTotal", orderDetails.getPackagingTotal());
		model.addAttribute("initialCouponDiscountTotal", orderDetails.getCouponDiscountTotal());
		model.addAttribute("initialDeliveryFee", orderDetails.getDeliveryFee());

		return "order/checkout";
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
			),
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
			),
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
		);
	}
}