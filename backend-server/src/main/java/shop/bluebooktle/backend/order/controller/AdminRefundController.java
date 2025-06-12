package shop.bluebooktle.backend.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.service.RefundService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.refund.request.RefundSearchRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.dto.refund.response.AdminRefundDetailResponse;
import shop.bluebooktle.common.dto.refund.response.RefundListResponse;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequestMapping("/api/admin/refunds")
@RequiredArgsConstructor
public class AdminRefundController {

	private final RefundService refundService;

	@GetMapping
	@Auth(type = UserType.ADMIN)
	@Operation(summary = "관리자 환불 내역 조회", description = "관리자가 접수된 환불 내역을 조회합니다.")
	public ResponseEntity<JsendResponse<PaginationData<RefundListResponse>>> getRefundList(
		@ModelAttribute RefundSearchRequest request,
		@PageableDefault(size = 10) Pageable pageable
	) {
		Page<RefundListResponse> refundList = refundService.getRefundList(request, pageable);
		PaginationData<RefundListResponse> paginationData = new PaginationData<>(refundList);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@GetMapping("/{refundId}")
	@Auth(type = UserType.ADMIN)
	@Operation(summary = "관리자 환불 상세 조회", description = "관리자가 환불 건의 상세 내역을 조회합니다.")
	public ResponseEntity<JsendResponse<AdminRefundDetailResponse>> getRefundDetail(@PathVariable Long refundId) {
		AdminRefundDetailResponse refundDetail = refundService.getAdminRefundDetail(refundId);
		return ResponseEntity.ok(JsendResponse.success(refundDetail));
	}

	@PostMapping("/update")
	@Auth(type = UserType.ADMIN)
	@Operation(summary = "관리자 환불 처리", description = "관리자가 접수된 환불 요청의 상태를 업데이트합니다.")
	public ResponseEntity<JsendResponse<Void>> updateRefundStatus(
		@Valid @RequestBody RefundUpdateRequest request
	) {
		refundService.updateRefund(request);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
