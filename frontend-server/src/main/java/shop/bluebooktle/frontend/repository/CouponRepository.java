package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.coupon.response.UserCouponResponse;
import shop.bluebooktle.frontend.config.FeignGlobalConfig;

@FeignClient(name = "backed-server", path = "/api/coupon", contextId = "couponRepository", configuration = FeignGlobalConfig.class)
public interface CouponRepository {

	//전체 유저 쿠폰 조회
	@GetMapping
	JsendResponse<List<UserCouponResponse>> getAllCoupon();

	//사용 가능한 쿠폰 조회
	@GetMapping("/usable")
	JsendResponse<List<UserCouponResponse>> getAllCouponUsable();

	//사용 완료 쿠폰 조회
	@GetMapping("/used")
	JsendResponse<List<UserCouponResponse>> getAllCouponUsed();

	//기간 만료 쿠폰 조회
	@GetMapping("/expired")
	JsendResponse<List<UserCouponResponse>> getExpiredCoupon();

	//유저 쿠폰 삭제
	@DeleteMapping("/{userCouponId}")
	JsendResponse<Void> deleteUserCoupon(@PathVariable("userCouponId") Long userCouponId);
}
