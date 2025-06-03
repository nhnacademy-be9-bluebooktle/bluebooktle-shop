package shop.bluebooktle.frontend.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.frontend.service.CouponService;

@Slf4j
@Controller
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
	private final CouponService couponService;

	@GetMapping("/usable-order")
	public UsableUserCouponMapResponse getUsableCouponsForOrder(
		@RequestParam("bookIds") List<Long> bookIds
	) {
		return couponService.getUsableCouponsForOrder(bookIds);
	}

	@PostMapping("/{id}/use")
	public void useCoupon(@PathVariable Long id) {
		couponService.useCoupon(id);
	}

	@PostMapping("/{id}/cancel")
	public void cancelCoupon(@PathVariable Long id) {
		couponService.cancelCoupon(id);
	}
}