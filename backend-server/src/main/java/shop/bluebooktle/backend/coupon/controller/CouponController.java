package shop.bluebooktle.backend.coupon.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.batch.direct.DirectCouponBatchLauncher;
import shop.bluebooktle.backend.coupon.service.CouponService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.UserCouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
@Tag(name = "관리자 쿠폰 API", description = "관리자 쿠폰 CRUD API")
public class CouponController {

	private final CouponService couponService;
	private final DirectCouponBatchLauncher directCouponBatchLauncher;

	@Operation(summary = "쿠폰 등록", description = "새로운 쿠폰을 등록합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerCoupon(@Valid @RequestBody CouponRegisterRequest request) {
		couponService.registerCoupon(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "쿠폰 전체 조회", description = "등록된 쿠폰 전체를 조회합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<CouponResponse>>> getAllCoupons(
		@RequestParam(value = "searchCouponName", required = false) String searchCouponName,
		@PageableDefault(size = 10) Pageable pageable) {
		Page<CouponResponse> couponPage = couponService.getAllCoupons(searchCouponName, pageable);
		PaginationData<CouponResponse> paginationData = new PaginationData<>(couponPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	// 쿠폰 발급
	@Operation(summary = "쿠폰 직접 발급", description = "쿠폰을 직접 발급합니다.")
	@PostMapping("/issue")
	public ResponseEntity<JsendResponse<Void>> registerUserCoupon(
		@RequestBody @Valid UserCouponRegisterRequest request) {
		directCouponBatchLauncher.run(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}
}
