package shop.bluebooktle.frontend.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.book.response.BookCartOrderResponse;
import shop.bluebooktle.common.dto.book_order.response.PackagingOptionInfoResponse;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.request.OrderItemRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.common.dto.user.response.UserWithAddressResponse;
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
@RefreshScope
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
		Model model
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

		Boolean isLoggedIn = (Boolean)model.getAttribute("isLoggedIn");

		log.info("isLoggedIn: {}", isLoggedIn);

		if (Boolean.TRUE.equals(isLoggedIn)) {
			UserWithAddressResponse user = userService.getUserWithAddresses();
			mav.addObject("user", user);
			List<Long> bookIdsForCoupon = bookItems.stream()
				.map(BookCartOrderResponse::bookId)
				.toList();
			UsableUserCouponMapResponse coupons = couponService.getUsableCouponsForOrder(bookIdsForCoupon);
			mav.addObject("coupons", coupons);
		} else {// 할 일 : else
			UsableUserCouponMapResponse emptyCoupons = new UsableUserCouponMapResponse();
			emptyCoupons.setUsableUserCouponMap(new HashMap<>());
			mav.addObject("coupons", emptyCoupons);
		}

		return mav;
	}

	@PostMapping("/create")
	public String createOrder(@ModelAttribute OrderCreateRequest request, RedirectAttributes ra,
		@CookieValue(value = "GUEST_ID", required = false) String guestId) {
		String orderKey = java.util.UUID.randomUUID().toString();
		OrderCreateRequest updatedRequest = request.toBuilder()
			.orderKey(orderKey)
			.build();
		try {
			Long orderId = orderService.createOrder(updatedRequest, guestId);
			log.info("주문 생성 :{}", orderId);
			return "redirect:/order/" + orderId + "/checkout";
		} catch (Exception e) {
			ra.addFlashAttribute("globalErrorMessage", e.getMessage());
			List<OrderItemRequest> items = request.orderItems();
			if (items.size() == 1) {
				OrderItemRequest item = items.getFirst();
				return "redirect:/order/create?bookId=%d&quantity=%d".formatted(item.bookId(), item.bookQuantity());
			} else {
				List<String> bookIdStrings = items.stream()
					.map(item -> String.valueOf(item.bookId()))
					.toList();
				String joinedBookIds = String.join(",", bookIdStrings);

				return "redirect:/order/create?bookIds=" + joinedBookIds;
			}
		}
	}

	@GetMapping("/{orderId}/checkout")
	public String finalConfirmationAndCheckoutPage(
		@PathVariable Long orderId,
		Model model,
		HttpServletRequest request
	) {
		OrderConfirmDetailResponse orderDetails = orderService.getOrderConfirmDetail(orderId);
		model.addAttribute("order", orderDetails);

		Boolean isLoggedIn = (Boolean)model.getAttribute("isLoggedIn");
		if (Boolean.TRUE.equals(isLoggedIn)) {
			UserWithAddressResponse currentUser = userService.getUserWithAddresses();
			model.addAttribute("currentUser", currentUser);
		}

		model.addAttribute("userPointBalance", orderDetails.getUserPointBalance());

		model.addAttribute("tossClientKey", tossPaymentClientKey);
		model.addAttribute("orderIdForPayment", orderDetails.getOrderId().toString());
		model.addAttribute("orderNameForPayment", orderDetails.getOrderName());

		model.addAttribute("customerNameForPayment", orderDetails.getOrdererName());

		String baseUrl = String.format("%s://%s:%d", request.getScheme(), request.getServerName(),
			request.getServerPort());
		model.addAttribute("tossSuccessUrl", baseUrl + "/order/toss/process");
		model.addAttribute("failUrl", baseUrl + "/order/toss/fail");

		model.addAttribute("pointSuccessUrl", baseUrl + "/order/point/process");

		model.addAttribute("initialSubTotal", orderDetails.getSubTotal());
		model.addAttribute("initialPackagingTotal", orderDetails.getPackagingTotal());
		model.addAttribute("initialCouponDiscountTotal", orderDetails.getCouponDiscountTotal());
		model.addAttribute("initialDeliveryFee", orderDetails.getDeliveryFee());

		return "order/checkout";
	}

	@GetMapping("/{paymentMethod}/process")
	public String processOrder(
		@PathVariable String paymentMethod,
		@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam Long amount,
		RedirectAttributes redirectAttributes,
		Model model
	) {
		if ("point".equalsIgnoreCase(paymentMethod)) {
			if (amount != 0) {
				redirectAttributes.addFlashAttribute("GlobalErrorTitle", "결제 오류");
				redirectAttributes.addFlashAttribute("GlobalErrorMessage", "포인트 전액 결제는 결제 금액이 0원이어야 합니다.");
				Boolean isLoggedIn = (Boolean)model.getAttribute("isLoggedIn");
				return Boolean.TRUE.equals(isLoggedIn) ? "redirect:/mypage/orders" : "redirect:/";
			}
		}

		PaymentConfirmRequest req = new PaymentConfirmRequest(paymentKey, orderId, amount);

		try {
			PaymentConfirmResponse resp = paymentsService.confirm(req, paymentMethod);
			redirectAttributes.addFlashAttribute("orderData", resp);
			return "redirect:/order/complete/" + resp.orderId();
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("GlobalErrorTitle", "주문 결제 실패");
			redirectAttributes.addFlashAttribute("GlobalErrorMessage", "결제에 실패했습니다: " + e.getMessage());
			Boolean isLoggedIn = (Boolean)model.getAttribute("isLoggedIn");
			if (Boolean.TRUE.equals(isLoggedIn)) {
				return "redirect:/mypage/orders";
			} else {
				return "redirect:/";
			}
		}
	}

	@GetMapping("/complete/{orderKey}")
	public String orderCompletePage(@PathVariable String orderKey, Model model, RedirectAttributes redirectAttributes
	) {
		try {
			OrderConfirmDetailResponse fullDetails = orderService.getOrderByKey(orderKey);

			model.addAttribute("orderKey", fullDetails.getOrderKey());
			model.addAttribute("totalAmount", fullDetails.getPaidAmount());
			model.addAttribute("paymentMethod", fullDetails.getPaymentMethod());
			model.addAttribute("fullAddress", fullDetails.getAddress() + " " + fullDetails.getDetailAddress());
			model.addAttribute("orderedItems", fullDetails.getOrderItems());
			if (fullDetails.getOrdererName() != null) {
				model.addAttribute("ordererNickname", fullDetails.getOrdererName());
			} else {
				model.addAttribute("ordererNickname", "비회원");
			}
			return "order/complete";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("GlobalErrorTitle", "주문 정보 조회 실패");
			redirectAttributes.addFlashAttribute("GlobalErrorMessage", "주문 정보를 조회할 수 없습니다.");
			Boolean isLoggedIn = (Boolean)model.getAttribute("isLoggedIn");
			if (Boolean.TRUE.equals(isLoggedIn)) {
				return "redirect:/mypage/orders";
			} else {
				return "redirect:/";
			}
		}
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

	@GetMapping("/{orderKey}")
	public ModelAndView orderDetailPage(@PathVariable String orderKey,
		RedirectAttributes redirectAttributes
	) {
		ModelAndView mav = new ModelAndView("mypage/order_detail");
		try {
			log.info("비회원 주문 조회");
			OrderDetailResponse orderDetails = orderService.getOrderDetailByOrderKey(orderKey);
			mav.addObject("order", orderDetails);
			mav.addObject("isMember", false);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("globalErrorMessage", e.getMessage());
			mav.setViewName("redirect:/");
		}
		return mav;
	}
}