package shop.bluebooktle.frontend.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import shop.bluebooktle.common.dto.order.response.DeliveryRuleResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(
	url = "${server.gateway-url}",
	path = "/api/delivery-rules",
	name = "deliveryRuleRepository",
	configuration = FeignGlobalConfig.class
)
public interface DeliveryRuleRepository {

	@GetMapping
	List<DeliveryRuleResponse> getDeliveryRules();

	@GetMapping("/default")
	DeliveryRuleResponse getDefaultDeliveryRule();
}
