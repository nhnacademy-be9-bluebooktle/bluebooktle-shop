package shop.bluebooktle.backend.coupon.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.coupon.service.FailedCouponIssueService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.coupon.request.FailedCouponIssueSearchRequest;
import shop.bluebooktle.common.dto.coupon.response.FailedCouponIssueResponse;
import shop.bluebooktle.common.security.Auth;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons/failed")
@Tag(name = "쿠폰 발급 실패 API", description = "발급 실패한 쿠폰 재처리 API")
public class FailedCouponIssueController {

	private final FailedCouponIssueService failedCouponIssueService;

	@Operation(summary = "발급 실패한 쿠폰 조회", description = "발급에 실패한 쿠폰을 조회합니다.")
	@GetMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PaginationData<FailedCouponIssueResponse>>> getFailedCoupons(
		FailedCouponIssueSearchRequest searchRequest,
		@PageableDefault(size = 10, sort = "createdAt") Pageable pageable
	) {
		Page<FailedCouponIssueResponse> result = failedCouponIssueService.getAllFailedCoupons(searchRequest, pageable);
		PaginationData<FailedCouponIssueResponse> paginationData = new PaginationData<>(result);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "발급 실패 쿠폰 단일 재발급", description = "발급 실패한 쿠폰을 단일 재발급 합니다.")
	@PostMapping("/{issue-id}/resend")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> resendSingle(@PathVariable("issue-id") Long issueId) {
		failedCouponIssueService.resend(issueId);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "발급 실패 쿠폰 전체 재발급", description = "발급 실패한 쿠폰을 전체 재발급 합니다.")
	@PostMapping("/resend-all")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> resendAll() {
		failedCouponIssueService.resendAll();
		return ResponseEntity.ok(JsendResponse.success());
	}
}
