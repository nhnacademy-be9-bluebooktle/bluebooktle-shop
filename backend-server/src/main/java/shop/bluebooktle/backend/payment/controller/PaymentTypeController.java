package shop.bluebooktle.backend.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;
import shop.bluebooktle.backend.payment.service.PaymentTypeService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/payments/types")
@Tag(name = "결제 수단 API", description = "결제 수단 조회 및 관리 API")
public class PaymentTypeController {

	private final PaymentTypeService paymentTypeService;

	public PaymentTypeController(PaymentTypeService paymentTypeService) {
		this.paymentTypeService = paymentTypeService;
	}

	@Operation(summary = "결제 수단 조회", description = "결제 수단을 조회합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<PaymentTypeResponse>>> getPaymentTypes(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		Page<PaymentTypeResponse> paymentTypes = paymentTypeService.getAll(pageable);
		PaginationData<PaymentTypeResponse> paginationData = new PaginationData<>(paymentTypes);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "결제 수단 등록", description = "결제 수단을 등록합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerPaymentType(
		@Valid @RequestBody PaymentTypeRequest request) {
		paymentTypeService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "결제 수단 수정", description = "결제 수단을 수정합니다.")
	@PutMapping("/{payment-type-id}")
	public ResponseEntity<JsendResponse<Void>> updatePaymentType(
		@PathVariable(name = "payment-type-id") Long paymentTypeId,
		@Valid @RequestBody PaymentTypeRequest request) {
		paymentTypeService.update(paymentTypeId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "결제 수단 삭제", description = "결제 수단을 삭제합니다.")
	@DeleteMapping("/{payment-type-id}")
	public ResponseEntity<JsendResponse<Void>> deletePaymentType(
		@PathVariable(name = "payment-type-id") Long paymentTypeId
	) {
		paymentTypeService.delete(paymentTypeId);
		return ResponseEntity.ok().body(JsendResponse.success());
	}
	
}
