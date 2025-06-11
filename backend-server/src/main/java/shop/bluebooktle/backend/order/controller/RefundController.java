package shop.bluebooktle.backend.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.service.RefundService;
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.refund.request.RefundCreateRequest;
import shop.bluebooktle.common.dto.refund.request.RefundUpdateRequest;
import shop.bluebooktle.common.security.Auth;
import shop.bluebooktle.common.security.UserPrincipal;

@Slf4j
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
@Tag(name = "환불 API", description = "환불 요청 및 처리 관련 API")
public class RefundController {

	private final RefundService refundService;

	@PostMapping("/request")
	@Auth(type = UserType.USER)
	@Operation(summary = "사용자 환불 요청", description = "사용자가 주문에 대한 환불을 요청합니다. 자신의 주문에 대해서만 요청할 수 있습니다.")
	public ResponseEntity<JsendResponse<Void>> requestRefund(
		@Valid @RequestBody RefundCreateRequest request,
		@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		Long userId = userPrincipal == null ? null : userPrincipal.getUserId();
		refundService.requestRefund(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@PatchMapping("/update")
	@Auth(type = UserType.ADMIN)
	@Operation(summary = "관리자 환불 처리", description = "관리자가 접수된 환불 요청의 상태를 업데이트합니다.")
	public ResponseEntity<JsendResponse<Void>> updateRefundStatus(
		@Valid @RequestBody RefundUpdateRequest request
	) {
		refundService.updateRefund(request);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
