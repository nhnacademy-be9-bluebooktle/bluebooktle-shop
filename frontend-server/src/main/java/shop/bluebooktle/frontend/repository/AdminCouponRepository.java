package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.frontend.config.FeignGlobalConfig;

@FeignClient(name = "backed-server", path = "/api/admin/coupons", contextId = "adminCouponRepository", configuration = FeignGlobalConfig.class)
public interface AdminCouponRepository {
	//쿠폰 정책 등록
	@PostMapping("/type")
	JsendResponse<Void> registerCouponType(@RequestBody CouponTypeRegisterRequest request);

	//쿠폰 정책 전체 조회
	@GetMapping("/type")
	JsendResponse<PaginationData<CouponTypeResponse>> getCouponType();

	//쿠폰 등록
	@PostMapping
	JsendResponse<Void> registerCoupon(@RequestBody CouponRegisterRequest request);

	//전체 쿠폰 조회
	@GetMapping
	JsendResponse<PaginationData<CouponResponse>> getAllCoupons();

	//쿠폰 발급
	@PostMapping("/issue")
	JsendResponse<Void> issueCoupon(@RequestBody UserCouponRegisterRequest request);
}
