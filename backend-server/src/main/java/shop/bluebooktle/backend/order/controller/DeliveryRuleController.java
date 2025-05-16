package shop.bluebooktle.backend.order.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.dto.request.DeliveryRuleRequest;
import shop.bluebooktle.backend.order.dto.response.DeliveryRuleResponse;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.service.DeliveryRuleService;
import shop.bluebooktle.common.dto.common.JsendResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeliveryRuleController {

	private final DeliveryRuleService deliveryRuleService;

	@PostMapping("/admin/delivery-rules")
	public ResponseEntity<JsendResponse<Long>> createDeliveryPolicy(@RequestBody DeliveryRuleRequest request) {
		DeliveryRule rule = deliveryRuleService.createPolicy(
			request.name(),
			request.price(),
			request.deliveryFee()
		);
		URI location = URI.create("/admin/delivery-rules/" + rule.getId());
		return ResponseEntity
			.created(location) // 201 Created + Location header
			.body(JsendResponse.success(rule.getId()));
	}

	@GetMapping("/admin/delivery-rules/{id}")
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getRule(@PathVariable Long id) {
		DeliveryRule rule = deliveryRuleService.getRule(id);
		return ResponseEntity.ok(JsendResponse.success(DeliveryRuleResponse.from(rule)));
	}

	// 전체 조회
	@GetMapping("/admin/delivery-rules/all")
	public ResponseEntity<JsendResponse<List<DeliveryRuleResponse>>> getAllRules() {
		List<DeliveryRuleResponse> rules = deliveryRuleService.getAll().stream()
			.map(DeliveryRuleResponse::from)
			.collect(Collectors.toList());
		return ResponseEntity.ok(JsendResponse.success(rules));
	}

	@DeleteMapping("/admin/delivery-rules/{id}")
	public ResponseEntity<JsendResponse<Void>> deleteRule(@PathVariable Long id) {
		deliveryRuleService.deletePolicy(id);
		return ResponseEntity.ok(JsendResponse.success());
	}

}
