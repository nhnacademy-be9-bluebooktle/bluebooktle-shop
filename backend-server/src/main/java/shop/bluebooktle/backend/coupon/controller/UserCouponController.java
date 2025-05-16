package shop.bluebooktle.backend.coupon.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.batch.CouponBatchLauncher;
import shop.bluebooktle.backend.coupon.service.UserCouponService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-coupon")
public class UserCouponController {

	private final CouponBatchLauncher couponBatchLauncher;
	private final UserCouponService userCouponService;

	// TODO [쿠폰] 쿠폰 발급
	@PostMapping("/admin")
	public ResponseEntity<JsendResponse<Void>> registerUserCoupon(
		@RequestBody @Valid UserCouponRegisterRequest request) {
		couponBatchLauncher.run(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// TODO [쿠폰] 유저 별 쿠폰 전체 조회

	// TODO [쿠폰] 유저 별 사용 가능한 쿠폰 전체 조회

	// TODO [쿠폰] 사용

	// TODO [쿠폰] 삭제

}
