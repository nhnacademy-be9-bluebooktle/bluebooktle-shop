package shop.bluebooktle.frontend.controller;

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
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
import shop.bluebooktle.common.exception.cart.GuestUserNotFoundException;
import shop.bluebooktle.frontend.service.AdminPackagingOptionService;
import shop.bluebooktle.frontend.service.BookService;
import shop.bluebooktle.frontend.service.CartService;
import shop.bluebooktle.frontend.service.CouponService;
import shop.bluebooktle.frontend.service.DeliveryRuleService;
import shop.bluebooktle.frontend.service.OrderService;
import shop.bluebooktle.frontend.service.PaymentsService;
import shop.bluebooktle.frontend.service.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

	private final PaymentsService paymentsService;
	private final UserService userService;
	private final AdminPackagingOptionService adminPackagingOptionService;
	private final DeliveryRuleService deliveryRuleService;
	private final OrderService orderService;
	private final BookService bookService;
	private final CartService cartService;
	private final CouponService couponService;

	@Value("${toss.client-key}")
	private String tossPaymentClientKey;

	@GetMapping("/create")
	public ModelAndView createPage(
		@RequestParam(required = false) Long bookId,
		@RequestParam(defaultValue = "1", required = false) Integer quantity,
		@RequestParam(required = false) List<Long> bookIds,
		@CookieValue(value = "GUEST_ID", required = false) String guestId,
		@CookieValue(value = "BB_AT", required = false) String token
	) {
		ModelAndView mav = new ModelAndView("order/create_form");

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

		DeliveryRuleResponse deliveryRule = deliveryRuleService.getDefaultDeliveryRule();
		mav.addObject("deliveryRule", deliveryRule);

		if (token != null) {
			UserWithAddressResponse user = userService.getUserWithAddresses();
			mav.addObject("user", user);
		}
		List<Long> bookIdsForCoupon = bookItems.stream()
			.map(BookCartOrderResponse::bookId)
			.toList();

		UsableUserCouponMapResponse coupons = couponService.getUsableCouponsForOrder(bookIdsForCoupon);
		mav.addObject("coupons", coupons);

		return mav;
	}

	@PostMapping("/create")
	public String createOrder(@ModelAttribute OrderCreateRequest request) {
		String orderKey = java.util.UUID.randomUUID().toString();
		OrderCreateRequest updatedRequest = request.toBuilder()
			.orderKey(orderKey)
			.build();
		Long orderId = orderService.createOrder(updatedRequest);
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
		@RequestParam Long amount,
		RedirectAttributes redirectAttributes
	) {

		PaymentConfirmRequest req = new PaymentConfirmRequest(paymentKey, orderId, amount);

		try {
			PaymentConfirmResponse resp = paymentsService.confirm(req);
			redirectAttributes.addFlashAttribute("orderData", resp);
			return "redirect:/order/complete";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("GlobalErrorMessage", "결제에 실패했습니다: " + e.getMessage());
			return "redirect:/mypage/orders";
		}
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
	public String orderFailPage(
		@RequestParam(name = "code", required = false) String code,
		@RequestParam(name = "message", required = false) String message,
		RedirectAttributes redirectAttributes
	) {

		if (message != null && !message.isBlank()) {
			redirectAttributes.addFlashAttribute("GlobalErrorTitle", code);
			redirectAttributes.addFlashAttribute("GlobalErrorMessage", message);
		} else {
			redirectAttributes.addFlashAttribute("GlobalErrorMessage", "알 수 없는 오류로 결제에 실패했습니다.");
		}
		return "redirect:mypage/orders";
	}

	private void validateGuestId(String guestId) {
		if (guestId == null || guestId.isBlank()) {
			log.warn("GUEST_ID 쿠키가 존재하지 않습니다.");
			throw new GuestUserNotFoundException("guestId가 존재하지 않습니다. 쿠키를 발급받아야 합니다.");
		}
	}
}