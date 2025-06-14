package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;
import shop.bluebooktle.frontend.config.retry.RetryWithTokenRefresh;

@FeignClient(url = "${server.gateway-url}", name = "OrderRepository", path = "/api/orders", configuration = FeignGlobalConfig.class)
public interface OrderRepository {
	@GetMapping("/{orderId}")
	@RetryWithTokenRefresh
	OrderConfirmDetailResponse getOrderConfirmDetail(
		@PathVariable Long orderId);

	@PostMapping
	@RetryWithTokenRefresh
	Long createOrder(@RequestBody OrderCreateRequest request,
		@RequestHeader(name = "GUEST_ID", required = false) String guestId);

	@GetMapping("/history")
	PaginationData<OrderHistoryResponse> getOrderHistory(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@RequestParam(value = "status", required = false) OrderStatus status
	);

	@PostMapping("/{orderKey}/cancel")
	void cancelOrder(@PathVariable String orderKey);

	@GetMapping("/{orderKey}/detail")
	OrderDetailResponse getOrderDetailByOrderKey(@PathVariable String orderKey);

	@GetMapping("/key/{orderKey}")
	OrderConfirmDetailResponse getOrderByKey(@PathVariable("orderKey") String orderKey);

}
