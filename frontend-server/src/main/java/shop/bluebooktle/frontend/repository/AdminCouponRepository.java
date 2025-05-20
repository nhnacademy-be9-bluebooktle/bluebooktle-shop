package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponUpdateRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.frontend.config.FeignGlobalConfig;

@FeignClient(name = "backed-server", path = "/api/coupon/admin", contextId = "adminCouponRepository", configuration = FeignGlobalConfig.class)
public interface AdminCouponRepository {
	//쿠폰 정책 등록
	@PostMapping("/type")
	JsendResponse<Void> registerCouponType(@RequestBody CouponTypeRegisterRequest request);

	//쿠폰 정책 전체 조회
	@GetMapping("/type")
	JsendResponse<List<CouponTypeResponse>> getCouponType();

	//쿠폰 등록
	@PostMapping
	JsendResponse<Void> registerCoupon(@RequestBody CouponRegisterRequest request);

	//쿠폰 단일 조회
	@GetMapping("/{couponId}")
	JsendResponse<CouponResponse> getCoupon(@PathVariable("couponId") Long couponId);

	//쿠폰 수정
	@PatchMapping("/{couponId}")
	JsendResponse<Void> updateCoupon(@RequestBody CouponUpdateRequest request,
		@PathVariable("couponId") Long couponId);

	//쿠폰 삭제
	@DeleteMapping("/{couponId}")
	JsendResponse<Void> deleteCoupon(@PathVariable("couponId") Long couponId);

	//쿠폰 발급
	@PostMapping("/{couponId}/issue")
	JsendResponse<Void> issueCoupon(@PathVariable("couponId") Long couponId,
		@RequestBody UserCouponRegisterRequest request);
}
