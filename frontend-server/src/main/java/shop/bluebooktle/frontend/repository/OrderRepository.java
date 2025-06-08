package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "OrderRepository", path = "/api/orders", configuration = FeignGlobalConfig.class)
public interface OrderRepository {
	@GetMapping("/{orderId}")
	OrderConfirmDetailResponse getOrderConfirmDetail(
		@PathVariable Long orderId);

	@PostMapping
	Long createOrder(@RequestBody OrderCreateRequest request);

	@GetMapping("/history")
	PaginationData<OrderHistoryResponse> getOrderHistory(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "status", required = false) OrderStatus status
	);

	@GetMapping("/key/{orderKey}")
	OrderConfirmDetailResponse getOrderByKey(@PathVariable("orderKey") String orderKey);

}
