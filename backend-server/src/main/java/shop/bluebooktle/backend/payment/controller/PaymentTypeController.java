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

import jakarta.validation.Valid;
import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;
import shop.bluebooktle.backend.payment.service.PaymentTypeService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/payments/types")
public class PaymentTypeController {

	private final PaymentTypeService paymentTypeService;

	public PaymentTypeController(PaymentTypeService paymentTypeService) {
		this.paymentTypeService = paymentTypeService;
	}

	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<PaymentTypeResponse>>> getPaymentTypes(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		Page<PaymentTypeResponse> paymentTypes = paymentTypeService.getAll(pageable);
		PaginationData<PaymentTypeResponse> paginationData = new PaginationData<>(paymentTypes);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerPaymentType(
		@Valid @RequestBody PaymentTypeRequest request) {
		paymentTypeService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@PutMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> updatePaymentType(
		@PathVariable Long id,
		@Valid @RequestBody PaymentTypeRequest request) {
		paymentTypeService.update(id, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> deletePaymentType(@PathVariable Long id) {
		paymentTypeService.delete(id);
		return ResponseEntity.ok().body(JsendResponse.success());
	}
}
