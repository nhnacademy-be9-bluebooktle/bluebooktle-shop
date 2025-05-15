package shop.bluebooktle.backend.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.dto.request.PaymentDetailRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentDetailResponse;
import shop.bluebooktle.backend.payment.service.PaymentDetailService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/payments/details")
@RequiredArgsConstructor
public class PaymentDetailController {

	private final PaymentDetailService service;

	@PostMapping
	public ResponseEntity<JsendResponse<Void>> create(@RequestBody @Valid PaymentDetailRequest req) {
		service.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@GetMapping("/{id}")
	public ResponseEntity<JsendResponse<PaymentDetailResponse>> get(@PathVariable Long id) {
		PaymentDetailResponse dto = service.get(id);
		return ResponseEntity.ok(JsendResponse.success(dto));
	}
}
