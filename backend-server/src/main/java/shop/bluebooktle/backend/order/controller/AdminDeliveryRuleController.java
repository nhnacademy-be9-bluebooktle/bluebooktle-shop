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
import shop.bluebooktle.common.domain.auth.UserType;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleCreateRequest;
import shop.bluebooktle.common.dto.order.request.DeliveryRuleUpdateRequest;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.common.security.Auth;

@RestController
@RequestMapping("/api/admin/delivery-rules")
@RequiredArgsConstructor
public class AdminDeliveryRuleController {

	private final DeliveryRuleService service;

	@GetMapping("/default")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getDefault() {
		DeliveryRuleResponse dto = service.getDefaultRule();
		return ResponseEntity.ok(JsendResponse.success(dto));
	}

	@GetMapping("/{delivery-rule-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getById(@PathVariable(name = "delivery-rule-id") Long deliveryRuleId) {
		DeliveryRuleResponse dto = service.getRule(deliveryRuleId);
		return ResponseEntity.ok(JsendResponse.success(dto));
	}

	@GetMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PaginationData<DeliveryRuleResponse>>> getAll(Pageable pageable) {
		Page<DeliveryRuleResponse> page = service.getAll(pageable);
		PaginationData<DeliveryRuleResponse> paginationData = new PaginationData<>(page);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@GetMapping("/active")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<List<DeliveryRuleResponse>>> getActive() {
		List<DeliveryRuleResponse> deliveryRuleResponses = service.getAllByIsActive();
		return ResponseEntity.ok(JsendResponse.success(deliveryRuleResponses));
	}

	@PostMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Long>> create(@RequestBody DeliveryRuleCreateRequest request) {
		Long id = service.createRule(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(id));
	}

	@PutMapping("/{delivery-rule-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> update(
		@PathVariable(name = "delivery-rule-id") Long deliveryRuleId,
		@RequestBody DeliveryRuleUpdateRequest request
	) {
		service.updateRule(deliveryRuleId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@DeleteMapping("/{delivery-rule-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> delete(
		@PathVariable(name = "delivery-rule-id") Long deliveryRuleId
	) {
		service.deleteRule(deliveryRuleId);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
