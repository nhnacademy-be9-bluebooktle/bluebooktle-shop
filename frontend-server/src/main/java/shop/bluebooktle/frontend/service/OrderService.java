package shop.bluebooktle.frontend.service;

import shop.bluebooktle.common.domain.order.OrderStatus;
import shop.bluebooktle.common.dto.common.PaginationData;
import shop.bluebooktle.common.dto.order.request.OrderCreateRequest;
import shop.bluebooktle.common.dto.order.response.OrderConfirmDetailResponse;
import shop.bluebooktle.common.dto.order.response.OrderHistoryResponse;

public interface OrderService {
	OrderConfirmDetailResponse getOrderConfirmDetail(Long orderId);

	Long createOrder(OrderCreateRequest orderCreateRequest);

	PaginationData<OrderHistoryResponse> getOrderHistory(int page, int size, OrderStatus status);
}
