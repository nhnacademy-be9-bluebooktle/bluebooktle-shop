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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.coupon.service.CouponTypeService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.CouponTypeRegisterRequest;
import shop.bluebooktle.common.dto.coupon.response.CouponTypeResponse;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons/type")
@Tag(name = "쿠폰 정책 API", description = "관리자 쿠폰 정책 CRUD 관련 API")
public class CouponTypeController {

	private final CouponTypeService couponTypeService;

	@Operation(summary = "쿠폰 정책 등록", description = "새로운 쿠폰 정책을 등록합니다.")
	@PostMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> registerCouponType(
		@RequestBody @Valid CouponTypeRegisterRequest request) {
		couponTypeService.registerCouponType(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "쿠폰 정책 조회", description = "등록된 쿠폰 정책을 조회합니다.")
	@GetMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PaginationData<CouponTypeResponse>>> findAllCouponTypeList(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		Page<CouponTypeResponse> couponTypePage = couponTypeService.getAllCouponTypeList(pageable);
		PaginationData<CouponTypeResponse> paginationData = new PaginationData<>(couponTypePage);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}
}
