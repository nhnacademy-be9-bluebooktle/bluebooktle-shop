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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.service.CouponTypeService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupon-type")
public class CouponTypeController {

	private final CouponTypeService couponTypeService;

	//쿠폰 정책 등록
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerCouponType(
		@RequestBody @Valid CouponTypeRegisterRequest request) {
		couponTypeService.registerCouponType(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	// 쿠폰 정책 전체 조회
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<CouponTypeResponse>>> findAllCouponTypeList(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		Page<CouponTypeResponse> couponTypePage = couponTypeService.getAllCouponTypeList(pageable);
		PaginationData<CouponTypeResponse> paginationData = new PaginationData<>(couponTypePage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}
}
