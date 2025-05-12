package shop.bluebooktle.backend.coupon.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.service.CouponService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponRegisterRequest;
import shop.bluebooktle.common.dto.coupon.request.CouponUpdateRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CouponController {

	private final CouponService couponService;

	@PostMapping("/admin/coupons")
	public ResponseEntity<JsendResponse<Void>> registerCoupon(@RequestBody CouponRegisterRequest request) {
		couponService.registerCoupon(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@GetMapping("/admin/coupons")
	public ResponseEntity<JsendResponse<PaginationData<CouponResponse>>> getAllCoupons(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {

		Page<CouponResponse> couponPage = couponService.getAllCoupons(pageable);
		PaginationData<CouponResponse> paginationData = new PaginationData<>(couponPage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@PatchMapping("/admin/coupon/{id}")
	public ResponseEntity<JsendResponse<Void>> updateCoupon(@PathVariable Long id,
		@Valid @RequestBody CouponUpdateRequest request) {
		couponService.updateCoupon(id, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@DeleteMapping("/admin/coupon/{id}")
	public ResponseEntity<JsendResponse<Void>> deleteCoupon(@PathVariable Long id) {
		couponService.deleteCoupon(id);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
