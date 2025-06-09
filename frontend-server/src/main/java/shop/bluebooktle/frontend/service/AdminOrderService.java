package shop.bluebooktle.frontend.service;

import org.springframework.data.domain.Pageable;

import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.AdminOrderSearchRequest;
import shop.bluebooktle.common.dto.order.request.AdminOrderStatusUpdateRequest;
import shop.bluebooktle.common.dto.order.response.AdminOrderDetailResponse;
import shop.bluebooktle.common.dto.order.response.AdminOrderListResponse;

public interface AdminOrderService {
	PaginationData<AdminOrderListResponse> searchOrders(AdminOrderSearchRequest request, Pageable pageable);

	AdminOrderDetailResponse getOrderDetail(Long orderId);

	void updateOrderStatus(Long orderId, AdminOrderStatusUpdateRequest status);
}
