package shop.bluebooktle.backend.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.service.DeliveryRuleService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;

@Slf4j
@RestController
@RequestMapping("/api/delivery-rules")
@RequiredArgsConstructor
public class DeliveryRuleController {
	private final DeliveryRuleService deliveryRuleService;

	@GetMapping
	public ResponseEntity<JsendResponse<List<DeliveryRuleResponse>>> getDeliveryRules() {
		List<DeliveryRuleResponse> rules = deliveryRuleService.getAllByIsActive();
		log.info("getDeliveryRules : {}", rules);
		return ResponseEntity.ok(JsendResponse.success(rules));
	}

	@GetMapping("/default")
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getDefaultDeliveryRule() {
		return ResponseEntity.ok(JsendResponse.success(deliveryRuleService.getDefaultRule()));
	}
}
