package shop.bluebooktle.frontend.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderStatusUpdateRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;
import shop.bluebooktle.frontend.config.feign.FeignGlobalConfig;

@FeignClient(url = "${server.gateway-url}", name = "AdminOrderRepository", path = "/api/admin/orders", configuration = FeignGlobalConfig.class)
public interface AdminOrderRepository {

	@GetMapping
	PaginationData<AdminOrderListResponse> searchOrders(@SpringQueryMap AdminOrderSearchRequest request,
		Pageable pageable);

	@GetMapping("/{orderId}")
	AdminOrderDetailResponse getOrderDetail(@PathVariable("orderId") Long orderId);

	@PostMapping("/{orderId}/update-status")
	void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody
	AdminOrderStatusUpdateRequest request);
}
