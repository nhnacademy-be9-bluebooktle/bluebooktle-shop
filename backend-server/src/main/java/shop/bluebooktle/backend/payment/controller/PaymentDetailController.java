package shop.bluebooktle.backend.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import shop.bluebooktle.backend.payment.dto.request.PaymentDetailRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentDetailResponse;
import shop.bluebooktle.backend.payment.service.PaymentDetailService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api/payments/details")
@RequiredArgsConstructor
@Tag(name = "PaymentDetail", description = "결제 상세 관리 API")
public class PaymentDetailController {

	private final PaymentDetailService service;

	@Operation(summary = "결제 상세 생성", description = "결제 상세를 생성합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> create(@RequestBody @Valid PaymentDetailRequest req) {
		service.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "결제 상세 조회", description = "결제 상세를 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<JsendResponse<PaymentDetailResponse>> get(@PathVariable Long id) {
		PaymentDetailResponse dto = service.get(id);
		return ResponseEntity.ok(JsendResponse.success(dto));
	}
}
