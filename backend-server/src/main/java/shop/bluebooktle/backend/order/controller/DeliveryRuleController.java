package shop.bluebooktle.backend.order.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.bluebooktle.backend.order.dto.request.DeliveryRuleRequest;
import shop.bluebooktle.backend.order.dto.response.DeliveryRuleResponse;
import shop.bluebooktle.backend.order.entity.DeliveryRule;
import shop.bluebooktle.backend.order.service.DeliveryRuleService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeliveryRuleController {

	private final DeliveryRuleService deliveryRuleService;

	@PostMapping("/admin/delivery-rules")
	public ResponseEntity<?> createDeliveryPolicy(@RequestBody DeliveryRuleRequest request) {
		DeliveryRule rule = deliveryRuleService.createPolicy(
			request.name(),
			request.price(),
			request.deliveryFee()
		);
		return ResponseEntity.ok(rule.getId());
	}

	@GetMapping("/admin/delivery-rules")
	public ResponseEntity<?> getRule(@RequestParam String name) {
		DeliveryRule rule = deliveryRuleService.getRule(name);
		return ResponseEntity.ok(DeliveryRuleResponse.from(rule));
	}

	// 전체 조회
	@GetMapping("/admin/delivery-rules/all")
	public ResponseEntity<?> getAllRules() {
		List<DeliveryRuleResponse> rules = deliveryRuleService.getAll().stream()
			.map(DeliveryRuleResponse::from)
			.collect(Collectors.toList());
		return ResponseEntity.ok(rules);
	}

	@DeleteMapping("/admin/delivery-rules/{id}")
	public ResponseEntity<?> deleteRule(@PathVariable Long id) {
		deliveryRuleService.deletePolicy(id);
		return ResponseEntity.noContent().build();
	}

}
