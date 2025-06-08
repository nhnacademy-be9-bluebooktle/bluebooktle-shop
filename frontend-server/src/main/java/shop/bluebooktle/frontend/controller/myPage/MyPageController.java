package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.frontend.service.CouponService;
import shop.bluebooktle.frontend.service.OrderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

	private final CouponService couponService;
	private final OrderService orderService;

	@GetMapping
	public String myPageDefault() {
		return "redirect:/mypage/profile";
	}

	@GetMapping("/orders")
	public String userOrdersPage(
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "size", defaultValue = "20") int size,
		@RequestParam(value = "status", required = false) OrderStatus status,
		Model model
	) {
		PaginationData<OrderHistoryResponse> paginationData = orderService.getOrderHistory(page, size, status);

		model.addAttribute("ordersPage", paginationData);
		model.addAttribute("status", status);
		return "mypage/order_list";
	}

	@GetMapping("/orders/{orderId}")
	public String orderDetailPage(@PathVariable String orderId) {
		System.out.println("Requesting order detail for (Mock): " + orderId);
		return "mypage/order_detail";
	}

	@GetMapping("/coupons")
	public String userCouponsPage(
		@RequestParam(defaultValue = "ALL") String filter,
		@PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
		Model model,
		HttpServletRequest request
	) {
		model.addAttribute("currentURI", request.getRequestURI());

		PaginationData<UserCouponResponse> coupons = couponService.getAllCoupons(filter, pageable);
		model.addAttribute("page", coupons);
		model.addAttribute("coupons", coupons.getContent());
		model.addAttribute("selectedFilter", filter);

		Long usableCount = couponService.countAllUsableCoupons();
		Long expiringCount = couponService.countExpiringThisMonth();

		model.addAttribute("usableCouponCount", usableCount);
		model.addAttribute("expiringCouponCount", expiringCount);

		return "mypage/coupon_list";
	}

	@GetMapping("/reviews")
	public String userReviewsPage() {

		return "mypage/review_list";
	}
}
