package shop.bluebooktle.backend.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.service.PaymentService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.payment.request.PaymentCancelRequest;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;
import shop.bluebooktle.common.security.UserPrincipal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "결제 API", description = "결제 확정 및 취소 API")
public class PaymentController {
	private final PaymentService paymentService;

	@Operation(summary = "결제 확정", description = "외부 서비스 결제 이후 결제 완료 데이터를 저장합니다. ")
	@PostMapping("/{gateway-name}/confirm")
	public ResponseEntity<JsendResponse<PaymentConfirmResponse>> confirmPayment(
		@PathVariable(name = "gateway-name") String gatewayName,
		@Valid @RequestBody PaymentConfirmRequest request) {
		paymentService.confirmPayment(request, gatewayName.toUpperCase());

		PaymentConfirmResponse responsePayload = new PaymentConfirmResponse(
			"SUCCESS",
			request.orderId(),
			request.amount().intValue()
		);
		return ResponseEntity.ok(JsendResponse.success(responsePayload));
	}

	@Operation(summary = "결제 취소", description = "결제를 취소합니다.")
	@PostMapping("/cancel")
	public ResponseEntity<JsendResponse<Void>> cancel(
		@Valid @RequestBody PaymentCancelRequest request,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		paymentService.cancelPayment(request, userPrincipal != null ? userPrincipal.getUserId() : null);

		return ResponseEntity.ok(JsendResponse.success());
	}
}
