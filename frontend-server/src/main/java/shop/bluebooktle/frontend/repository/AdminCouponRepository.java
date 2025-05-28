package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", path = "/api/admin/coupons", name = "adminCouponRepository", configuration = FeignGlobalConfig.class)
public interface AdminCouponRepository {
	//쿠폰 정책 등록
	@PostMapping("/type")
	void registerCouponType(@RequestBody CouponTypeRegisterRequest request);

	//쿠폰 정책 전체 조회
	@GetMapping("/type")
	PaginationData<CouponTypeResponse> getAllCouponType();

	//쿠폰 등록
	@PostMapping
	void registerCoupon(@RequestBody CouponRegisterRequest request);

	//전체 쿠폰 조회
	@GetMapping
	PaginationData<CouponResponse> getAllCoupon();

	//쿠폰 발급
	@PostMapping("/issue")
	void issueCoupon(@RequestBody UserCouponRegisterRequest request);
}
