package shop.bluebooktle.backend.payment.controller;

import static shop.bluebooktle.common.dto.common.JsendResponse.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.dto.request.PaymentRequest;
import shop.bluebooktle.backend.payment.entity.Payment;
import shop.bluebooktle.backend.payment.service.PaymentService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 관리 API")
@Validated
public class PaymentController {

	private final PaymentService paymentService;

	@Operation(summary = "결제 생성", description = "결제를 생성합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Long>> create(@RequestBody @Valid PaymentRequest req) {
		Payment entity = Payment.builder()
			// TODO 필수 연관객체(order)는 Service 쪽에서 찾아서 주입하도록 위임
			.pointAmount(req.pointAmount())
			.originalAmount(req.originalAmount())
			.finalAmount(req.finalAmount())
			.build();

		Long id = paymentService.create(entity);
		return ResponseEntity.status(HttpStatus.CREATED).body(success(id));
	}

	@Operation(summary = "결제 단건 조회", description = "결제를 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<JsendResponse<Payment>> get(@PathVariable Long id) {
		Payment payment = paymentService.get(id);
		return ResponseEntity.ok(success(payment));
	}
}
