package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", path = "/api/coupon", name = "couponRepository", configuration = FeignGlobalConfig.class)
public interface CouponRepository {

	//전체 유저 쿠폰 조회
	@GetMapping
	PaginationData<UserCouponResponse> getAllCoupon();

	//사용 가능한 쿠폰 조회
	@GetMapping("/usable")
	PaginationData<UserCouponResponse> getAllCouponUsable();

	//사용 완료 쿠폰 조회
	@GetMapping("/used")
	PaginationData<UserCouponResponse> getAllCouponUsed();

	//기간 만료 쿠폰 조회
	@GetMapping("/expired")
	PaginationData<UserCouponResponse> getExpiredCoupon();

	//유저 쿠폰 삭제
	@DeleteMapping("/{userCouponId}")
	void deleteUserCoupon(@PathVariable("userCouponId") Long userCouponId);
}
