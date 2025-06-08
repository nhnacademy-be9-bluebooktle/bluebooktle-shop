package shop.bluebooktle.backend.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.service.PaymentService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.payment.request.PaymentConfirmRequest;
import shop.bluebooktle.common.dto.payment.response.PaymentConfirmResponse;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
	private final PaymentService paymentService;

	@PostMapping("/{gatewayName}/confirm")
	public ResponseEntity<JsendResponse<PaymentConfirmResponse>> confirmPayment(
		@PathVariable String gatewayName,
		@Valid @RequestBody PaymentConfirmRequest request) throws Exception {
		paymentService.confirmPayment(request, gatewayName.toUpperCase());

		PaymentConfirmResponse responsePayload = new PaymentConfirmResponse(
			"SUCCESS",
			request.orderId(),
			request.amount().intValue()
		);
		return ResponseEntity.ok(JsendResponse.success(responsePayload));
	}
}
