package shop.bluebooktle.frontend.controller.myPage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.frontend.service.CouponService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/coupons")
public class MyPageCouponController {

	private final CouponService couponService;

	@GetMapping
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
}
