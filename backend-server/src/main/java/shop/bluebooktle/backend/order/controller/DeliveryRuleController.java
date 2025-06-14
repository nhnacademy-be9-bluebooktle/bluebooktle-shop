package shop.bluebooktle.backend.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.bluebooktle.backend.order.service.DeliveryRuleService;
import shop.bluebooktle.common.dto.common.JsendResponse;
import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;

@Slf4j
@RestController
@RequestMapping("/api/delivery-rules")
@RequiredArgsConstructor
@Tag(name = "배송 정책 API", description = "배송 정책 조회 API")
public class DeliveryRuleController {
	private final DeliveryRuleService deliveryRuleService;

	@GetMapping
	@Operation(summary = "활성 배송 정책 조회", description = "활성화된 모든 배송 정책을 조회합니다.")
	public ResponseEntity<JsendResponse<List<DeliveryRuleResponse>>> getDeliveryRules() {
		List<DeliveryRuleResponse> rules = deliveryRuleService.getAllByIsActive();
		log.info("getDeliveryRules : {}", rules);
		return ResponseEntity.ok(JsendResponse.success(rules));
	}

	@GetMapping("/default")
	@Operation(summary = "기본 배송 정책 조회", description = "기본 배송 정책을 조회합니다.")
	public ResponseEntity<JsendResponse<DeliveryRuleResponse>> getDefaultDeliveryRule() {
		return ResponseEntity.ok(JsendResponse.success(deliveryRuleService.getDefaultRule()));
	}
}