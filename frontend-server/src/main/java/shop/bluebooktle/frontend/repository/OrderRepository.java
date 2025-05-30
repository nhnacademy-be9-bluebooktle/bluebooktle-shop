package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "OrderRepository", path = "/api/order", configuration = FeignGlobalConfig.class)
public interface OrderRepository {
	@GetMapping("/{orderId}/confirmation")
	OrderConfirmDetailResponse getOrderConfirmDetail(
		@PathVariable Long orderId);
}
