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
import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.payment.dto.request.PaymentTypeRequest;
import shop.bluebooktle.backend.payment.dto.response.PaymentTypeResponse;
import shop.bluebooktle.backend.payment.service.PaymentTypeService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;

@RestController
@RequestMapping("/api/payments/types")
@RequiredArgsConstructor
@Tag(name = "PaymentType", description = "결제 타입 관리 API")
public class PaymentTypeController {

	private final PaymentTypeService paymentTypeService;

	@Operation(summary = "결제 타입 목록 조회", description = "페이징된 결제 타입 목록을 반환합니다.")
	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<PaymentTypeResponse>>> getPaymentTypes(
		@PageableDefault(size = 10, sort = "id") Pageable pageable) {
		Page<PaymentTypeResponse> paymentTypes = paymentTypeService.getAll(pageable);
		PaginationData<PaymentTypeResponse> paginationData = new PaginationData<>(paymentTypes);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "결제 타입 등록", description = "새로운 결제 타입을 등록합니다.")
	@PostMapping
	public ResponseEntity<JsendResponse<Void>> registerPaymentType(
		@Valid @RequestBody PaymentTypeRequest request) {
		paymentTypeService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success());
	}

	@Operation(summary = "결제 타입 수정", description = "기존 결제 타입 정보를 수정합니다.")
	@PutMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> updatePaymentType(
		@PathVariable Long id,
		@Valid @RequestBody PaymentTypeRequest request) {
		paymentTypeService.update(id, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "결제 타입 삭제", description = "지정한 ID의 결제 타입을 삭제(soft delete)합니다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> deletePaymentType(@PathVariable Long id) {
		paymentTypeService.delete(id);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
