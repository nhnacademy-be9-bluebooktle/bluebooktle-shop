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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자 배송 정책 관리 API", description = "관리자 페이지에서 배송 정책을 관리합니다.")
public class AdminDeliveryRuleController {

	private final DeliveryRuleService service;

	@Operation(summary = "기본 배송 정책 조회", description = "관리자 페이지에서 기본 배송 정책을 조회합니다.")
	@GetMapping("/default")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getDefault() {
		DeliveryRuleResponse dto = service.getDefaultRule();
		return ResponseEntity.ok(JsendResponse.success(dto));
	}

	@Operation(summary = "배송 정책 조회", description = "관리자 페이지에서 배송 정책을 조회합니다.")
	@GetMapping("/{delivery-rule-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getById(@PathVariable(name = "delivery-rule-id") Long deliveryRuleId) {
		DeliveryRuleResponse dto = service.getRule(deliveryRuleId);
		return ResponseEntity.ok(JsendResponse.success(dto));
	}

	@Operation(summary = "배송 정책 목록 조회", description = "관리자 페이지에서 배송 정책 목록을 조회합니다.")
	@GetMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<PaginationData<DeliveryRuleResponse>>> getAll(Pageable pageable) {
		Page<DeliveryRuleResponse> page = service.getAll(pageable);
		PaginationData<DeliveryRuleResponse> paginationData = new PaginationData<>(page);
		return ResponseEntity.ok(JsendResponse.success(paginationData));
	}

	@Operation(summary = "활성 배송 정책 조회", description = "관리자 페이지에서 활성 배송 정책을 조회합니다.")
	@GetMapping("/active")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<List<DeliveryRuleResponse>>> getActive() {
		List<DeliveryRuleResponse> deliveryRuleResponses = service.getAllByIsActive();
		return ResponseEntity.ok(JsendResponse.success(deliveryRuleResponses));
	}

	@Operation(summary = "배송 정책 등록", description = "관리자 페이지에서 배송 정책을 등록합니다.")
	@PostMapping
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Long>> create(@RequestBody DeliveryRuleCreateRequest request) {
		Long id = service.createRule(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(JsendResponse.success(id));
	}

	@Operation(summary = "배송 정책 수정", description = "관리자 페이지에서 배송 정책을 수정합니다.")
	@PutMapping("/{delivery-rule-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> update(
		@PathVariable(name = "delivery-rule-id") Long deliveryRuleId,
		@RequestBody DeliveryRuleUpdateRequest request
	) {
		service.updateRule(deliveryRuleId, request);
		return ResponseEntity.ok(JsendResponse.success());
	}

	@Operation(summary = "배송 정책 삭제", description = "관리자 페이지에서 배송 정책을 삭제합니다.")
	@DeleteMapping("/{delivery-rule-id}")
	@Auth(type = UserType.ADMIN)
	public ResponseEntity<JsendResponse<Void>> delete(
		@PathVariable(name = "delivery-rule-id") Long deliveryRuleId
	) {
		service.deleteRule(deliveryRuleId);
		return ResponseEntity.ok(JsendResponse.success());
	}
}
