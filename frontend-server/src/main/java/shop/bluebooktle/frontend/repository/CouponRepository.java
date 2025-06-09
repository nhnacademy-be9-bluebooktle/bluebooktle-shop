package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.domain.coupon.UserCouponFilterType;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UsableUserCouponMapResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", path = "/api/coupons", name = "userCouponRepository", configuration = FeignGlobalConfig.class)
public interface CouponRepository {

	//전체 유저 쿠폰 조회
	@GetMapping
	PaginationData<UserCouponResponse> getAllCoupons(
		@RequestParam("target") UserCouponFilterType filter,
		@SpringQueryMap Pageable pageable);

	//주문에 해당하는 쿠폰 조회
	@GetMapping("/usable-order")
	UsableUserCouponMapResponse getUsableCouponsForOrder(@RequestParam("bookIds") List<Long> bookIds);

	@GetMapping("/count/usable")
	Long countAllUsableCoupons();

	@GetMapping("/count/expiring")
	Long countExpiringThisMonth();

	@PostMapping("/{id}/use")
	void useCoupon(@PathVariable Long id);

	@PostMapping("/{id}/cancel")
	void cancelCoupon(@PathVariable Long id);
}
