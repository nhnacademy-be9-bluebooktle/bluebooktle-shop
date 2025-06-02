package shop.bluebooktle.backend.order.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.service.DeliveryRuleService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;

@RestController
@RequestMapping("/api/admin/delivery-rules")
@RequiredArgsConstructor
public class AdminDeliveryRuleController {

	private final DeliveryRuleService service;

	@GetMapping("/default")
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getDefault() {
		DeliveryRuleResponse dto = service.getDefaultRule();
		return ResponseEntity.ok(JsendResponse.success(dto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getById(@PathVariable Long id) {
		DeliveryRuleResponse dto = service.getRule(id);
		return ResponseEntity.ok(JsendResponse.success(dto));
	}

	@GetMapping
	public ResponseEntity<JsendResponse<PaginationData<DeliveryRuleResponse>>> getAll(Pageable pageable) {
		Page<DeliveryRuleResponse> page = service.getAll(pageable);
		PaginationData<DeliveryRuleResponse> paginationData = new PaginationData<>(page);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@GetMapping("/active")
	public ResponseEntity<JsendResponse<List<DeliveryRuleResponse>>> getActive() {
		List<DeliveryRuleResponse> deliveryRuleResponses = service.getAllByIsActive();
		return ResponseEntity.ok(JsendResponse.success(deliveryRuleResponses));
	}

	@PostMapping
	public ResponseEntity<JsendResponse<Long>> create(@RequestBody DeliveryRuleCreateRequest request) {
		Long id = service.createRule(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> update(
		@PathVariable Long id,
		@RequestBody DeliveryRuleUpdateRequest request
	) {
		service.updateRule(id, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<JsendResponse<Void>> delete(@PathVariable Long id) {
		service.deleteRule(id);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
